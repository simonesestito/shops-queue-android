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

import android.os.Handler;
import android.os.Looper;

import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Utility methods that unit tests can use to do common android library mocking that might be needed.
 * <p>
 * Credits: https://gist.github.com/dpmedeiros/7f7724fdf13fc5390bb05958448cdcad
 */
public class AndroidMockUtil {
    private final static ScheduledExecutorService mainThread = Executors.newSingleThreadScheduledExecutor();

    private AndroidMockUtil() {
    }

    /**
     * Mocks main thread handler post() and postDelayed() for use in Android unit tests
     * <p>
     * To use this:
     * <ol>
     *     <li>Call this method in an {@literal @}Before method of your test.</li>
     *     <li>Place Looper.class in the {@literal @}PrepareForTest annotation before your test class.</li>
     *     <li>any class under test that needs to call {@code new Handler(Looper.getMainLooper())} should be placed
     *     in the {@literal @}PrepareForTest annotation as well.</li>
     * </ol>
     */
    public static void mockMainThreadHandler() throws Exception {
        PowerMockito.mockStatic(Looper.class);
        Looper mockMainThreadLooper = mock(Looper.class);
        when(Looper.getMainLooper()).thenReturn(mockMainThreadLooper);
        Handler mockMainThreadHandler = mock(Handler.class);

        Answer<Boolean> handlerPostAnswer = invocation -> {
            Runnable runnable = invocation.getArgument(0, Runnable.class);
            Long delay = 0L;
            if (invocation.getArguments().length > 1) {
                delay = invocation.getArgument(1, Long.class);
            }
            if (runnable != null) {
                mainThread.schedule(runnable, delay, TimeUnit.MILLISECONDS);
            }
            return true;
        };

        when(mockMainThreadHandler.post(any(Runnable.class))).then(handlerPostAnswer);
        when(mockMainThreadHandler.postDelayed(any(Runnable.class), anyLong())).then(handlerPostAnswer);
        PowerMockito.whenNew(Handler.class).withArguments(mockMainThreadLooper).thenReturn(mockMainThreadHandler);
    }
}
