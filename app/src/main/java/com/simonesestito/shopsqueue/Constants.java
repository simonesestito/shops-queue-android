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

package com.simonesestito.shopsqueue;

import android.os.Build;

@SuppressWarnings("WeakerAccess")
public class Constants {
    public static final boolean IS_EMULATOR = Build.PRODUCT.equals("sdk");
    public static final String API_BASE_URL = getApiBaseUrl();
    public static final String SHARED_PREFERENCES_FILE = BuildConfig.APPLICATION_ID + "_preferences";
    public static final String SHARED_PREFERENCES_TOKEN_KEY = "api_access_token";
    public static final int COORDINATES_DIGITS_PRECISION = 6;
    public static final String APK_DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/shops-queue.appspot.com/o/app.apk?alt=media";
    public static final String GITHUB_REPO_URL = "https://github.com/simonesestito/shops-queue-android";


    private static String getApiBaseUrl() {
        return "https://shopsqueue.simonesestito.com";
        //if (!BuildConfig.DEBUG)
        //    return "https://shopsqueue.simonesestito.com";
        //else if (IS_EMULATOR)
        //    return "http://10.0.2.2:1234";
        //else
        //    return "http://192.168.1.100:1234";
    }
}
