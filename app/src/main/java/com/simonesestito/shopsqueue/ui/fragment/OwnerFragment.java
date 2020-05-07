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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.databinding.OwnerFragmentBinding;
import com.simonesestito.shopsqueue.databinding.OwnerNextUsersQueueBinding;
import com.simonesestito.shopsqueue.model.ShopOwnerDetails;
import com.simonesestito.shopsqueue.ui.dialog.ConfirmDialog;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.OwnerBookingsAdapter;
import com.simonesestito.shopsqueue.viewmodel.OwnerViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class OwnerFragment extends AbstractAppFragment<OwnerFragmentBinding> {
    private static final int REQUEST_CANCEL_ALL = 1;
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
        queueBinding.ownerCancelAll.setOnClickListener(v ->
                ConfirmDialog.showForResult(this, REQUEST_CANCEL_ALL,
                        getString(R.string.owner_cancel_all_message)));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ownerViewModel = new ViewModelProvider(this, viewModelFactory).get(OwnerViewModel.class);

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CANCEL_ALL && resultCode == Activity.RESULT_OK)
            ownerViewModel.cancelAllBookings();
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

        Booking booking = data.getCurrentUser();
        if (booking == null) {
            getViewBinding().ownerCurrentCalledUser.ownerLatestUserCalled.setVisibility(View.INVISIBLE);
            getViewBinding().ownerCurrentCalledUser.ownerNoLatestUserMessage.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().ownerCurrentCalledUser.ownerLatestUserCalled.setVisibility(View.VISIBLE);
            getViewBinding().ownerCurrentCalledUser.ownerNoLatestUserMessage.setVisibility(View.GONE);
            getViewBinding().ownerCurrentCalledUser.ownerLatestUserCalledName
                    .setText(booking.getUser().getName());
        }

        List<Booking> queue = data.getQueue();
        if (queue.isEmpty()) {
            queueBinding.ownerNextUsersEmpty.setVisibility(View.VISIBLE);
        } else {
            queueBinding.ownerNextUsersEmpty.setVisibility(View.GONE);
        }
        OwnerBookingsAdapter adapter = (OwnerBookingsAdapter) queueBinding.ownerNextUsers.getAdapter();
        Objects.requireNonNull(adapter);
        adapter.updateDataSet(queue);
    }
}
