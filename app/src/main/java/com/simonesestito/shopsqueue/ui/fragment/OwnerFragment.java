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

import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.databinding.OwnerCurrentCalledUserBinding;
import com.simonesestito.shopsqueue.databinding.OwnerFragmentBinding;
import com.simonesestito.shopsqueue.viewmodel.OwnerViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class OwnerFragment extends AbstractAppFragment<OwnerFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private OwnerViewModel ownerViewModel;

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
        getViewBinding().ownerCallNextUser.setOnClickListener(v -> {
            v.setEnabled(false);
            ownerViewModel.callNextUser();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ownerViewModel = new ViewModelProvider(this, viewModelFactory).get(OwnerViewModel.class);

        ownerViewModel.getCurrentShop().observe(getViewLifecycleOwner(), shop -> {
            getViewBinding().ownerShopLoading.hide();

            if (shop == null) {
                showShopError();
            } else {
                updateShopInfo(shop);
            }
        });

        ownerViewModel.getCurrentCalledUser()
                .observe(getViewLifecycleOwner(), this::onNewBooking);

        ownerViewModel.getQueue()
                .observe(getViewLifecycleOwner(), this::onNewQueue);
    }

    @Override
    protected void onOffline() {
        super.onOffline();
        getViewBinding().ownerCallNextUser.setEnabled(false);
    }

    @Override
    protected void onOnline() {
        super.onOnline();
        getViewBinding().ownerCallNextUser.setEnabled(true);
    }

    private void onNewQueue(List<Booking> bookings) {
        // TODO
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

    private void onNewBooking(Booking booking) {
        getViewBinding().ownerCallNextUser.setEnabled(true);
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
