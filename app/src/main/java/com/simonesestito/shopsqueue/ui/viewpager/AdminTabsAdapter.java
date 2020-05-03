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

package com.simonesestito.shopsqueue.ui.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.simonesestito.shopsqueue.ui.fragment.AbstractAppFragment;
import com.simonesestito.shopsqueue.ui.fragment.AdminShopsFragment;
import com.simonesestito.shopsqueue.ui.fragment.AdminUsersFragment;

public class AdminTabsAdapter extends FragmentStateAdapter {
    public AdminTabsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        AbstractAppFragment fragment;
        switch (position) {
            case 0:
                fragment = new AdminUsersFragment();
                break;
            case 1:
                fragment = new AdminShopsFragment();
                break;
            default:
                throw new IllegalArgumentException("Unknown index: " + position);
        }

        // They are below tabs, so the container's appbar
        // // must have elevation equal to zero
        fragment.requestUnelevatedAppbar();

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
