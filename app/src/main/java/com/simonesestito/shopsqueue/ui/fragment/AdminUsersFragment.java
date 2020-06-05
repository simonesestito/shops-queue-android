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

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.AdminChildFragmentBinding;
import com.simonesestito.shopsqueue.ui.dialog.ConfirmDialog;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.AdminUsersAdapter;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.viewmodel.AdminUsersViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Objects;

import javax.inject.Inject;

public class AdminUsersFragment extends AbstractAppFragment<AdminChildFragmentBinding> {
    private static final int REQUEST_DELETE_USER = 1;
    private static final String EXTRA_CLICKED_USER_ID = "userId";
    @Inject ViewModelFactory viewModelFactory;
    private AdminUsersViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @NonNull
    @Override
    protected AdminChildFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AdminChildFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AdminUsersAdapter adapter = new AdminUsersAdapter();
        adapter.setMenuItemListener((menuItem, user) -> {
            if (menuItem.getItemId() == R.id.deleteMenuAction) {
                Bundle data = new Bundle();
                data.putInt(EXTRA_CLICKED_USER_ID, user.getId());
                ConfirmDialog.showForResult(this,
                        REQUEST_DELETE_USER,
                        getString(R.string.user_delete_confirm_message),
                        data);
            }
        });
        adapter.setItemClickListener(user ->
                NavUtils.navigate(this, AdminFragmentDirections.adminUserEdit().setUserId(user.getId())));
        getViewBinding().adminList.setAdapter(adapter);
        ViewUtils.addDivider(getViewBinding().adminList);

        getViewBinding().adminListRefresh
                .setOnRefreshListener(() -> viewModel.refreshUsers());

        getViewBinding().adminListActionAdd.setOnClickListener(v ->
                NavUtils.navigate(this, AdminFragmentDirections.adminUserEdit()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(AdminUsersViewModel.class);

        ViewUtils.onRecyclerViewLoadMore(getViewBinding().adminList, viewModel::loadNextPage);

        viewModel.getUsers().observe(getViewLifecycleOwner(), event -> {
            getViewBinding().adminListRefresh.setRefreshing(event.isLoading());

            if (event.isFailed() && event.hasToBeHandled()) {
                event.handle();
                ErrorDialog.newInstance(requireContext(), event.getError())
                        .show(getChildFragmentManager(), null);
            } else if (event.isSuccessful()) {
                AdminUsersAdapter adapter = (AdminUsersAdapter) getViewBinding().adminList.getAdapter();
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

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refreshUsers();
    }
}
