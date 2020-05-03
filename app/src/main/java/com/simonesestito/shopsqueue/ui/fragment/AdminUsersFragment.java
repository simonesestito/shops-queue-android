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
import com.simonesestito.shopsqueue.databinding.AdminUsersFragmentBinding;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.viewmodel.AdminUsersViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class AdminUsersFragment extends AbstractAppFragment<AdminUsersFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private AdminUsersViewModel adminUsersViewModel;

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
        // TODO
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adminUsersViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(AdminUsersViewModel.class);

        adminUsersViewModel.getUsers().observe(getViewLifecycleOwner(), event -> {
            getViewBinding().adminUsersRefresh.setRefreshing(event.isLoading());

            if (event.isFailed() && !event.hasBeenHandled()) {
                event.handle();
                ErrorDialog.newInstance(getString(R.string.error_network_offline))
                        .show(getChildFragmentManager(), null);
            }

            if (event.isSuccessful()) {
                // TODO
            }
        });
    }
}
