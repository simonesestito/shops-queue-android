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

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.SignUpFragmentBinding;
import com.simonesestito.shopsqueue.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class SignUpFragment extends AbstractAppFragment<SignUpFragmentBinding> {
    private static final int MIN_FORM_FIELD_LENGTH = 3;
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
        String name = getViewBinding().nameInputLayout.getEditText().getText().toString().trim();
        String surname = getViewBinding().surnameInputLayout.getEditText().getText().toString().trim();
        String email = getViewBinding().emailInputLayout.getEditText().getText().toString().trim();
        String password = getViewBinding().passwordInputLayout.getEditText().getText().toString().trim();

        // FIXME: find a better validation solution
        boolean isInputValid = true;

        // Validate name
        if (name.isEmpty()) {
            isInputValid = false;
            getViewBinding().nameInputLayout.setError(getText(R.string.form_field_required));
        } else if (name.length() < MIN_FORM_FIELD_LENGTH) {
            isInputValid = false;
            getViewBinding().nameInputLayout.setError(getText(R.string.form_field_too_short));
        } else {
            getViewBinding().nameInputLayout.setError("");
        }

        // Validate surname
        if (surname.isEmpty()) {
            isInputValid = false;
            getViewBinding().surnameInputLayout.setError(getText(R.string.form_field_required));
        } else if (surname.length() < MIN_FORM_FIELD_LENGTH) {
            isInputValid = false;
            getViewBinding().surnameInputLayout.setError(getText(R.string.form_field_too_short));
        } else {
            getViewBinding().surnameInputLayout.setError("");
        }

        // Validate email
        if (email.isEmpty()) {
            isInputValid = false;
            getViewBinding().emailInputLayout.setError(getString(R.string.form_field_required));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isInputValid = false;
            getViewBinding().emailInputLayout.setError(getString(R.string.form_invalid_email_field));
        } else {
            getViewBinding().emailInputLayout.setError("");
        }

        // Validate password
        if (password.isEmpty()) {
            isInputValid = false;
            getViewBinding().passwordInputLayout.setError(getString(R.string.form_field_required));
        } else if (password.length() < 8) {
            isInputValid = false;
            getViewBinding().passwordInputLayout.setError(getText(R.string.form_field_too_short));
        } else {
            getViewBinding().passwordInputLayout.setError("");
        }

        if (!isInputValid)
            return;

        // TODO Execute login
        Toast.makeText(requireActivity(), "TODO", Toast.LENGTH_LONG).show();
    }
}
