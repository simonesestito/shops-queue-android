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

import android.os.Looper;

import com.simonesestito.shopsqueue.AndroidMockUtil;
import com.simonesestito.shopsqueue.util.functional.Callback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Looper.class)
public class ApiResponseTest {
    @Mock
    public Callback<Object> callback;

    @Before
    public void mockMainThread() throws Exception {
        AndroidMockUtil.mockMainThreadHandler();
    }

    @Test
    public void onResultHandlerIsCalled() {
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.onResult(callback);

        Object result = new Object();
        apiResponse.emitResult(result);

        verify(callback).onResult(result);
    }
}
