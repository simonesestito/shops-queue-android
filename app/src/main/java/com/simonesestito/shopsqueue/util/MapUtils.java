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
import com.google.android.gms.location.LocationServices;
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
public class MapUtils {
    public static void getLastKnownLocation(Activity activity, OnSuccessListener<Location> callback) {
        LocationServices.getFusedLocationProviderClient(activity)
                .getLastLocation()
                .addOnSuccessListener(callback)
                .addOnFailureListener(e -> {
                    if (e instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(activity, 0);
                            return;
                        } catch (IntentSender.SendIntentException intentException) {
                            intentException.printStackTrace();
                        }
                    }
                    callback.onSuccess(null);
                });
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
