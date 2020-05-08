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
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.util.ThemeUtils;
import com.simonesestito.shopsqueue.util.functional.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class to work with Mapbox
 */
@SuppressWarnings("WeakerAccess")
public class MapboxHelper implements LifecycleObserver {
    private static final String MARKER_ICON_ID = "custom-marker";
    private final MapView mapView;
    private SymbolManager symbolManager;
    private final int currentZoom;
    private final Map<Integer, Symbol> symbols = new HashMap<>();
    private final Map<Long, Runnable> markerClickCallbacks = new HashMap<>();

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

        initMap(null);
    }

    private void initMap(@Nullable Runnable callback) {
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
                    Runnable markerListener = markerClickCallbacks.get(clickedSymbol.getId());
                    if (markerListener != null) {
                        markerListener.run();
                    }
                });

                if (callback != null)
                    callback.run();
            });
        });
    }

    public void moveTo(Location location) {
        moveTo(new LatLng(location.getLatitude(), location.getLongitude()));
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
        if (symbolManager == null) {
            initMap(() -> showOrReplaceMarker(id, latLng));
            return;
        }

        if (symbols.containsKey(id)) {
            symbolManager.delete(Objects.requireNonNull(symbols.get(id)));
            symbols.remove(id);
        }

        if (latLng != null) {
            mapView.getMapAsync(map -> {
                map.getStyle(style -> {
                    Symbol symbol = addMarker(symbolManager, style, mapView.getContext(), latLng);
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

        for (int i = 0; i < symbolManager.getAnnotations().size(); i++) {
            Symbol symbol = symbolManager.getAnnotations().get(i);
            if (Objects.requireNonNull(symbol).getLatLng().equals(latLng)) {
                return;
            }
        }

        mapView.getMapAsync(map -> {
            map.getStyle(style -> {
                Symbol symbol = addMarker(symbolManager, style, mapView.getContext(), latLng);
                if (onClickListener != null)
                    markerClickCallbacks.put(symbol.getId(), onClickListener);
            });
        });
    }

    public void onMapMoved(Callback<LatLng> callback) {
        mapView.getMapAsync(map -> {
            map.addOnCameraIdleListener(() -> {
                callback.onResult(map.getCameraPosition().target);
            });
        });
    }

    @SuppressWarnings("SameReturnValue")
    public void setOnClickListener(Callback<LatLng> listener) {
        mapView.getMapAsync(map -> map.addOnMapClickListener(location -> {
            listener.onResult(location);
            return true;
        }));
    }

    private Symbol addMarker(SymbolManager symbolManager, Style style, Context context, LatLng position) {
        if (style.getImage(MARKER_ICON_ID) == null) {
            Drawable drawable = context.getDrawable(R.drawable.ic_map_marker_24dp);
            Objects.requireNonNull(drawable);
            style.addImage(MARKER_ICON_ID, drawable);
        }

        symbolManager.setTextAllowOverlap(true);
        symbolManager.setIconAllowOverlap(true);

        return symbolManager.create(new SymbolOptions()
                .withLatLng(position)
                .withIconImage(MARKER_ICON_ID)
                .withIconSize(1.3f));
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
