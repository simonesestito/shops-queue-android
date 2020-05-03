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

package com.simonesestito.shopsqueue.api;

import com.simonesestito.shopsqueue.SharedPreferencesStore;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
    private final SharedPreferencesStore sharedPreferencesStore;

    @Inject
    public AuthorizationInterceptor(SharedPreferencesStore sharedPreferencesStore) {
        this.sharedPreferencesStore = sharedPreferencesStore;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String accessToken = sharedPreferencesStore.getAccessToken();

        try {
            // TODO: Test only, remove me after development
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (accessToken != null) {
            request = request
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();
        }

        return chain.proceed(request);
    }
}
