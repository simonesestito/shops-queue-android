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
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.NewUser;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.api.dto.UserDetails;
import com.simonesestito.shopsqueue.databinding.AdminUserEditBinding;
import com.simonesestito.shopsqueue.model.UserRole;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.UserRoleSpinnerAdapter;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;
import com.simonesestito.shopsqueue.viewmodel.AdminUserEditViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Arrays;

import javax.inject.Inject;

public class AdminUserEditFragment extends EditFragment<UserDetails, AdminUserEditBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private AdminUserEditViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @NonNull
    @Override
    protected AdminUserEditBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AdminUserEditBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserRoleSpinnerAdapter roleAdapter = new UserRoleSpinnerAdapter(requireContext());
        getViewBinding().adminUserRole.setAdapter(roleAdapter);

        getViewBinding().shopInput.setOnClickListener(v ->
                NavUtils.navigate(this, AdminUserEditFragmentDirections.adminNewUserPickShop()));

        ViewUtils.setSpinnerListener(getViewBinding().adminUserRole, index -> {
            UserRole clickedRole = UserRole.values()[index];
            int shopVisibility = clickedRole.equals(UserRole.OWNER) ? View.VISIBLE : View.GONE;
            getViewBinding().shopInputLayout.setVisibility(shopVisibility);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Shop pickedShop = NavUtils.getFragmentResult(this, ShopPickerFragment.PICKED_SHOP_KEY);
        if (pickedShop != null) {
            onShopPicked(pickedShop);
        }
    }

    @Override
    protected void showData(@Nullable UserDetails user) {
        super.showData(user);
        if (user != null) {
            requireActivity().setTitle(user.getFullName());
            getViewBinding().nameInput.setText(user.getName());
            getViewBinding().surnameInput.setText(user.getSurname());
            getViewBinding().emailInput.setText(user.getEmail());
            getViewBinding().userActive.setChecked(user.isActive());

            int roleIndex = Arrays.asList(UserRole.values()).indexOf(user.getRole());
            getViewBinding().adminUserRole.setSelection(roleIndex);

            Shop shop = user.getShop();
            viewModel.pickedShop = shop;
            if (shop == null) {
                getViewBinding().shopInputLayout.setVisibility(View.GONE);
                getViewBinding().shopInput.setText("");
            } else {
                getViewBinding().shopInputLayout.setVisibility(View.VISIBLE);
                getViewBinding().shopInput.setText(shop.getName());
            }
        } else {
            requireActivity().setTitle(getString(R.string.new_user_title));
        }
    }

    private void onShopPicked(Shop shop) {
        viewModel.pickedShop = shop;
        getViewBinding().shopInput.setText(shop.getName());
    }

    @Override
    protected void handleError(Throwable error) {
        super.handleError(error);
        ErrorDialog.newInstance(requireContext(), error)
                .show(getChildFragmentManager(), null);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onSaveForm() {
        // Validate input
        UserRole role = UserRole.values()[getViewBinding().adminUserRole.getSelectedItemPosition()];
        boolean shopValid = !role.equals(UserRole.OWNER) || viewModel.pickedShop != null;
        if (!shopValid) {
            getViewBinding().shopInputLayout.setError(getString(R.string.form_field_required));
        }

        boolean isPasswordValid = getArgumentId() == 0
                ? FormValidators.isPassword(getViewBinding().passwordInputLayout)
                : FormValidators.optional(getViewBinding().passwordInputLayout, FormValidators::isPassword);

        boolean isInputValid = ArrayUtils.allTrue(
                FormValidators.isString(getViewBinding().nameInputLayout),
                FormValidators.isString(getViewBinding().surnameInputLayout),
                FormValidators.isEmail(getViewBinding().emailInputLayout),
                isPasswordValid,
                shopValid
        );

        if (!isInputValid)
            return;

        String name = getViewBinding().nameInputLayout.getEditText().getText().toString().trim();
        String surname = getViewBinding().surnameInputLayout.getEditText().getText().toString().trim();
        String email = getViewBinding().emailInputLayout.getEditText().getText().toString().trim();
        String password = getViewBinding().passwordInputLayout.getEditText().getText().toString().trim();
        Integer shopId = role.equals(UserRole.OWNER) ? viewModel.pickedShop.getId() : null;
        boolean active = getViewBinding().userActive.isChecked();

        if (password.isEmpty())
            password = null;

        NewUser newUser = new NewUser(name, surname, email, password, shopId, role, active);
        if (getArgumentId() == 0) {
            viewModel.saveNewUser(newUser);
        } else {
            viewModel.updateUser(getArgumentId(), newUser);
        }
    }

    @Override
    protected int getArgumentId() {
        return AdminUserEditFragmentArgs.fromBundle(requireArguments()).getUserId();
    }

    @Override
    protected LiveResource<UserDetails> getLiveData() {
        if (viewModel == null)
            viewModel = new ViewModelProvider(this, viewModelFactory).get(AdminUserEditViewModel.class);
        return viewModel.getLiveUser();
    }

    @Override
    protected View getSaveButton() {
        return getViewBinding().userSaveEdit;
    }


    @Override
    protected View getLoadingView() {
        return getViewBinding().contentLoading;
    }

    @Override
    protected void setFormVisibility(int visibility) {
        super.setFormVisibility(visibility);
        getViewBinding().userForm.setVisibility(visibility);
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        if (viewModel == null)
            viewModel = new ViewModelProvider(this, viewModelFactory).get(AdminUserEditViewModel.class);
        viewModel.loadUser(getArgumentId());
    }
}
