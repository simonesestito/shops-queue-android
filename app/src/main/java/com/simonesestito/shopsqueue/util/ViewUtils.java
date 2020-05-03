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

import androidx.annotation.StringRes;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewUtils {
    /**
     * Sync a TabLayout with a ViewPager updating the tabs title
     *
     * @param tabs Titles of the tabs
     */
    public static void setupTabsWithViewPager(TabLayout tabLayout, ViewPager2 viewPager, @StringRes int[] tabs) {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabLayout.getContext().getString(tabs[position]));
        }).attach();
    }
}
