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

import androidx.annotation.Nullable;

import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

/**
 * Utility functions for Mapbox
 */
public class MapUtils {
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

    public static void setStyle(Context context, MapboxMap map) {
        setStyle(context, map, null);
    }
}
