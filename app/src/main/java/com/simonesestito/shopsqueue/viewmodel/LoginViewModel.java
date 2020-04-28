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

import com.simonesestito.shopsqueue.SharedPreferencesStore;
import com.simonesestito.shopsqueue.api.LoginService;
import com.simonesestito.shopsqueue.api.dto.AuthResponse;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {
    private final LoginService loginService;
    private final SharedPreferencesStore sharedPreferencesStore;

    private MutableLiveData<AuthResponse> authStatus = new MutableLiveData<>();

    @Inject
    public LoginViewModel(LoginService loginService, SharedPreferencesStore sharedPreferencesStore) {
        this.loginService = loginService;
        this.sharedPreferencesStore = sharedPreferencesStore;
        initAuthStatus();
    }

    private void initAuthStatus() {
        // Check if a token can be found in SharedPreferences
        String savedToken = sharedPreferencesStore.getAccessToken();
        if (savedToken == null) {
            // Not authenticated for sure
            authStatus.setValue(null);
        } else {
            // Validate token against the API
            // The token will be added to the request by an interceptor
            loginService.getCurrentUser()
                    .thenAccept(user -> {
                        AuthResponse authResponse = new AuthResponse();
                        authResponse.setAccessToken(savedToken);
                        authResponse.setUser(user);
                        authStatus.postValue(authResponse);
                    }).exceptionally(e -> {
                // TODO Handle network error
                authStatus.postValue(null);
                return null;
            });
        }
    }

    public LiveData<AuthResponse> getAuthStatus() {
        return authStatus;
    }

    // FIXME: Remove this
    public void setAuthStatus(AuthResponse authStatus) {
        this.authStatus.postValue(authStatus);
    }
}
