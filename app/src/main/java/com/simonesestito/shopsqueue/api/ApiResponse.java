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

import android.os.Handler;
import android.os.Looper;

import com.simonesestito.shopsqueue.util.ApiException;
import com.simonesestito.shopsqueue.util.functional.Callback;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApiResponse<T> {
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private List<Callback<T>> onResultHandlers;
    private Map<Integer, List<Callback<ApiException>>> onErrorStatusHandlers;
    private List<Callback<Throwable>> onErrorHandlers;

    ApiResponse() {
        this.onResultHandlers = new LinkedList<>();
        this.onErrorStatusHandlers = new HashMap<>();
        this.onErrorHandlers = new LinkedList<>();
    }

    void emitResult(T data) {
        for (Callback<T> handler : onResultHandlers) {
            uiHandler.post(() -> handler.onResult(data));
        }
    }

    void emitError(Throwable e) {
        // Check if this is an API error and there's a specific callback set
        if (e instanceof ApiException) {
            int status = ((ApiException) e).getStatusCode();
            List<Callback<ApiException>> statusCallbacks = onErrorStatusHandlers.get(status);
            if (statusCallbacks != null && !statusCallbacks.isEmpty()) {
                for (Callback<ApiException> statusCallback : statusCallbacks) {
                    uiHandler.post(() -> statusCallback.onResult((ApiException) e));
                }
                return;
            }
        }

        // Otherwise, fire the generic error handler
        for (Callback<Throwable> handler : onErrorHandlers) {
            uiHandler.post(() -> handler.onResult(e));
        }
    }

    public ApiResponse<T> onResult(Callback<T> onResult) {
        this.onResultHandlers.add(onResult);
        return this;
    }

    public ApiResponse<T> onStatus(int status, Callback<ApiException> onErrorStatus) {
        List<Callback<ApiException>> callbacks = onErrorStatusHandlers.get(status);
        if (callbacks == null)
            callbacks = new LinkedList<>();
        callbacks.add(onErrorStatus);
        onErrorStatusHandlers.put(status, callbacks);
        return this;
    }

    public ApiResponse<T> onError(Callback<Throwable> onError) {
        this.onErrorHandlers.add(onError);
        return this;
    }
}
