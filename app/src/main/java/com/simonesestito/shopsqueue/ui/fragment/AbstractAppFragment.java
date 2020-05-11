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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.util.ThemeUtils;

import java.util.Objects;

/**
 * Base class for app fragments
 * It uses ViewBinding
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractAppFragment<T extends ViewBinding> extends Fragment {
    private boolean isAppbarHidden = false;
    private boolean isAppbarElevated = true;
    private T viewBinding;

    @NonNull
    protected abstract T onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container);

    protected void requestToHideAppbar() {
        isAppbarHidden = true;
    }

    public void requestUnelevatedAppbar() {
        isAppbarElevated = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = onCreateViewBinding(inflater, container);
        return viewBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        Objects.requireNonNull(actionBar);

        if (isAppbarHidden) {
            actionBar.hide();
        } else {
            actionBar.show();
        }

        if (isAppbarElevated || ThemeUtils.isDarkTheme(requireContext())) {
            actionBar.setElevation(activity.getResources().getDimension(R.dimen.default_appbar_elevation));
        } else {
            actionBar.setElevation(0f);
        }
    }

    protected T getViewBinding() {
        return viewBinding;
    }
}
