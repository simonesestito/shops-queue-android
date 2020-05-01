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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.databinding.OwnerCurrentCalledUserBinding;
import com.simonesestito.shopsqueue.databinding.OwnerFragmentBinding;
import com.simonesestito.shopsqueue.databinding.OwnerNextUsersQueueBinding;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.OwnerBookingsAdapter;
import com.simonesestito.shopsqueue.viewmodel.OwnerViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class OwnerFragment extends AbstractAppFragment<OwnerFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private OwnerViewModel ownerViewModel;
    private OwnerNextUsersQueueBinding queueBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

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
        getViewBinding().ownerQueueRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ownerViewModel = new ViewModelProvider(this, viewModelFactory).get(OwnerViewModel.class);

        ownerViewModel.getCurrentShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop == null) {
                showShopError();
            } else {
                updateShopInfo(shop);
            }
        });

        ownerViewModel.getCurrentCalledUser().observe(getViewLifecycleOwner(), event -> {
            if (event.isInProgress()) {
                getViewBinding().ownerQueueRefreshLayout.setRefreshing(true);
                getViewBinding().ownerCallNextUser.setEnabled(false);
                getViewBinding().ownerCurrentCalledUser.ownerLatestUserCalled.setVisibility(View.INVISIBLE);
            } else if (!event.isSuccessful()) {
                ErrorDialog.newInstance(getString(R.string.error_network_offline))
                        .show(getChildFragmentManager(), null);
                onOffline();
                event.handle();
            }

            onNewBooking(event.getData());
        });

        ownerViewModel.getQueue()
                .observe(getViewLifecycleOwner(), queue -> {
                    if (queue != null)
                        onNewQueue(queue);
                });
    }

    @Override
    protected void onOffline() {
        super.onOffline();
        getViewBinding().ownerCallNextUser.setEnabled(false);
    }

    @Override
    protected void onOnline() {
        super.onOnline();
        ownerViewModel.refreshBookings();
    }

    private void onNewQueue(List<Booking> bookings) {
        getViewBinding().ownerCallNextUser.setEnabled(true);
        getViewBinding().ownerQueueRefreshLayout.setRefreshing(false);

        OwnerBookingsAdapter adapter = (OwnerBookingsAdapter)
                Objects.requireNonNull(queueBinding.ownerNextUsers.getAdapter());
        adapter.updateDataSet(bookings);

        if (bookings.isEmpty()) {
            queueBinding.ownerNextUsers.setVisibility(View.GONE);
            queueBinding.ownerNextUsersEmpty.setVisibility(View.VISIBLE);
        } else {
            queueBinding.ownerNextUsers.setVisibility(View.VISIBLE);
            queueBinding.ownerNextUsersEmpty.setVisibility(View.GONE);
        }
    }

    private void updateShopInfo(Shop shop) {
        hideShopError();
        requireActivity().setTitle(shop.getName());
    }

    private void showShopError() {
        getViewBinding().ownerLayoutGroup.setVisibility(View.INVISIBLE);
        getViewBinding().ownerShopError.setVisibility(View.VISIBLE);
    }

    private void hideShopError() {
        getViewBinding().ownerLayoutGroup.setVisibility(View.VISIBLE);
        getViewBinding().ownerShopError.setVisibility(View.INVISIBLE);
    }

    private void onNewBooking(@Nullable Booking booking) {
        OwnerCurrentCalledUserBinding calledUserView = getViewBinding().ownerCurrentCalledUser;
        if (booking == null) {
            calledUserView.ownerLatestUserCalled.setVisibility(View.INVISIBLE);
            calledUserView.ownerNoLatestUserMessage.setVisibility(View.VISIBLE);
        } else {
            calledUserView.ownerLatestUserCalled.setVisibility(View.VISIBLE);
            calledUserView.ownerNoLatestUserMessage.setVisibility(View.GONE);

            calledUserView.ownerLatestUserCalledName.setText(booking.getUser().getName());
        }
    }
}
