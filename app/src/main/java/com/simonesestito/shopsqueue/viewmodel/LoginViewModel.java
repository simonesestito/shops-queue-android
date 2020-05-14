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

import androidx.lifecycle.ViewModel;

import com.google.firebase.iid.FirebaseInstanceId;
import com.simonesestito.shopsqueue.SharedPreferencesStore;
import com.simonesestito.shopsqueue.api.dto.AuthResponse;
import com.simonesestito.shopsqueue.api.dto.FcmToken;
import com.simonesestito.shopsqueue.api.dto.NewSimpleUser;
import com.simonesestito.shopsqueue.api.dto.UserDetails;
import com.simonesestito.shopsqueue.api.dto.UserLogin;
import com.simonesestito.shopsqueue.api.service.FcmService;
import com.simonesestito.shopsqueue.api.service.LoginService;
import com.simonesestito.shopsqueue.model.AuthUserHolder;
import com.simonesestito.shopsqueue.model.HttpStatus;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import java.util.Objects;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {
    private final LoginService loginService;
    private final FcmService fcmService;
    private final SharedPreferencesStore sharedPreferencesStore;
    private final LiveResource<UserDetails> authStatus = new LiveResource<>();
    private final LiveResource<AuthResponse> loginRequest = new LiveResource<>();

    @Inject
    LoginViewModel(LoginService loginService, FcmService fcmService, SharedPreferencesStore sharedPreferencesStore) {
        this.loginService = loginService;
        this.fcmService = fcmService;
        this.sharedPreferencesStore = sharedPreferencesStore;
        initAuthStatus();
        this.authStatus.observeForever(event -> {
            if (!event.isLoading())
                AuthUserHolder.setCurrentUser(event.getData());

            if (event.isSuccessful() && event.getData() != null) {
                updateFcmToken();
            }
        });
    }

    private void updateFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(instanceIdResult -> {
                    String token = instanceIdResult.getToken();
                    fcmService.addFcmToken(new FcmToken(token));
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void initAuthStatus() {
        // Check if a token can be found in SharedPreferences
        String savedToken = sharedPreferencesStore.getAccessToken();
        if (savedToken == null) {
            // Not authenticated for sure
            authStatus.emitResult(null);
        } else {
            // Validate token against the API
            // The token will be added to the request by an interceptor
            authStatus.emitLoading();
            loginService.getCurrentUser()
                    .onResult(authStatus::emitResult)
                    .onStatus(HttpStatus.HTTP_NOT_LOGGED_IN, e -> authStatus.emitResult(null))
                    .onError(e -> {
                        e.printStackTrace();
                        authStatus.emitError(e);
                    });
        }
    }

    /**
     * Do a login request.
     *
     * @see LoginViewModel#loginRequest
     */
    public void login(String email, String password) {
        loginRequest.emitLoading();
        this.loginService.login(new UserLogin(email, password))
                .onResult(auth -> {
                    loginRequest.emitResult(auth);
                    sharedPreferencesStore.setAccessToken(auth.getAccessToken());
                    authStatus.emitResult(auth.getUser());
                }).onError(loginRequest::emitError);
    }

    /**
     * Register a new user.
     * If the registration is successful, login too.
     *
     * @see LoginViewModel#loginRequest
     */
    public void signUpAndLogin(NewSimpleUser newUser) {
        loginRequest.emitLoading();
        this.loginService.registerUser(newUser)
                .onResult(u -> this.login(newUser.getEmail(), newUser.getPassword()))
                .onError(loginRequest::emitError);
    }

    public void logout() {
        final String oldToken = sharedPreferencesStore.getAccessToken();
        this.loginService.logout()
                .onResult(v -> {
                    if (Objects.equals(oldToken, sharedPreferencesStore.getAccessToken())) {
                        sharedPreferencesStore.setAccessToken(null);
                    }
                })
                .onError(Throwable::printStackTrace);
        this.authStatus.emitResult(null);
    }

    public LiveResource<UserDetails> getAuthStatus() {
        return authStatus;
    }

    public LiveResource<AuthResponse> getLoginRequest() {
        return loginRequest;
    }
}
