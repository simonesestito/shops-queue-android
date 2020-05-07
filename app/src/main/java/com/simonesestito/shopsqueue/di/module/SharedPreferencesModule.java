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

package com.simonesestito.shopsqueue.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.simonesestito.shopsqueue.Constants;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {
    @Provides
    public SharedPreferences provideSharedPreferences(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return provideEncryptedSharedPreferences(context);
        } else {
            return provideLegacySharedPreferences(context);
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private SharedPreferences provideEncryptedSharedPreferences(Context context) {
        try {
            return EncryptedSharedPreferences.create(
                    Constants.SHARED_PREFERENCES_FILE,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("SharedPreferences", "Unable to provide encrypted SharedPreferences", e);
            return provideLegacySharedPreferences(context);
        }
    }

    private SharedPreferences provideLegacySharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }
}
