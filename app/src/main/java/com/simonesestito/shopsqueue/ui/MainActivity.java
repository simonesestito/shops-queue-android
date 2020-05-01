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

package com.simonesestito.shopsqueue.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.User;
import com.simonesestito.shopsqueue.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Objects;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    @Inject ViewModelFactory factory;
    private NavController navController;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ShopsQueueApplication.getInjector().inject(this);
        loginViewModel = new ViewModelProvider(this, factory)
                .get(LoginViewModel.class);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            // Check if it's a root destination
            NavGraph destinationGraph = destination.getParent();
            boolean isRootDestination = destinationGraph != null &&
                    destinationGraph.getStartDestination() == destination.getId();
            // Update back button accordingly
            Objects.requireNonNull(getSupportActionBar())
                    .setDisplayHomeAsUpEnabled(!isRootDestination);

            // Update activity title
            setTitle(destination.getLabel());
        });

        loginViewModel.getAuthStatus().observeUnhandled(this, user -> {
            onNewAuthStatus(user);
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItemLogout) {
            loginViewModel.logout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void onNewAuthStatus(User currentUser) {
        @IdRes int destinationGraph = 0;

        if (currentUser == null) {
            destinationGraph = R.id.login_graph;
        } else {
            switch (currentUser.getRole()) {
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
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build();

        navController.navigate(destinationGraph, null, navOptions);
    }
}
