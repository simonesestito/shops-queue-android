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

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.simonesestito.shopsqueue.R;

import java.util.Objects;

/**
 * Utility functions for Mapbox
 */
public class MapUtils {
    private static final String MARKER_ICON_ID = "custom-map-marker";

    /**
     * Set the style of the map, according to the current UI Theme
     */
    public static void setStyle(Context context, MapboxMap map, @Nullable Style.OnStyleLoaded callback) {
        if (ThemeUtils.isDarkTheme(context)) {
            map.setStyle(Style.DARK, callback);
        } else {
            map.setStyle(Style.MAPBOX_STREETS, callback);
        }
    }

    /**
     * Add a marker to the map
     */
    public static void addMarker(SymbolManager symbolManager, Style style, Context context, LatLng position) {
        if (style.getImage(MARKER_ICON_ID) == null) {
            Drawable drawable = context.getDrawable(R.drawable.ic_map_marker_24dp);
            Objects.requireNonNull(drawable);
            style.addImage(MARKER_ICON_ID, drawable);
        }

        symbolManager.setTextAllowOverlap(true);
        symbolManager.setIconAllowOverlap(true);

        symbolManager.create(new SymbolOptions()
                .withLatLng(position)
                .withIconImage(MARKER_ICON_ID)
                .withIconSize(1.3f));
    }
}
