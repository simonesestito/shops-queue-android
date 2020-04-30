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
import com.simonesestito.shopsqueue.api.dto.AuthResponse;
import com.simonesestito.shopsqueue.api.dto.NewUser;
import com.simonesestito.shopsqueue.api.dto.User;
import com.simonesestito.shopsqueue.api.dto.UserLogin;
import com.simonesestito.shopsqueue.api.service.LoginService;
import com.simonesestito.shopsqueue.model.HttpStatus;
import com.simonesestito.shopsqueue.util.LiveRequest;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {
    public final LiveRequest<AuthResponse> loginRequest = new LiveRequest<>();
    private final LoginService loginService;
    private final SharedPreferencesStore sharedPreferencesStore;
    private MutableLiveData<User> authStatus = new MutableLiveData<>();

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
                    .onResult(authStatus::setValue)
                    .onStatus(HttpStatus.HTTP_NOT_LOGGED_IN, () -> {
                        authStatus.setValue(null);
                    })
                    // TODO handle network error
                    .onError(Throwable::printStackTrace);
        }
    }

    /**
     * Do a login request.
     * @see LoginViewModel#loginRequest
     */
    public void login(String email, String password) {
        this.loginService.login(new UserLogin(email, password))
                .onResult(auth -> {
                    sharedPreferencesStore.setAccessToken(auth.getAccessToken());
                    authStatus.setValue(auth.getUser());
                }).postToLiveRequest(loginRequest);
    }

    /**
     * Register a new user.
     * If the registration is successful, login too.
     *
     * @see LoginViewModel#loginRequest
     */
    public void signUpAndLogin(NewUser newUser) {
        this.loginService.registerUser(newUser)
                .onResult(u -> this.login(newUser.getEmail(), newUser.getPassword()))
                .onStatus(HttpStatus.HTTP_CONFLICT, () ->
                        loginRequest.emitRequestError(HttpStatus.HTTP_CONFLICT))
                .onError(loginRequest::emitNetworkError);
    }

    public LiveData<User> getAuthStatus() {
        return authStatus;
    }
}
