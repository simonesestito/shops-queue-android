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

package com.simonesestito.shopsqueue.util.livedata;

import androidx.annotation.Nullable;

public class Resource<T> {
    private boolean handled;
    private boolean isSuccessful;
    private boolean isLoading;
    @Nullable private T data;
    @Nullable private Throwable error;

    private Resource() {
    }

    public static <T> Resource<T> successful(T data) {
        Resource<T> resource = new Resource<>();
        resource.isSuccessful = true;
        resource.data = data;
        return resource;
    }

    public static <T> Resource<T> error(Throwable error) {
        Resource<T> resource = new Resource<>();
        resource.error = error;
        return resource;
    }

    public static <T> Resource<T> loading() {
        Resource<T> resource = new Resource<>();
        resource.isLoading = true;
        return resource;
    }

    public void handle() {
        handled = true;
    }

    public boolean hasToBeHandled() {
        return !handled;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isFailed() {
        return !isSuccessful && !isLoading;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }
}
