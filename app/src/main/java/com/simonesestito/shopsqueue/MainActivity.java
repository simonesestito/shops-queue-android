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

package com.simonesestito.shopsqueue;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.simonesestito.shopsqueue.lifecycle.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.model.dto.input.AuthResponse;

public class MainActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getAuthStatus().observe(this, (AuthResponse a) -> {
            @IdRes int destinationGraph = 0;

            if (a == null) {
                destinationGraph = R.id.login_graph;
            } else {
                switch (a.getUser().getRole()) {
                    case USER:
                        destinationGraph = R.id.user_graph;
                        break;
                    case OWNER:
                        destinationGraph = R.id.owner_graph;
                        break;
                    case ADMIN:
                        destinationGraph = R.id.admin_graph;
                        break;
                }
            }

            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.main_graph, true)
                    .build();

            Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(destinationGraph, null, navOptions);
        });
    }
}
