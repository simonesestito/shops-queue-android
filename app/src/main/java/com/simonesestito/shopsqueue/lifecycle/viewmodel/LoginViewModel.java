package com.simonesestito.shopsqueue.lifecycle.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.simonesestito.shopsqueue.model.dto.input.AuthResponse;

public class LoginViewModel extends ViewModel {
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
