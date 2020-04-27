package com.simonesestito.shopsqueue.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.databinding.LoginFragmentBinding;
import com.simonesestito.shopsqueue.lifecycle.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.model.dto.input.AuthResponse;
import com.simonesestito.shopsqueue.model.dto.input.User;

public class LoginFragment extends AbstractAppFragment<LoginFragmentBinding> {
    @Override
    protected LoginFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return LoginFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // FIXME: Remove this
        new Handler().postDelayed(() -> new ViewModelProvider(requireActivity())
                .get(LoginViewModel.class)
                .setAuthStatus(
                        new AuthResponse(
                                null,
                                new User(
                                        0,
                                        null,
                                        null,
                                        null,
                                        "USER",
                                        null
                                )
                        )
                ), 1500);
    }
}
