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

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.databinding.AdminFragmentBinding;
import com.simonesestito.shopsqueue.ui.viewpager.AdminTabsAdapter;
import com.simonesestito.shopsqueue.util.ViewUtils;

public class AdminFragment extends AbstractAppFragment<AdminFragmentBinding> {
    @Override
    protected AdminFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AdminFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().adminViewPager.setAdapter(new AdminTabsAdapter(this));
        ViewUtils.setupTabsWithViewPager(
                getViewBinding().adminTabs,
                getViewBinding().adminViewPager,
                new int[]{R.string.admin_tab_users, R.string.admin_tab_shops}
        );
    }
}
