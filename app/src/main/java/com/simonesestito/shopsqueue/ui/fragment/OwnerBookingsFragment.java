/*
 * Copyright 2020 Simone Sestito
 * This file is part of Shops Queue.
 *
 * Shops Queue is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shops Queue is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shops Queue.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.simonesestito.shopsqueue.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.api.dto.ShoppingList;
import com.simonesestito.shopsqueue.databinding.OwnerFragmentBinding;
import com.simonesestito.shopsqueue.databinding.OwnerNextUsersQueueBinding;
import com.simonesestito.shopsqueue.model.ShopOwnerDetails;
import com.simonesestito.shopsqueue.ui.dialog.ConfirmDialog;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.OwnerBookingsAdapter;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.livedata.Resource;
import com.simonesestito.shopsqueue.viewmodel.OwnerOrdersViewModel;
import com.simonesestito.shopsqueue.viewmodel.OwnerViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class OwnerBookingsFragment extends AbstractAppFragment<OwnerFragmentBinding> {
    private static final int REQUEST_CANCEL_ALL = 1;
    @Inject ViewModelFactory viewModelFactory;
    private OwnerViewModel ownerViewModel;
    private OwnerOrdersViewModel ownerOrdersViewModel;
    private OwnerNextUsersQueueBinding queueBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        ownerViewModel = new ViewModelProvider(this, viewModelFactory).get(OwnerViewModel.class);
        ownerOrdersViewModel = new ViewModelProvider(this, viewModelFactory).get(OwnerOrdersViewModel.class);
        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    protected OwnerFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return OwnerFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ViewBinding bug with included <merge> layouts
        queueBinding = OwnerNextUsersQueueBinding.bind(view);

        getViewBinding().ownerCallNextUser.
                setOnClickListener(v -> ownerViewModel.callNextUser());

        getViewBinding().ownerQueueRefreshLayout
                .setOnRefreshListener(() -> ownerViewModel.refreshBookings());

        queueBinding.ownerNextUsers.setAdapter(new OwnerBookingsAdapter());
        queueBinding.ownerCancelAll.setOnClickListener(v ->
                ConfirmDialog.showForResult(this, REQUEST_CANCEL_ALL,
                        getString(R.string.owner_cancel_all_message)));

        ownerViewModel.getShopData().observe(getViewLifecycleOwner(), event -> {
            if (event.isLoading()) {
                onProgress();
            } else if (event.isSuccessful()) {
                onData(Objects.requireNonNull(event.getData()));
            } else {
                getViewBinding().ownerQueueRefreshLayout.setRefreshing(false);
                if (event.hasToBeHandled()) {
                    ErrorDialog.newInstance(requireContext(), event.getError())
                            .show(getChildFragmentManager(), null);
                }
            }
        });

        ownerOrdersViewModel.getShoppingLists().observe(getViewLifecycleOwner(), event -> {
            if (event.isSuccessful() && event.getData() != null && event.getData().size() != 0) {
                requireActivity().invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CANCEL_ALL && resultCode == Activity.RESULT_OK)
            ownerViewModel.cancelAllBookings();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.owner_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setupShoppingListsMenuItem(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavDirections directions;
        switch (item.getItemId()) {
            case R.id.ownerProducts:
                directions = OwnerBookingsFragmentDirections.actionOwnerBookingsFragmentToOwnerProductsFragment();
                NavUtils.navigate(this, directions);
                return true;
            case R.id.ownerShoppingLists:
                directions = OwnerBookingsFragmentDirections.actionOwnerBookingsFragmentToOwnerOrdersFragment();
                NavUtils.navigate(this, directions);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onProgress() {
        getViewBinding().ownerQueueRefreshLayout.setRefreshing(true);
        getViewBinding().ownerCallNextUser.setEnabled(false);
        getViewBinding().ownerShopError.setVisibility(View.GONE);
        queueBinding.ownerNextUsersEmpty.setVisibility(View.GONE);
    }

    private void onData(ShopOwnerDetails data) {
        getViewBinding().ownerCallNextUser.setEnabled(true);
        getViewBinding().ownerQueueRefreshLayout.setRefreshing(false);
        requireActivity().setTitle(data.getShop().getName());

        Booking currentUser;
        List<Booking> nextUsers;
        if (data.getQueue().size() == 0) {
            currentUser = null;
            nextUsers = Collections.emptyList();
        } else {
            currentUser = data.getQueue().get(0);
            nextUsers = data.getQueue().subList(1, data.getQueue().size());
        }

        if (currentUser == null) {
            getViewBinding().ownerCurrentCalledUser.ownerLatestUserCalled.setVisibility(View.INVISIBLE);
            getViewBinding().ownerCurrentCalledUser.ownerNoLatestUserMessage.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().ownerCurrentCalledUser.ownerLatestUserCalled.setVisibility(View.VISIBLE);
            getViewBinding().ownerCurrentCalledUser.ownerNoLatestUserMessage.setVisibility(View.GONE);
            getViewBinding().ownerCurrentCalledUser.ownerLatestUserCalledName
                    .setText(currentUser.getUser().getName());
        }

        if (nextUsers.isEmpty()) {
            queueBinding.ownerNextUsersEmpty.setVisibility(View.VISIBLE);
        } else {
            queueBinding.ownerNextUsersEmpty.setVisibility(View.GONE);
        }
        OwnerBookingsAdapter adapter = (OwnerBookingsAdapter) queueBinding.ownerNextUsers.getAdapter();
        Objects.requireNonNull(adapter);
        adapter.updateDataSet(nextUsers);
    }

    private void setupShoppingListsMenuItem(Menu menu) {
        MenuItem badgeItem = menu.findItem(R.id.ownerShoppingLists);

        ImageView menuItemIcon = badgeItem.getActionView().findViewById(R.id.menuItemIcon);
        menuItemIcon.setImageDrawable(badgeItem.getIcon());
        menuItemIcon.setContentDescription(badgeItem.getTitle());

        TextView menuItemBadge = badgeItem.getActionView().findViewById(R.id.menuItemBadge);
        Resource<List<ShoppingList>> value = ownerOrdersViewModel.getShoppingLists().getValue();
        if (value == null || value.getData() == null) {
            menuItemBadge.setVisibility(View.GONE);
        } else {
            List<ShoppingList> shoppingLists = value.getData();
            int todoListsCount = 0; // Count how many shopping lists aren't ready yet
            for (ShoppingList shoppingList : shoppingLists) {
                if (!shoppingList.isReady())
                    todoListsCount++;
            }

            if (todoListsCount == 0) {
                // All shopping lists are ready to retire
                menuItemBadge.setVisibility(View.GONE);
            } else {
                // There are some shopping lists to prepare
                menuItemBadge.setVisibility(View.VISIBLE);
                menuItemBadge.setText(String.valueOf(todoListsCount));
            }
        }

        badgeItem.getActionView().setOnClickListener(v -> onOptionsItemSelected(badgeItem));
    }
}
