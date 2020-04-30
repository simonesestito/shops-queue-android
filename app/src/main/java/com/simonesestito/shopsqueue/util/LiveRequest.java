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

import androidx.lifecycle.LifecycleOwner;

import com.simonesestito.shopsqueue.api.Callback;

/**
 * Wrap the response of a request inside a LiveData object
 */
public class LiveRequest<T> extends EventLiveData<LiveRequest.Status> {
    private static final int TYPE_SUCCESS = 1;
    private static final int TYPE_NETWORK_ERROR = 2;
    private static final int TYPE_REQUEST_ERROR = 3;

    public LiveRequest<T> onSuccess(LifecycleOwner lifecycleOwner, Callback<T> observer) {
        observeStatus(TYPE_SUCCESS, lifecycleOwner, observer);
        return this;
    }

    public LiveRequest<T> onNetworkError(LifecycleOwner lifecycleOwner, Callback<Throwable> observer) {
        observeStatus(TYPE_NETWORK_ERROR, lifecycleOwner, observer);
        return this;
    }

    public LiveRequest<T> onRequestError(LifecycleOwner lifecycleOwner, Callback<Integer> observer) {
        observeStatus(TYPE_REQUEST_ERROR, lifecycleOwner, observer);
        return this;
    }

    public void emitResult(T data) {
        emit(new Status(TYPE_SUCCESS, data));
    }

    public void emitNetworkError(Throwable error) {
        emit(new Status(TYPE_NETWORK_ERROR, error));
    }

    public void emitRequestError(int status) {
        emit(new Status(TYPE_REQUEST_ERROR, status));
    }

    /**
     * Observe to status updates,
     * calling the callback only if the status type matches
     */
    @SuppressWarnings("unchecked")
    private <D> void observeStatus(int type, LifecycleOwner lifecycleOwner, Callback<D> callback) {
        observeUnhandled(lifecycleOwner, status -> {
            if (status.type == type) {
                callback.onResult((D) status.data);
                return true;
            }
            return false;
        });
    }

    static class Status {
        private final int type;
        private final Object data;

        Status(int type, Object data) {
            this.type = type;
            this.data = data;
        }
    }
}
