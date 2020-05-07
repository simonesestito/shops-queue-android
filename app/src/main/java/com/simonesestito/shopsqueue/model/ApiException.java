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

package com.simonesestito.shopsqueue.model;

import android.annotation.SuppressLint;
import android.util.Log;

public class ApiException extends Exception {
    private final int statusCode;
    private final String body;

    public ApiException(int statusCode, String body) {
        super("Request failed with HTTP status " + statusCode);
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @SuppressLint("LogNotTimber")
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        Log.e("ApiException", "Error response body:\n" + body);
    }
}
