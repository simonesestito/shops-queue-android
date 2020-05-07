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

package com.simonesestito.shopsqueue.util;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.simonesestito.shopsqueue.util.functional.Callback;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Utility functions for Mapbox
 */
@SuppressWarnings("WeakerAccess")
public class MapUtils {
    public static final int ENABLE_LOCATION_REQUEST_CODE = 10;

    /**
     * Check location Settings
     * You need to pass a callback
     * It'll be return {@code true} if it's safe to fetch location updates
     * It'll return {@code false} if it's impossible to change location settings
     * In case location is disabled, an intent will be launched to fix the issue
     * You should listen on onActivityResult.
     * The callback won't be called!
     *
     * @param onLocationEnabled Callback
     */
    public static void checkLocationSettings(Activity activity, Callback<Boolean> onLocationEnabled) {
        LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest())
                .build();
        LocationServices.getSettingsClient(activity)
                .checkLocationSettings(settingsRequest)
                .addOnSuccessListener(response -> onLocationEnabled.onResult(true))
                .addOnFailureListener(error -> {
                    if (!(error instanceof ResolvableApiException)) {
                        onLocationEnabled.onResult(false);
                        return;
                    }

                    ResolvableApiException resolvable = (ResolvableApiException) error;
                    try {
                        resolvable.startResolutionForResult(
                                activity, ENABLE_LOCATION_REQUEST_CODE
                        );
                    } catch (IntentSender.SendIntentException e) {
                        onLocationEnabled.onResult(false);
                    }
                });
    }

    public static void getCurrentLocation(Activity activity, OnSuccessListener<Location> callback) {
        checkLocationSettings(activity, success -> {
            if (!success)
                return;

            LocationServices.getFusedLocationProviderClient(activity)
                    .requestLocationUpdates(createLocationRequest(), new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            callback.onSuccess(locationResult.getLastLocation());
                        }
                    }, null);
        });
    }

    private static LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setNumUpdates(1)
                .setExpirationDuration(5_000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static void getAddressByCoordinates(LatLng newLocation, Callback<String> callback) {
        MapboxGeocoding.builder()
                .accessToken(Objects.requireNonNull(Mapbox.getAccessToken()))
                .query(Point.fromLngLat(newLocation.getLongitude(), newLocation.getLatitude()))
                .build()
                .enqueueCall(new retrofit2.Callback<GeocodingResponse>() {
                    @Override
                    public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            callback.onResult(null);
                            return;
                        }

                        List<CarmenFeature> features = response.body().features();
                        if (features.isEmpty()) {
                            callback.onResult(null);
                            return;
                        }

                        callback.onResult(features.get(0).placeName());
                    }

                    @Override
                    public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                        callback.onResult(null);
                    }
                });
    }

    public static void getCoordinatesByName(LatLng startPoint, String query, Callback<CarmenFeature> callback) {
        MapboxGeocoding.builder()
                .accessToken(Objects.requireNonNull(Mapbox.getAccessToken()))
                .query(query)
                .geocodingTypes("address")
                .proximity(Point.fromLngLat(startPoint.getLongitude(), startPoint.getLatitude()))
                .build()
                .enqueueCall(new retrofit2.Callback<GeocodingResponse>() {
                    @Override
                    public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            callback.onResult(null);
                            return;
                        }

                        List<CarmenFeature> features = response.body().features();
                        if (features.isEmpty()) {
                            callback.onResult(null);
                            return;
                        }

                        // Find nearest location found
                        CarmenFeature nearestFeature = null;
                        double minDistance = Double.MAX_VALUE;
                        for (CarmenFeature feature : features) {
                            Point point = (Point) feature.geometry();
                            if (point == null)
                                continue;
                            LatLng latLng = new LatLng(point.latitude(), point.longitude());
                            double distance = latLng.distanceTo(startPoint);
                            if (distance < minDistance) {
                                nearestFeature = feature;
                                minDistance = distance;
                            }
                        }

                        callback.onResult(nearestFeature);
                    }

                    @Override
                    public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                        callback.onResult(null);
                    }
                });
    }
}
