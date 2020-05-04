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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.AdminUsersFragmentBinding;
import com.simonesestito.shopsqueue.ui.dialog.ConfirmDialog;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.AdminUsersAdapter;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.viewmodel.AdminUsersViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Objects;

import javax.inject.Inject;

public class AdminUsersFragment extends AbstractAppFragment<AdminUsersFragmentBinding> {
    private static final int REQUEST_DELETE_USER = 1;
    private static final String EXTRA_CLICKED_USER_ID = "userId";
    @Inject ViewModelFactory viewModelFactory;
    private AdminUsersViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    protected AdminUsersFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AdminUsersFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AdminUsersAdapter adapter = new AdminUsersAdapter();
        adapter.setMenuItemListener((menuItem, userId) -> {
            if (menuItem.getItemId() == R.id.deleteUserMenuAction) {
                Bundle data = new Bundle();
                data.putInt(EXTRA_CLICKED_USER_ID, userId);
                ConfirmDialog.showForResult(this,
                        REQUEST_DELETE_USER,
                        getString(R.string.user_delete_confirm_message),
                        data);
            }
        });
        adapter.setItemClickListener(user -> {
            NavDirections directions = AdminFragmentDirections
                    .actionAdminFragmentToAdminUserEditFragment()
                    .setUserId(user.getId());
            NavUtils.navigate(this, directions);
        });
        getViewBinding().adminUsersList.setAdapter(adapter);
        ViewUtils.addDivider(getViewBinding().adminUsersList);

        getViewBinding().adminUsersRefresh
                .setOnRefreshListener(() -> viewModel.refreshUsers());

        getViewBinding().adminUsersAdd.setOnClickListener(v -> {
            NavDirections directions = AdminFragmentDirections
                    .actionAdminFragmentToAdminUserEditFragment();
            NavUtils.navigate(this, directions);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(AdminUsersViewModel.class);

        ViewUtils.onRecyclerViewLoadMore(getViewBinding().adminUsersList, viewModel::loadNextPage);

        viewModel.getUsers().observe(getViewLifecycleOwner(), event -> {
            getViewBinding().adminUsersRefresh.setRefreshing(event.isLoading());

            if (event.isFailed() && !event.hasBeenHandled()) {
                event.handle();
                ErrorDialog.newInstance(getString(R.string.error_network_offline))
                        .show(getChildFragmentManager(), null);
            } else if (event.isSuccessful()) {
                AdminUsersAdapter adapter = (AdminUsersAdapter) getViewBinding().adminUsersList.getAdapter();
                Objects.requireNonNull(adapter).updateDataSet(event.getData());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DELETE_USER
                && resultCode == Activity.RESULT_OK
                && data != null) {
            int userId = data.getIntExtra(EXTRA_CLICKED_USER_ID, 0);
            if (userId > 0) {
                viewModel.deleteUser(userId);
            }
        }
    }
}
