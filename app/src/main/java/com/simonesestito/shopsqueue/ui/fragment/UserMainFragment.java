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
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.BookingWithCount;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.api.dto.ShopResult;
import com.simonesestito.shopsqueue.databinding.UserFragmentBinding;
import com.simonesestito.shopsqueue.model.ApiException;
import com.simonesestito.shopsqueue.model.HttpStatus;
import com.simonesestito.shopsqueue.ui.MapboxHelper;
import com.simonesestito.shopsqueue.ui.dialog.ConfirmDialog;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.UserBookingsAdapter;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.MapUtils;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.util.livedata.Resource;
import com.simonesestito.shopsqueue.viewmodel.UserMainViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

public class UserMainFragment extends AbstractAppFragment<UserFragmentBinding> {
    private static final String CANCEL_BOOKING_ID_KEY = "bookingId";
    private static final int CANCEL_BOOKING_REQUEST_CODE = 2;
    @Inject ViewModelFactory viewModelFactory;
    private UserMainViewModel viewModel;
    private MapboxHelper mapboxHelper;
    private boolean shouldFitAll = false;
    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior currentShopBottomSheet;
    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior userBookingsBottomSheet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(UserMainViewModel.class);
    }

    @NonNull
    @Override
    protected UserFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return UserFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Shop clickedShop = NavUtils.getFragmentResult(this, UserFavouriteShopsFragment.SELECTED_SHOP_KEY);
        if (clickedShop != null) {
            ShopResult shopResult = new ShopResult(clickedShop, true);
            onShopMarkerClicked(shopResult);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapboxHelper = new MapboxHelper(getViewBinding().userShopsMap, this);
        mapboxHelper.onMapMoved(latLng -> viewModel.loadNearShops(latLng.getLatitude(), latLng.getLongitude()));

        currentShopBottomSheet = BottomSheetBehavior.from(getViewBinding().currentShopBottomSheet.getRoot());
        userBookingsBottomSheet = BottomSheetBehavior.from(getViewBinding().userBookingsBottomSheet.getRoot());

        UserBookingsAdapter adapter = new UserBookingsAdapter();
        adapter.setMenuItemListener((menuItem, item) -> onAskCancelBooking(item.getId()));
        getViewBinding().userBookingsBottomSheet.userBookingsList.setAdapter(adapter);

        viewModel.getBookings().observe(getViewLifecycleOwner(), this::onBookingEvent);
        viewModel.getShops().observe(getViewLifecycleOwner(), this::onShopsEvent);

        MapUtils.listenLocation(this, this::onNewUserLocation);

        currentShopBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        NavUtils.onBackPressed(this, a -> {
            if (getViewBinding().shopSearchEditText.getText().toString().trim().length() > 0) {
                onUserRefreshMenuClicked();
                return true;
            }

            if (currentShopBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                    userBookingsBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                currentShopBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                userBookingsBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }

            return false;
        });

        getViewBinding().shopSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_SEARCH)
                return false;

            boolean isInputValid = FormValidators.isString(getViewBinding().shopSearchLayout);
            if (!isInputValid)
                return false;

            ViewUtils.hideKeyboard((EditText) v);

            String query = v.getText().toString().trim();
            mapboxHelper.clearMarkers();
            shouldFitAll = true;
            LatLng location = viewModel.getLastUserLocation();
            viewModel.searchShops(location.getLatitude(), location.getLongitude(), query);
            return true;
        });
    }

    private void cancelQuery() {
        getViewBinding().shopSearchEditText.setText("");
        viewModel.clearQuery();
        LatLng latLng = viewModel.getLastUserLocation();
        viewModel.loadNearShops(latLng.getLatitude(), latLng.getLongitude());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.user_main_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.userRefreshBookings:
                onUserRefreshMenuClicked();
                return true;
            case R.id.userFavouriteShops:
                onUserFavouritesMenuClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onUserRefreshMenuClicked() {
        cancelQuery();
        viewModel.loadBookings();
        mapboxHelper.moveTo(viewModel.getLastUserLocation());
        currentShopBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        userBookingsBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void onUserFavouritesMenuClicked() {
        NavUtils.navigate(this, UserMainFragmentDirections.actionUserMainFragmentToUserFavouriteShopsFragment());
    }

    private void onNewUserLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mapboxHelper.showUserLocation(latLng);
        viewModel.updateUserLocation(latLng.getLatitude(), latLng.getLongitude());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MapUtils.LOCATION_PERMISSION_REQUEST_CODE && ArrayUtils.all(grantResults, PackageManager.PERMISSION_GRANTED)) {
            MapUtils.listenLocation(this, this::onNewUserLocation);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case MapUtils.ENABLE_LOCATION_REQUEST_CODE:
                MapUtils.listenLocation(this, this::onNewUserLocation);
                break;
            case CANCEL_BOOKING_REQUEST_CODE:
                if (data == null)
                    break;
                int bookingId = data.getIntExtra(CANCEL_BOOKING_ID_KEY, -1);
                if (bookingId == -1)
                    break;
                viewModel.cancelBooking(bookingId);
                break;
        }
    }

    private void onAskCancelBooking(int bookingId) {
        Bundle args = new Bundle();
        args.putInt(CANCEL_BOOKING_ID_KEY, bookingId);
        ConfirmDialog.showForResult(this, CANCEL_BOOKING_REQUEST_CODE,
                getString(R.string.cancel_booking_confirm_message), args);
    }

    private void onBookingEvent(Resource<List<BookingWithCount>> event) {
        UserBookingsAdapter adapter = (UserBookingsAdapter) getViewBinding()
                .userBookingsBottomSheet
                .userBookingsList
                .getAdapter();
        Objects.requireNonNull(adapter);

        if (event.isLoading()) {
            getViewBinding().userBookingsBottomSheet.userBookingsLoading.setVisibility(View.VISIBLE);
            getViewBinding().userBookingsBottomSheet.userBookingsList.setVisibility(View.GONE);
            getViewBinding().userBookingsBottomSheet.userBookingsEmptyView.setVisibility(View.GONE);
            currentShopBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            userBookingsBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }

        getViewBinding().userBookingsBottomSheet.userBookingsLoading.setVisibility(View.GONE);

        if (event.isFailed()) {
            if (event.hasToBeHandled()) {
                event.handle();
                if (event.getError() instanceof ApiException &&
                        ((ApiException) event.getError()).getStatusCode() == HttpStatus.HTTP_CONFLICT) {
                    ErrorDialog.newInstance(getString(R.string.user_booking_already_present))
                            .show(getChildFragmentManager(), null);
                } else {
                    ErrorDialog.newInstance(requireContext(), event.getError())
                            .show(getChildFragmentManager(), null);
                }
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
    }

    private void onShopsEvent(Resource<Set<ShopResult>> event) {
        if (event.isSuccessful()) {
            Set<ShopResult> shops = Objects.requireNonNull(event.getData());
            if (shops.isEmpty()) {
                Toast.makeText(requireContext(), R.string.no_shops_found, Toast.LENGTH_LONG).show();
            }
            List<LatLng> latLngs = new LinkedList<>();
            for (ShopResult shop : shops) {
                LatLng latLng = new LatLng(shop.getLatitude(), shop.getLongitude());
                latLngs.add(latLng);
                mapboxHelper.addMarker(latLng, () -> {
                    onShopMarkerClicked(shop);
                });
            }

            if (shouldFitAll) {
                shouldFitAll = false;
                mapboxHelper.fitBounds(latLngs);
            }
        } else if (event.isFailed() && event.hasToBeHandled()) {
            event.handle();
            ErrorDialog.newInstance(requireContext(), event.getError())
                    .show(getChildFragmentManager(), null);
        }
    }

    private void onShopMarkerClicked(ShopResult shop) {
        userBookingsBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        currentShopBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

        getViewBinding().currentShopBottomSheet.currentShopName.setText(shop.getName());
        getViewBinding().currentShopBottomSheet.currentShopAddress.setText(shop.getAddress());

        LatLng shopLatLng = new LatLng(shop.getLatitude(), shop.getLongitude());
        mapboxHelper.moveTo(shopLatLng);
        double distance = shopLatLng.distanceTo(viewModel.getLastUserLocation()) / 1000;
        String displayDistance = getString(R.string.shop_distance_field, distance);
        getViewBinding().currentShopBottomSheet.currentShopDistance.setText(displayDistance);

        String queueCount = getResources().getQuantityString(
                R.plurals.shop_queue_count, shop.getCount(), shop.getCount());
        getViewBinding().currentShopBottomSheet.currentShopQueueCount.setText(queueCount);

        getViewBinding().currentShopBottomSheet.starButton.setSelected(shop.isFavourite());
        getViewBinding().currentShopBottomSheet.starButton.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            viewModel.setFavouriteShop(shop.getId(), v.isSelected());
        });

        adjustBookButton(shop);
    }

    private void adjustBookButton(Shop shop) {
        int bookingId = -1;
        Resource<List<BookingWithCount>> currentState = viewModel.getBookings().getValue();
        if (currentState != null && currentState.isSuccessful()) {
            List<BookingWithCount> currentBookings = currentState.getData();
            if (currentBookings != null) {
                for (BookingWithCount booking : currentBookings) {
                    if (booking.getShop().getId() == shop.getId()) {
                        bookingId = booking.getId();
                        break;
                    }
                }
            }
        }

        if (bookingId == -1) {
            getViewBinding().currentShopBottomSheet.currentShopBookButton.setVisibility(View.VISIBLE);
            getViewBinding().currentShopBottomSheet.shoppingButton.setVisibility(View.VISIBLE);
            getViewBinding().currentShopBottomSheet.currentShopCancelButton.setVisibility(View.GONE);
        } else {
            getViewBinding().currentShopBottomSheet.currentShopBookButton.setVisibility(View.GONE);
            getViewBinding().currentShopBottomSheet.shoppingButton.setVisibility(View.GONE);
            getViewBinding().currentShopBottomSheet.currentShopCancelButton.setVisibility(View.VISIBLE);
        }

        getViewBinding().currentShopBottomSheet.currentShopBookButton
                .setOnClickListener(v -> viewModel.book(shop.getId()));
        int finalBookingId = bookingId;
        getViewBinding().currentShopBottomSheet.currentShopCancelButton
                .setOnClickListener(v -> onAskCancelBooking(finalBookingId));

        // TODO On shopping button click
    }
}
