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

import java.util.HashMap;
import java.util.Map;

public class ApiResponse<T> {
    private Callback<T> onResponse;
    private Map<Integer, Runnable> onErrorStatusHandlers;
    private Callback<Throwable> onError;

    ApiResponse() {
        this.onErrorStatusHandlers = new HashMap<>();
    }

    void onSuccess(T data) {
        if (onResponse == null) {
            throw new IllegalStateException("Undefined onResponse handler");
        }

        onResponse.onResult(data);
    }

    void onError(Throwable e) {
        if (onError == null) {
            throw new IllegalStateException("Undefined onResponse handler");
        }

        onError.onResult(e);
    }

    void onStatus(int status) {
        Runnable handler = onErrorStatusHandlers.get(status);
        if (handler == null) {
            onError(new RuntimeException("Request failed with HTTP status " + status));
        } else {
            handler.run();
        }
    }

    public ApiResponse<T> then(Callback<T> onResponse) {
        this.onResponse = onResponse;
        return this;
    }

    public ApiResponse<T> onStatus(int status, Runnable onErrorStatus) {
        onErrorStatusHandlers.put(status, onErrorStatus);
        return this;
    }

    public ApiResponse<T> onError(Callback<Throwable> onError) {
        this.onError = onError;
        return this;
    }
}
