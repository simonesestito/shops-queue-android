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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.BookingWithCount;
import com.simonesestito.shopsqueue.databinding.UserFragmentBinding;
import com.simonesestito.shopsqueue.ui.MapboxHelper;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.UserBookingsAdapter;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.MapUtils;
import com.simonesestito.shopsqueue.viewmodel.UserMainViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class UserMainFragment extends AbstractAppFragment<UserFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private UserMainViewModel viewModel;
    private MapboxHelper mapboxHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(UserMainViewModel.class);
    }

    @Override
    protected UserFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return UserFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapboxHelper = new MapboxHelper(getViewBinding().userShopsMap, this);

        UserBookingsAdapter adapter = new UserBookingsAdapter();
        adapter.setMenuItemListener(((menuItem, booking) -> {
            viewModel.deleteBooking(booking.getId());
        }));
        getViewBinding().userBookingsBottomSheet.userBookingsList.setAdapter(adapter);

        viewModel.loadBookings();
        viewModel.getBookings().observe(getViewLifecycleOwner(), event -> {
            if (event.isLoading()) {
                getViewBinding().userBookingsBottomSheet.userBookingsLoading.setVisibility(View.VISIBLE);
                getViewBinding().userBookingsBottomSheet.userBookingsList.setVisibility(View.GONE);
                getViewBinding().userBookingsBottomSheet.userBookingsEmptyView.setVisibility(View.GONE);
                BottomSheetBehavior.from(getViewBinding().userBookingsBottomSheet.getRoot())
                        .setState(BottomSheetBehavior.STATE_EXPANDED);
                return;
            }

            getViewBinding().userBookingsBottomSheet.userBookingsLoading.setVisibility(View.GONE);

            if (event.isFailed()) {
                if (event.hasToBeHandled()) {
                    ErrorDialog.newInstance(requireContext(), event.getError())
                            .show(getChildFragmentManager(), null);
                    event.handle();
                }
                return;
            }

            List<BookingWithCount> bookings = Objects.requireNonNull(event.getData());
            adapter.updateDataSet(bookings);
            if (bookings.isEmpty()) {
                getViewBinding().userBookingsBottomSheet.userBookingsList.setVisibility(View.GONE);
                getViewBinding().userBookingsBottomSheet.userBookingsEmptyView.setVisibility(View.VISIBLE);
            } else {
                getViewBinding().userBookingsBottomSheet.userBookingsList.setVisibility(View.VISIBLE);
                getViewBinding().userBookingsBottomSheet.userBookingsEmptyView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (MapUtils.requestLocationPermission(this)) {
            showUserLocation();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.user_main_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.userLocation:
                showUserLocation();
                break;
            case R.id.userRefreshBookings:
                viewModel.loadBookings();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressWarnings("MissingPermission")
    private void showUserLocation() {
        MapUtils.getCurrentLocation(requireActivity(), location -> {
            Log.d("UserFragment Map", "User location detected");
            mapboxHelper.moveTo(location);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MapUtils.LOCATION_PERMISSION_REQUEST_CODE && ArrayUtils.all(grantResults, PackageManager.PERMISSION_GRANTED)) {
            showUserLocation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MapUtils.ENABLE_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            showUserLocation();
        }
    }
}
