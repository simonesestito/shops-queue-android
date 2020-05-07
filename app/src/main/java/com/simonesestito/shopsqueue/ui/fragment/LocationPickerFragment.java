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
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.LocationPickerBinding;
import com.simonesestito.shopsqueue.model.PickedLocation;
import com.simonesestito.shopsqueue.ui.MapboxHelper;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.util.MapUtils;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.NumberUtils;
import com.simonesestito.shopsqueue.viewmodel.LocationPickerViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class LocationPickerFragment extends AbstractAppFragment<LocationPickerBinding> {
    static final String PICKED_LOCATION_KEY = "picked_location";
    @Inject ViewModelFactory viewModelFactory;
    private LocationPickerFragmentArgs args;
    private LocationPickerViewModel viewModel;
    private MapboxHelper mapboxHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        args = LocationPickerFragmentArgs.fromBundle(requireArguments());
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    protected LocationPickerBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return LocationPickerBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapboxHelper = new MapboxHelper(getViewBinding().map, this);
        getViewBinding().fabPickLocation.hide();
        getViewBinding().fabPickLocation.setOnClickListener(v -> {
            NavUtils.setFragmentResult(this, PICKED_LOCATION_KEY, viewModel.latestLocation);
        });
        getViewBinding().locationSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show();
                doSearch(v.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(LocationPickerViewModel.class);

        if (viewModel.latestLocation == null) {
            if (args.getStartLocation() != null)
                onNewLocation(args.getStartLocation());
            else
                showUserLocation();
        } else {
            onNewLocation(viewModel.latestLocation);
        }

        mapboxHelper.setOnClickListener(this::onNewLocation);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.location_picker_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.locationPickerMenuUserLocation) {
            showUserLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUserLocation() {
        MapUtils.getLastKnownLocation(requireActivity(), location -> {
            if (location == null) {
                ErrorDialog.newInstance(getString(R.string.gps_disabled_error));
                return;
            }

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            viewModel.userLocation = latLng;
            onNewLocation(latLng);
        });
    }

    private void onNewLocation(LatLng newLocation) {
        getViewBinding().fabPickLocation.hide();
        double lat = NumberUtils.roundCoordinate(newLocation.getLatitude());
        double lon = NumberUtils.roundCoordinate(newLocation.getLongitude());
        String displayCoordinates = lat + ";" + lon;
        getViewBinding().locationSearchEditText.setText(displayCoordinates);

        mapboxHelper.moveTo(newLocation);
        mapboxHelper.showOrReplaceMarker(1, newLocation);

        MapUtils.getAddressByCoordinates(newLocation, geocodeAddress -> {
            onNewLocation(new PickedLocation(newLocation.getLatitude(), newLocation.getLongitude(), geocodeAddress));
        });
    }

    private void onNewLocation(PickedLocation pickedLocation) {
        viewModel.latestLocation = pickedLocation;
        LatLng newLocation = pickedLocation.toLatLng();
        getViewBinding().locationSearchEditText.setText(pickedLocation.getAddress());
        getViewBinding().fabPickLocation.show();
        mapboxHelper.moveTo(newLocation);
        mapboxHelper.showOrReplaceMarker(1, newLocation);
    }

    private void doSearch(String query) {
        getViewBinding().fabPickLocation.hide();
        MapUtils.getCoordinatesByName(viewModel.userLocation, query, location -> {
            Point point = (Point) location.geometry();
            if (point == null)
                return;
            PickedLocation pickedLocation = new PickedLocation(point.latitude(), point.longitude(), location.placeName());
            onNewLocation(pickedLocation);
        });
    }
}
