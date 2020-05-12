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

package com.simonesestito.shopsqueue.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.util.ThemeUtils;
import com.simonesestito.shopsqueue.util.functional.Callback;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class to work with Mapbox
 */
@SuppressWarnings("WeakerAccess")
public class MapboxHelper implements LifecycleObserver {
    private static final String MARKER_ICON_ID = "custom-place-marker";
    private static final String USER_LOCATION_ICON_ID = "custom-user-location-marker";
    private static final int USER_LOCATION_MARKER_ID = 3742378;
    private final MapView mapView;
    private final int currentZoom;
    private final Map<Integer, Symbol> symbols = new HashMap<>();
    private final Map<LatLng, Runnable> markerClickCallbacks = new HashMap<>();
    private SymbolManager symbolManager;
    private boolean isLoadingMap;
    private List<Runnable> onMapLoadedCallbacks = new LinkedList<>();

    public MapboxHelper(MapView mapView, Fragment fragment) {
        this.mapView = mapView;
        currentZoom = fragment.getResources().getInteger(R.integer.mapbox_default_zoom);
        init(fragment.getViewLifecycleOwner().getLifecycle(), null);
    }

    @SuppressWarnings("WeakerAccess")
    public void init(Lifecycle lifecycle, @Nullable Runnable callback) {
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED))
            onCreate();
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED))
            onStart();
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED))
            onResume();
        lifecycle.addObserver(this);

        initMap(callback);
    }

    private void initMap(@Nullable Runnable callback) {
        if (callback != null)
            onMapLoadedCallbacks.add(callback);

        if (isLoadingMap)
            return;

        isLoadingMap = true;

        mapView.getMapAsync(mapboxMap -> {
            String style = ThemeUtils.isDarkTheme(mapView.getContext())
                    ? Style.DARK : Style.MAPBOX_STREETS;
            mapboxMap.setStyle(style, mapStyle -> {
                this.symbolManager = new SymbolManager(
                        mapView,
                        mapboxMap,
                        mapStyle
                );
                this.symbolManager.addClickListener(clickedSymbol -> {
                    Runnable markerListener = markerClickCallbacks.get(clickedSymbol.getLatLng());
                    if (markerListener != null) {
                        markerListener.run();
                    }
                });
                symbolManager.setTextAllowOverlap(true);
                symbolManager.setIconAllowOverlap(true);
                addMarkers(mapStyle);

                mapboxMap.getUiSettings().setCompassEnabled(false);

                for (Runnable onMapLoadedCallback : onMapLoadedCallbacks) {
                    onMapLoadedCallback.run();
                }

                isLoadingMap = false;
            });
        });
    }

    public void moveTo(LatLng latLng) {
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .zoom(currentZoom)
                .build();
        mapView.getMapAsync(map -> {
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        });
    }

    public void showOrReplaceMarker(int id, @Nullable LatLng latLng) {
        showOrReplaceMarker(id, latLng, MARKER_ICON_ID);
    }

    private void showOrReplaceMarker(int id, @Nullable LatLng latLng, String icon) {
        if (symbolManager == null) {
            initMap(() -> showOrReplaceMarker(id, latLng, icon));
            return;
        }

        if (symbols.containsKey(id)) {
            symbolManager.delete(Objects.requireNonNull(symbols.get(id)));
            symbols.remove(id);
        }

        if (latLng != null) {
            mapView.getMapAsync(map -> {
                map.getStyle(style -> {
                    Symbol symbol = addMarker(symbolManager, latLng, icon);
                    symbols.put(id, symbol);
                });
            });
        }
    }

    public void addMarker(@NonNull LatLng latLng, @Nullable Runnable onClickListener) {
        if (symbolManager == null) {
            initMap(() -> addMarker(latLng, onClickListener));
            return;
        }

        if (markerClickCallbacks.containsKey(latLng)) {
            // Marker already present at that location.
            // There's no need to add another one
            // We can just update the listener
            markerClickCallbacks.put(latLng, onClickListener);
            return;
        }

        mapView.getMapAsync(map -> {
            map.getStyle(style -> {
                addMarker(symbolManager, latLng, MARKER_ICON_ID);
                markerClickCallbacks.put(latLng, onClickListener);
            });
        });
    }

    public void clearMarkers() {
        if (symbolManager == null) {
            initMap(this::clearMarkers);
        } else {
            Symbol userLocation = symbols.get(USER_LOCATION_MARKER_ID);

            symbolManager.deleteAll();
            symbols.clear();
            markerClickCallbacks.clear();

            if (userLocation != null) {
                showUserLocation(userLocation.getLatLng());
            }
        }
    }

    public void showUserLocation(LatLng latLng) {
        showOrReplaceMarker(USER_LOCATION_MARKER_ID, latLng, USER_LOCATION_ICON_ID);
        mapView.getMapAsync(map -> {
            if (map.getCameraPosition().target.equals(new LatLng(0, 0, 0)))
                moveTo(latLng);
        });
    }

    public void onMapMoved(Callback<LatLng> callback) {
        mapView.getMapAsync(map -> {
            map.addOnCameraIdleListener(() -> callback.onResult(map.getCameraPosition().target));
        });
    }

    public void fitBounds(List<LatLng> latLngs) {
        if (latLngs.size() == 0)
            return;

        mapView.getMapAsync(map -> {
            if (latLngs.size() == 1) {
                this.moveTo(latLngs.get(0));
            } else {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .includes(latLngs)
                        .build();
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 3));
            }
        });
    }

    @SuppressWarnings("SameReturnValue")
    public void setOnClickListener(Callback<LatLng> listener) {
        mapView.getMapAsync(map -> map.addOnMapClickListener(location -> {
            listener.onResult(location);
            return true;
        }));
    }

    private Symbol addMarker(SymbolManager symbolManager, LatLng position, String icon) {
        float size = icon.equals(MARKER_ICON_ID) ? 1.3f : 1f;
        return symbolManager.create(new SymbolOptions()
                .withLatLng(position)
                .withIconImage(icon)
                .withIconSize(size));
    }

    @SuppressWarnings("ConstantConditions")
    private void addMarkers(Style style) {
        Context context = mapView.getContext();

        if (style.getImage(MARKER_ICON_ID) == null) {
            style.addImage(MARKER_ICON_ID, context.getDrawable(R.drawable.ic_map_marker_24dp));
        }

        if (style.getImage(USER_LOCATION_ICON_ID) == null) {
            style.addImage(USER_LOCATION_ICON_ID, context.getDrawable(R.drawable.ic_user_location_24dp));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        mapView.onCreate(null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        mapView.onStart();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        mapView.onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        mapView.onPause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        mapView.onStop();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mapView.onDestroy();
        symbolManager = null;
    }
}
