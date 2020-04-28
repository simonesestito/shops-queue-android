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

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiCallAdapter<T> implements CallAdapter<T, ApiResponse<T>> {
    private final Type responseType;

    private ApiCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return this.responseType;
    }

    @Override
    public ApiResponse<T> adapt(Call<T> call) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    apiResponse.onSuccess(response.body());
                } else {
                    apiResponse.onStatus(response.code());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                apiResponse.onError(t);
            }
        });
        return apiResponse;
    }

    public static class Factory extends CallAdapter.Factory {
        @Override
        public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
            if (getRawType(returnType) != ApiResponse.class) {
                return null;
            }
            Type parameterType = getParameterUpperBound(0, (ParameterizedType) returnType);
            Log.d("ApiCallAdapter", getRawType(parameterType).getCanonicalName());
            return new ApiCallAdapter<>(parameterType);
        }
    }
}
