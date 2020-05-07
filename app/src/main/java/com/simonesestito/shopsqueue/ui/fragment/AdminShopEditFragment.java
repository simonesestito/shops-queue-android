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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.NewShop;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.databinding.AdminShopEditBinding;
import com.simonesestito.shopsqueue.di.module.ShopAdminDetails;
import com.simonesestito.shopsqueue.model.PickedLocation;
import com.simonesestito.shopsqueue.ui.MapboxHelper;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.dialog.PermissionDialog;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;
import com.simonesestito.shopsqueue.viewmodel.AdminShopEditViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class AdminShopEditFragment extends AdminEditFragment<ShopAdminDetails, AdminShopEditBinding> {
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final static String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    @Inject ViewModelFactory viewModelFactory;
    private AdminShopEditViewModel viewModel;
    private MapboxHelper mapboxHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(AdminShopEditViewModel.class);
    }

    @Override
    protected AdminShopEditBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AdminShopEditBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapboxHelper = new MapboxHelper(getViewBinding().map, this);
        mapboxHelper.setOnClickListener(l -> pickLocation());
        getViewBinding().mapEmptyView.setOnClickListener(v -> pickLocation());
        if (viewModel.pickedLocation != null) {
            updateLocation(viewModel.pickedLocation);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PickedLocation pickedLocation = NavUtils.getFragmentResult(this, LocationPickerFragment.PICKED_LOCATION_KEY);
        if (pickedLocation != null)
            updateLocation(pickedLocation);
    }

    private void updateLocation(@Nullable PickedLocation pickedLocation) {
        viewModel.pickedLocation = pickedLocation;

        if (pickedLocation == null) {
            getViewBinding().map.setVisibility(View.INVISIBLE);
            getViewBinding().mapEmptyView.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().map.setVisibility(View.VISIBLE);
            getViewBinding().mapEmptyView.setVisibility(View.GONE);

            LatLng newLocation = pickedLocation.toLatLng();
            mapboxHelper.moveTo(newLocation);
            mapboxHelper.showOrReplaceMarker(1, newLocation);
        }
    }

    @Override
    protected void showData(@Nullable ShopAdminDetails data) {
        super.showData(data);
        if (data != null) {
            Shop shop = data.getShop();
            requireActivity().setTitle(shop.getName());
            getViewBinding().nameInput.setText(shop.getName());

            PickedLocation pickedLocation = new PickedLocation(shop.getLatitude(), shop.getLongitude(), shop.getAddress());
            updateLocation(pickedLocation);

            // TODO Show owners (read-only)
        } else {
            updateLocation(null);
            requireActivity().setTitle(R.string.new_shop_title);
        }
    }

    private void pickLocation() {
        // Check location permission
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            NavDirections directions = AdminShopEditFragmentDirections
                    .adminShopEditPickLocation()
                    .setStartLocation(viewModel.pickedLocation);
            NavUtils.navigate(this, directions);
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), LOCATION_PERMISSION)) {
            PermissionDialog.showForResult(this, LOCATION_PERMISSION_REQUEST_CODE, LOCATION_PERMISSION);
        } else {
            requestPermissions(new String[]{LOCATION_PERMISSION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
                && ArrayUtils.all(grantResults, PackageManager.PERMISSION_GRANTED)) {
            pickLocation();
        }
    }

    @Override
    protected void handleError(Throwable error) {
        super.handleError(error);
        ErrorDialog.newInstance(requireContext(), error)
                .show(getChildFragmentManager(), null);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onSaveForm() {
        super.onSaveForm();
        boolean isInputValid = ArrayUtils.allTrue(
                FormValidators.isString(getViewBinding().nameInputLayout)
                // TODO Map selection
        );

        if (!isInputValid)
            return;

        String name = getViewBinding().nameInputLayout.getEditText().getText().toString().trim();
        // TODO Map selection

        if (getArgumentId() == 0) {
            // TODO New shop
        } else {
            // TODO Update shop
        }
    }

    //region AdminEditFragment

    @Override
    protected View getSaveButton() {
        return getViewBinding().shopSaveEdit;
    }

    @Override
    protected View getForm() {
        return getViewBinding().shopForm;
    }

    @Override
    protected View getLoadingView() {
        return getViewBinding().contentLoading;
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        viewModel.loadShop(getArgumentId());
    }

    @Override
    protected LiveResource<ShopAdminDetails> getLiveData() {
        return viewModel.getLiveShop();
    }

    @Override
    protected int getArgumentId() {
        return AdminShopEditFragmentArgs.fromBundle(requireArguments()).getShopId();
    }

    //endregion
}
