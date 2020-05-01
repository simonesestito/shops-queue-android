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
import com.simonesestito.shopsqueue.api.dto.AuthResponse;
import com.simonesestito.shopsqueue.api.dto.NewUser;
import com.simonesestito.shopsqueue.databinding.SignUpFragmentBinding;
import com.simonesestito.shopsqueue.model.HttpStatus;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.util.ApiException;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.livedata.Resource;
import com.simonesestito.shopsqueue.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class SignUpFragment extends AbstractAppFragment<SignUpFragmentBinding> {
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
        loginViewModel.getLoginRequest().observe(getViewLifecycleOwner(), event -> {
            enableSignUp();
            if (event.isSuccessful()) {
                triggerAutofill();
            } else if (event.isInProgress()) {
                disableSignUp();
            } else if (event.getError() != null && !event.hasBeenHandled()) {
                handleError(event);
            }
        });
    }

    @Override
    protected SignUpFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return SignUpFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().signUpButton.setOnClickListener(v -> onSignUpSubmit());
        getViewBinding().backButton.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    @SuppressWarnings("ConstantConditions")
    private void onSignUpSubmit() {
        boolean isInputValid = ArrayUtils.allTrue(
                FormValidators.isString(getViewBinding().nameInputLayout),
                FormValidators.isString(getViewBinding().surnameInputLayout),
                FormValidators.isEmail(getViewBinding().emailInputLayout),
                FormValidators.isPassword(getViewBinding().passwordInputLayout)
        );

        if (!isInputValid)
            return;

        String name = getViewBinding().nameInputLayout.getEditText().getText().toString().trim();
        String surname = getViewBinding().surnameInputLayout.getEditText().getText().toString().trim();
        String email = getViewBinding().emailInputLayout.getEditText().getText().toString().trim();
        String password = getViewBinding().passwordInputLayout.getEditText().getText().toString().trim();

        NewUser newUser = new NewUser(name, surname, email, password);
        loginViewModel.signUpAndLogin(newUser);
        disableSignUp();
    }

    private void handleError(Resource<AuthResponse> event) {
        Throwable error = event.getError();
        if (error == null)
            return;

        if (!(error instanceof ApiException)) {
            ErrorDialog.newInstance(getString(R.string.error_network_offline))
                    .show(getChildFragmentManager(), null);
            event.handle();
            return;
        }

        ApiException apiException = (ApiException) error;
        if (apiException.getStatusCode() == HttpStatus.HTTP_CONFLICT) {
            ErrorDialog.newInstance(getString(R.string.error_sign_up_duplicate_email))
                    .show(getChildFragmentManager(), null);
            event.handle();
        }
    }


    @SuppressWarnings("ConstantConditions")
    private void enableSignUp() {
        getViewBinding().signUpButton.setVisibility(View.VISIBLE);
        getViewBinding().loadingSignUp.setVisibility(View.GONE);
        getViewBinding().nameInputLayout.getEditText().setEnabled(true);
        getViewBinding().surnameInputLayout.getEditText().setEnabled(true);
        getViewBinding().emailInputLayout.getEditText().setEnabled(true);
        getViewBinding().passwordInputLayout.getEditText().setEnabled(true);
    }

    @SuppressWarnings("ConstantConditions")
    private void disableSignUp() {
        getViewBinding().signUpButton.setVisibility(View.INVISIBLE);
        getViewBinding().loadingSignUp.setVisibility(View.VISIBLE);
        getViewBinding().nameInputLayout.getEditText().setEnabled(false);
        getViewBinding().surnameInputLayout.getEditText().setEnabled(false);
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
