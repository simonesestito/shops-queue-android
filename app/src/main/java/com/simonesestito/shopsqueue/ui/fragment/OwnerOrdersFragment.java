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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.ShoppingList;
import com.simonesestito.shopsqueue.databinding.OwnerShoppingListsFragmentBinding;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.OwnerOrdersAdapter;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.viewmodel.OwnerOrdersViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class OwnerOrdersFragment extends AbstractAppFragment<OwnerShoppingListsFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private OwnerOrdersAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    protected OwnerShoppingListsFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return OwnerShoppingListsFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(OwnerOrdersViewModel.class)
                .getShoppingLists()
                .observe(getViewLifecycleOwner(), event -> {
                    getViewBinding().ownerOrdersLoading.setVisibility(
                            event.isLoading() ? View.VISIBLE : View.GONE
                    );

                    if (event.isFailed() && event.hasToBeHandled()) {
                        event.handle();
                        ErrorDialog.newInstance(requireContext(), event.getError())
                                .show(getChildFragmentManager(), null);
                    } else if (event.isSuccessful()) {
                        List<ShoppingList> lists = event.getData();
                        if (lists == null || lists.isEmpty()) {
                            getViewBinding().ownerOrdersEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            getViewBinding().ownerOrdersEmptyView.setVisibility(View.GONE);
                        }
                        adapter.updateDataSet(lists);
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new OwnerOrdersAdapter();
        getViewBinding().ownerOrdersList.setAdapter(adapter);

        adapter.setItemClickListener(shoppingList -> {
            NavDirections directions = OwnerOrdersFragmentDirections
                    .actionOwnerOrdersFragmentToOwnerOrdersBottomSheet(shoppingList);
            NavUtils.navigate(this, directions);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.owner_orders_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ownerProducts) {
            NavDirections directions = OwnerOrdersFragmentDirections.actionOwnerOrdersFragmentToOwnerProductsFragment();
            NavUtils.navigate(this, directions);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
