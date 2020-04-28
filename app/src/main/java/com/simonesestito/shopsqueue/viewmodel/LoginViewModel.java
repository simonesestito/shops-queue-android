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

package com.simonesestito.shopsqueue.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.simonesestito.shopsqueue.api.LoginService;
import com.simonesestito.shopsqueue.api.dto.AuthResponse;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {
    @Inject
    public LoginViewModel(LoginService loginService) {
    }

    // TODO Check if SharedPreferences contain an access token
    private MutableLiveData<AuthResponse> authStatus = new MutableLiveData<>(null);

    public LiveData<AuthResponse> getAuthStatus() {
        return authStatus;
    }

    // FIXME: Remove this
    public void setAuthStatus(AuthResponse authStatus) {
        this.authStatus.postValue(authStatus);
    }
}
