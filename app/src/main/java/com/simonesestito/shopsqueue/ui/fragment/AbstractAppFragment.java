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

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.simonesestito.shopsqueue.util.InternetUtils;

/**
 * Base class for app fragments
 * It uses ViewBinding
 */
public abstract class AbstractAppFragment<T extends ViewBinding> extends Fragment {
    private static final String LATEST_INTERNET_CHECK = "internet_check";
    private boolean isAppbarHidden = false;
    private ConnectivityManager.NetworkCallback callback;
    private Handler uiThread = new Handler(Looper.getMainLooper());
    private boolean latestInternetCheck = true;
    private T viewBinding;

    protected abstract T onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container);

    @SuppressWarnings("WeakerAccess")
    protected void requestToHideAppbar() {
        isAppbarHidden = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                latestInternetCheck = false;
                uiThread.post(AbstractAppFragment.this::onOffline);
            }

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                latestInternetCheck = true;
                uiThread.post(AbstractAppFragment.this::onOnline);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = onCreateViewBinding(inflater, container);
        return viewBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(LATEST_INTERNET_CHECK, latestInternetCheck);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null)
            latestInternetCheck = true;
        else
            latestInternetCheck = savedInstanceState.getBoolean(LATEST_INTERNET_CHECK, true);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onStart() {
        super.onStart();
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (isAppbarHidden) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(requireContext(),
                ConnectivityManager.class);
        if (connectivityManager != null) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(networkRequest, callback);
        }

        // Check if there has been a change while in the background
        boolean currentCheck = InternetUtils.isOnline(requireContext());
        if (latestInternetCheck != currentCheck) {
            if (currentCheck) {
                onOnline();
            } else {
                onOffline();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(requireContext(),
                ConnectivityManager.class);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(callback);
        }
    }

    protected void onOnline() {
    }

    protected void onOffline() {
    }

    @SuppressWarnings("WeakerAccess")
    protected T getViewBinding() {
        return viewBinding;
    }
}
