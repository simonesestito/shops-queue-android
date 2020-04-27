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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.databinding.LoginFragmentBinding;
import com.simonesestito.shopsqueue.lifecycle.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.model.dto.input.AuthResponse;
import com.simonesestito.shopsqueue.model.dto.input.User;

public class LoginFragment extends AbstractAppFragment<LoginFragmentBinding> {
    @Override
    protected LoginFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return LoginFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // FIXME: Remove this
        new Handler().postDelayed(() -> new ViewModelProvider(requireActivity())
                .get(LoginViewModel.class)
                .setAuthStatus(
                        new AuthResponse(
                                null,
                                new User(
                                        0,
                                        null,
                                        null,
                                        null,
                                        "USER",
                                        null
                                )
                        )
                ), 1500);
    }
}
