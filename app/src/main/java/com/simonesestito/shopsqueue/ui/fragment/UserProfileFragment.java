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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.NewSimpleUser;
import com.simonesestito.shopsqueue.api.dto.UserDetails;
import com.simonesestito.shopsqueue.databinding.UserProfileFragmentBinding;
import com.simonesestito.shopsqueue.model.AuthUserHolder;
import com.simonesestito.shopsqueue.model.EmailRevokedException;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;
import com.simonesestito.shopsqueue.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.viewmodel.UserProfileViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class UserProfileFragment extends EditFragment<UserDetails, UserProfileFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private UserProfileViewModel viewModel;
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(UserProfileViewModel.class);
    }

    @NonNull
    @Override
    protected UserProfileFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return UserProfileFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(LoginViewModel.class);
    }

    @Override
    protected void showData(@Nullable UserDetails data) {
        super.showData(data);
        if (data == null)
            return;

        getViewBinding().nameInput.setText(data.getName());
        getViewBinding().surnameInput.setText(data.getSurname());
        getViewBinding().emailInput.setText(data.getEmail());
        requireActivity().setTitle(data.getFullName());
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onSaveForm() {
        super.onSaveForm();
        boolean isPasswordValid = getArgumentId() == 0
                ? FormValidators.isPassword(getViewBinding().passwordInputLayout)
                : FormValidators.optional(getViewBinding().passwordInputLayout, FormValidators::isPassword);

        boolean isInputValid = ArrayUtils.allTrue(
                FormValidators.isString(getViewBinding().nameInputLayout),
                FormValidators.isString(getViewBinding().surnameInputLayout),
                FormValidators.isEmail(getViewBinding().emailInputLayout),
                isPasswordValid
        );

        if (!isInputValid)
            return;

        String name = getViewBinding().nameInputLayout.getEditText().getText().toString().trim();
        String surname = getViewBinding().surnameInputLayout.getEditText().getText().toString().trim();
        String email = getViewBinding().emailInputLayout.getEditText().getText().toString().trim();
        String password = getViewBinding().passwordInput.getText().toString().trim();
        if (password.isEmpty())
            password = null;

        viewModel.updateUser(new NewSimpleUser(name, surname, email, password));
    }

    //region AdminEditFragment


    @Override
    protected void handleError(Throwable error) {
        if (error instanceof EmailRevokedException) {
            Toast.makeText(requireActivity(), R.string.login_email_not_confirmed, Toast.LENGTH_LONG).show();
            loginViewModel.logout();
        } else {
            ErrorDialog.newInstance(requireContext(), error)
                    .show(getChildFragmentManager(), null);
        }
    }

    @Override
    protected View getSaveButton() {
        return getViewBinding().userSaveEdit;
    }

    @Override
    protected void setFormVisibility(int visibility) {
        super.setFormVisibility(visibility);
        getViewBinding().userForm.setVisibility(visibility);
    }

    @Override
    protected View getLoadingView() {
        return getViewBinding().contentLoading;
    }

    @Override
    protected LiveResource<UserDetails> getLiveData() {
        return viewModel.getCurrentUser();
    }

    @Override
    protected int getArgumentId() {
        return AuthUserHolder.getCurrentUser().getId();
    }

    //endregion
}
