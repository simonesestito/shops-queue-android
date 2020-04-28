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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.LoginFragmentBinding;
import com.simonesestito.shopsqueue.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Objects;

import javax.inject.Inject;

public class LoginFragment extends AbstractAppFragment<LoginFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(LoginViewModel.class);
    }

    @Override
    protected LoginFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return LoginFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().loginSubmitButton.setOnClickListener(v -> onLoginSubmit());
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }

    @SuppressWarnings("ConstantConditions")
    private void onLoginSubmit() {
        String email = getViewBinding().emailInput.getText().toString().trim();
        String password = getViewBinding().passwordInput.getText().toString().trim();

        // FIXME: find a better validation solution
        boolean isInputValid = true;

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
        } else {
            getViewBinding().passwordInputLayout.setError("");
        }

        if (!isInputValid)
            return;

        // TODO Execute login
        Toast.makeText(requireActivity(), "TODO", Toast.LENGTH_LONG).show();
    }
}
