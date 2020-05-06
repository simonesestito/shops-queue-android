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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.fragment.NavHostFragment;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.LocationPickerBinding;
import com.simonesestito.shopsqueue.model.PickedLocation;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.util.MapUtils;
import com.simonesestito.shopsqueue.util.MapboxLifecycleObserver;
import com.simonesestito.shopsqueue.util.NumberUtils;
import com.simonesestito.shopsqueue.viewmodel.LocationPickerViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Objects;

import javax.inject.Inject;

public class LocationPickerFragment extends AbstractAppFragment<LocationPickerBinding> {
    private static final int LOCATION_SEARCH_REQUEST_CODE = 1;
    private static final String PICKED_LOCATION_KEY = "picked_location";
    @Inject ViewModelFactory viewModelFactory;
    private LocationPickerViewModel viewModel;
    private SymbolManager symbolManager;
    private Style style;
    private Symbol lastMarker;
    private MapboxMap map;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    protected LocationPickerBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return LocationPickerBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewLifecycleOwner().getLifecycle()
                .addObserver(new MapboxLifecycleObserver(getViewBinding().map));
        getViewBinding().map.getMapAsync(this::onMapReady);

        getViewBinding().fabPickLocation.hide();
        getViewBinding().fabPickLocation.setOnClickListener(v -> onLocationPicked());
        getViewBinding().map.onCreate(savedInstanceState);
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
        if (viewModel.latestLocation != null)
            onNewLocation(viewModel.latestLocation);
    }

    private void onMapReady(MapboxMap map) {
        this.map = map;

        // Init map
        MapUtils.setStyle(requireContext(), map, style -> {
            this.style = style;
            this.symbolManager = new SymbolManager(
                    getViewBinding().map,
                    map,
                    style
            );
        });

        // Get latest location
        MapUtils.getLastKnownLocation(requireActivity(), location -> {
            if (location == null) {
                ErrorDialog.newInstance(getString(R.string.gps_disabled_error));
                return;
            }

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition position = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(getResources().getInteger(R.integer.mapbox_picker_zoom))
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position));

            viewModel.userLocation = latLng;
            onNewLocation(latLng);
        });

        map.addOnMapClickListener(location -> {
            onNewLocation(location);
            return true;
        });
    }

    private void onNewLocation(CarmenFeature location) {
        Point point = (Point) location.geometry();
        Objects.requireNonNull(point);
        onNewLocation(new LatLng(point.latitude(), point.longitude()));
    }

    private void onNewLocation(LatLng newLocation) {
        getViewBinding().fabPickLocation.hide();
        double lat = NumberUtils.roundCoordinate(newLocation.getLatitude());
        double lon = NumberUtils.roundCoordinate(newLocation.getLongitude());
        String displayCoordinates = lat + ";" + lon;
        getViewBinding().locationSearchEditText.setText(displayCoordinates);

        MapUtils.getAddressByCoordinates(newLocation, geocodeAddress -> {
            onNewLocation(new PickedLocation(newLocation.getLatitude(), newLocation.getLongitude(), geocodeAddress));
        });
    }

    private void onNewLocation(PickedLocation pickedLocation) {
        viewModel.latestLocation = pickedLocation;
        LatLng newLocation = pickedLocation.toLatLng();
        getViewBinding().locationSearchEditText.setText(pickedLocation.getAddress());
        getViewBinding().fabPickLocation.show();

        if (map != null) {
            CameraPosition newPosition = new CameraPosition.Builder()
                    .target(newLocation)
                    .zoom(getResources().getInteger(R.integer.mapbox_picker_zoom))
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(newPosition));
        }


        if (symbolManager != null) {
            if (lastMarker != null)
                symbolManager.delete(lastMarker);

            lastMarker = MapUtils.addMarker(symbolManager, style, requireContext(), newLocation);
        }
    }

    private void onLocationPicked() {
        NavBackStackEntry backStackEntry = NavHostFragment
                .findNavController(this)
                .getPreviousBackStackEntry();
        Objects.requireNonNull(backStackEntry)
                .getSavedStateHandle()
                .set(PICKED_LOCATION_KEY, viewModel.latestLocation);
        requireActivity().onBackPressed();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SEARCH_REQUEST_CODE && data != null) {
            CarmenFeature pickedPlace = PlaceAutocomplete.getPlace(data);
            onNewLocation(pickedPlace);
        }
    }
}
