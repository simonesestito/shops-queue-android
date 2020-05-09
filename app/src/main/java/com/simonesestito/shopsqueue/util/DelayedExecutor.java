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

import android.os.Handler;

import com.simonesestito.shopsqueue.util.functional.Callback;

/**
 * Execute a block of code after that
 * the execute method hasn't been called
 * for a determined amount of time
 */
public class DelayedExecutor<T> {
    private int delayMillis;
    private Handler handler;
    private Callback<T> callback;
    private int token;
    private T data;

    public DelayedExecutor(int delayMillis, Callback<T> callback) {
        this.delayMillis = delayMillis;
        this.handler = new Handler();
        this.callback = callback;
    }

    public void execute(T data) {
        final int newToken = (int) (Math.random() * Integer.MAX_VALUE);
        this.token = newToken;
        this.data = data;
        handler.postDelayed(() -> {
            if (this.token == newToken)
                callback.onResult(this.data);
        }, delayMillis);
    }
}
