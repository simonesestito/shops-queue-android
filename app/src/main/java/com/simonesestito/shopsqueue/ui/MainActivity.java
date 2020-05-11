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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.User;
import com.simonesestito.shopsqueue.util.ThemeUtils;
import com.simonesestito.shopsqueue.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Objects;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_AVAILABILITY_REQUEST_CODE = 1;
    @Inject ViewModelFactory factory;
    private NavController navController;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setSupportActionBar(findViewById(R.id.appToolbar));

        ShopsQueueApplication.getInjector().inject(this);
        loginViewModel = new ViewModelProvider(this, factory)
                .get(LoginViewModel.class);

        navController = Navigation.findNavController(this, R.id.navHostFragment);
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

        loginViewModel.getAuthStatus().observeUnhandled(this, event -> {
            if (event.isSuccessful()) {
                // Time to hide splash screen
                @ColorInt int backgroundColor = ThemeUtils.getThemeColor(this, android.R.attr.colorBackground);
                findViewById(R.id.navHostFragment).setBackgroundColor(backgroundColor);

                onNewAuthStatus(event.getData());
                if (event.getData() == null) {
                    // Cancel notifications on logout
                    NotificationManagerCompat.from(this).cancelAll();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int apiResult = api.isGooglePlayServicesAvailable(this);
        if (apiResult != ConnectionResult.SUCCESS) {
            // Google Play Services not available
            api.getErrorDialog(this, apiResult,
                    PLAY_SERVICES_AVAILABILITY_REQUEST_CODE,
                    d -> checkPlayServicesOrFinish()
            ).show();
        }
    }

    private void checkPlayServicesOrFinish() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int apiResult = api.isGooglePlayServicesAvailable(this);
        if (apiResult != ConnectionResult.SUCCESS) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemLogout:
                loginViewModel.logout();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_SERVICES_AVAILABILITY_REQUEST_CODE) {
            checkPlayServicesOrFinish();
        } else {
            handleFragmentActivityResult(requestCode, resultCode, data);

        }
    }

    private void handleFragmentActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Fragment firstFragment = getSupportFragmentManager()
                .getFragments()
                .get(0);
        if (!(firstFragment instanceof NavHostFragment))
            return;

        NavHostFragment navHostFragment = (NavHostFragment) firstFragment;
        Fragment currentFragment = navHostFragment
                .getChildFragmentManager()
                .getPrimaryNavigationFragment();
        if (currentFragment != null)
            currentFragment.onActivityResult(requestCode, resultCode, data);
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
