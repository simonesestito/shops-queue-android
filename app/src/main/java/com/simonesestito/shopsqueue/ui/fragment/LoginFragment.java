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

package com.simonesestito.shopsqueue.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.LoginFragmentBinding;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class LoginFragment extends AbstractAppFragment<LoginFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestToHideAppbar();
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(LoginViewModel.class);
        loginViewModel.loginRequest
                .onSuccess(this, data -> {
                    triggerAutofill();
                })
                .onRequestError(this, status -> {
                    enableLogin();
                    ErrorDialog.newInstance(getString(R.string.error_login_invalid))
                            .show(getChildFragmentManager(), null);
                })
                .onNetworkError(this, e -> {
                    enableLogin();
                    ErrorDialog.newInstance(getString(R.string.error_network_offline))
                            .show(getChildFragmentManager(), null);
                    e.printStackTrace();
                });
    }

    @Override
    protected LoginFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return LoginFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().loginButton.setOnClickListener(v -> onLoginSubmit());
        getViewBinding().signUpButton.setOnClickListener(v ->
                NavUtils.navigate(this, LoginFragmentDirections.actionLoginFragmentToSignUpFragment()));
    }

    @SuppressWarnings("ConstantConditions")
    private void onLoginSubmit() {
        boolean isInputValid = ArrayUtils.allTrue(
                FormValidators.isEmail(getViewBinding().emailInputLayout),
                FormValidators.isPassword(getViewBinding().passwordInputLayout)
        );

        if (!isInputValid)
            return;

        String email = getViewBinding().emailInputLayout.getEditText().getText().toString().trim();
        String password = getViewBinding().passwordInputLayout.getEditText().getText().toString().trim();

        loginViewModel.login(email, password);
        disableLogin();
    }

    @SuppressWarnings("ConstantConditions")
    private void enableLogin() {
        getViewBinding().loginButton.setVisibility(View.VISIBLE);
        getViewBinding().signUpButton.setVisibility(View.VISIBLE);
        getViewBinding().loadingLogin.setVisibility(View.GONE);
        getViewBinding().emailInputLayout.getEditText().setEnabled(true);
        getViewBinding().passwordInputLayout.getEditText().setEnabled(true);
    }

    @SuppressWarnings("ConstantConditions")
    private void disableLogin() {
        getViewBinding().loginButton.setVisibility(View.INVISIBLE);
        getViewBinding().signUpButton.setVisibility(View.GONE);
        getViewBinding().loadingLogin.setVisibility(View.VISIBLE);
        getViewBinding().emailInputLayout.getEditText().setEnabled(false);
        getViewBinding().passwordInputLayout.getEditText().setEnabled(false);
    }

    private void triggerAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager autofillManager = requireContext().getSystemService(AutofillManager.class);
            if (autofillManager != null)
                autofillManager.commit();
        }
    }
}
