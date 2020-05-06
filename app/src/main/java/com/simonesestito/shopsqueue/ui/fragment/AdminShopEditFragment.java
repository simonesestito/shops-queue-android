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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.databinding.AdminShopEditBinding;
import com.simonesestito.shopsqueue.di.module.ShopAdminDetails;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.dialog.PermissionDialog;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.MapUtils;
import com.simonesestito.shopsqueue.util.MapboxLifecycleObserver;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;
import com.simonesestito.shopsqueue.viewmodel.AdminShopEditViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class AdminShopEditFragment extends AdminEditFragment<ShopAdminDetails, AdminShopEditBinding> {
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final static int PICK_LOCATION_REQUEST_CODE = 2;
    private final static String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    @Inject ViewModelFactory viewModelFactory;
    private AdminShopEditViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    protected AdminShopEditBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AdminShopEditBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewLifecycleOwner().getLifecycle()
                .addObserver(new MapboxLifecycleObserver(getViewBinding().map));
        getViewBinding().map.onCreate(savedInstanceState);

        getViewBinding().map.setOnClickListener(v -> pickLocation());
        getViewBinding().mapEmptyView.setOnClickListener(v -> pickLocation());
    }

    @Override
    protected void showData(@Nullable ShopAdminDetails data) {
        super.showData(data);
        if (data != null) {
            Shop shop = data.getShop();
            requireActivity().setTitle(shop.getName());
            getViewBinding().nameInput.setText(shop.getName());
            getViewBinding().map.setVisibility(View.VISIBLE);
            getViewBinding().mapEmptyView.setVisibility(View.GONE);
            getViewBinding().map.getMapAsync(map -> {
                LatLng position = new LatLng(shop.getLatitude(), shop.getLongitude());

                map.setCameraPosition(new CameraPosition.Builder()
                        .target(position)
                        .zoom(getResources().getInteger(R.integer.mapbox_default_zoom))
                        .build());

                MapUtils.setStyle(requireContext(), map, style -> {
                    SymbolManager symbolManager = new SymbolManager(
                            getViewBinding().map,
                            map,
                            style
                    );
                    MapUtils.addMarker(symbolManager, style, requireContext(), position);
                });
            });

            // TODO Show owners (read-only)
        } else {
            getViewBinding().map.setVisibility(View.INVISIBLE);
            getViewBinding().mapEmptyView.setVisibility(View.VISIBLE);
            requireActivity().setTitle(R.string.new_shop_title);
        }
    }

    private void pickLocation() {
        // Check location permission
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            NavUtils.navigate(this, AdminShopEditFragmentDirections.adminShopEditPickLocation());
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
        ErrorDialog.newInstance(getString(R.string.error_network_offline))
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
        if (viewModel == null)
            viewModel = new ViewModelProvider(this, viewModelFactory).get(AdminShopEditViewModel.class);
        viewModel.loadShop(getArgumentId());
    }

    @Override
    protected LiveResource<ShopAdminDetails> getLiveData() {
        if (viewModel == null)
            viewModel = new ViewModelProvider(this, viewModelFactory).get(AdminShopEditViewModel.class);
        return viewModel.getLiveShop();
    }

    @Override
    protected int getArgumentId() {
        return AdminShopEditFragmentArgs.fromBundle(requireArguments()).getShopId();
    }

    //endregion
}
