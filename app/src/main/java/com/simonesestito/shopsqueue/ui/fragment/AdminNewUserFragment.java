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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.databinding.AdminUserEditBinding;
import com.simonesestito.shopsqueue.model.HttpStatus;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.util.ApiException;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.viewmodel.AdminUserEditViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class AdminNewUserFragment extends AbstractAppFragment<AdminUserEditBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private AdminUserEditViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        NavUtils.<Shop>listenForResult(this, ShopPickerFragment.PICKED_SHOP_KEY,
                shop -> viewModel.pickedShop = shop);
    }

    @Override
    protected AdminUserEditBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AdminUserEditBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().userSaveEdit.setOnClickListener(v -> onSaveUser());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(AdminUserEditViewModel.class);

        viewModel.getLiveUser().observe(getViewLifecycleOwner(), event -> {
            if (event.isLoading()) {
                getViewBinding().contentLoading.setVisibility(View.VISIBLE);
                getViewBinding().userForm.setVisibility(View.GONE);
                getViewBinding().userSaveEdit.setEnabled(false);
            } else if (event.isFailed()) {
                getViewBinding().userSaveEdit.setEnabled(false);
                getViewBinding().contentLoading.setVisibility(View.GONE);
                if (!event.hasBeenHandled()) {
                    event.handle();
                    handleError(event.getError());
                }
            }
        });
    }

    private void handleError(Throwable error) {
        @StringRes int errorMessage = 0;

        if (!(error instanceof ApiException)) {
            errorMessage = R.string.error_network_offline;
        } else if (((ApiException) error).getStatusCode() == HttpStatus.HTTP_CONFLICT) {
            errorMessage = R.string.error_sign_up_duplicate_email;
        } else {
            error.printStackTrace();
        }

        if (errorMessage != 0) {
            ErrorDialog.newInstance(getString(errorMessage))
                    .show(getChildFragmentManager(), null);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void onSaveUser() {
        // Validate input
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

        // TODO Shop ID
        // TODO User role

        // TODO NewUser
    }
}
