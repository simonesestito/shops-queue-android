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

package com.simonesestito.shopsqueue.util;

import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.simonesestito.shopsqueue.R;

import java.util.Objects;

/**
 * Navigation Component utilities
 */
public class NavUtils {
    /**
     * Navigate to a destination using the default transition.
     * Otherwise, it would have been manually added to every
     * possible action in the nav graph.
     *
     * @param fragment Current fragment, not the destination
     */
    public static void navigate(Fragment fragment, NavDirections directions) {
        NavOptions animationNavOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build();

        NavHostFragment.findNavController(fragment)
                .navigate(directions, animationNavOptions);
    }

    /**
     * https://developer.android.com/guide/navigation/navigation-programmatic#returning_a_result
     */
    @Nullable
    public static <T> T getFragmentResult(Fragment fragment, String key) {
        NavController navController = NavHostFragment.findNavController(fragment);
        NavBackStackEntry backStackEntry = navController.getCurrentBackStackEntry();
        Objects.requireNonNull(backStackEntry);
        return backStackEntry.getSavedStateHandle().get(key);
    }

    public static void setFragmentResult(Fragment fragment, String key, Parcelable value) {
        NavBackStackEntry backStackEntry = NavHostFragment
                .findNavController(fragment)
                .getPreviousBackStackEntry();
        Objects.requireNonNull(backStackEntry)
                .getSavedStateHandle()
                .set(key, value);
        fragment.requireActivity().onBackPressed();
    }
}
