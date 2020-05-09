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
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.UserFavouriteShopsBinding;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.ShopsAdapter;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.viewmodel.UserFavouriteShopsViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class UserFavouriteShopsFragment extends AbstractAppFragment<UserFavouriteShopsBinding> {
    static final String SELECTED_SHOP_KEY = "picked_shop";
    @Inject ViewModelFactory viewModelFactory;
    private ShopsAdapter adapter = new ShopsAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    protected UserFavouriteShopsBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return UserFavouriteShopsBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().userFavouriteShopsList.setAdapter(adapter);
        adapter.setItemClickListener(clickedShop -> {
            NavUtils.setFragmentResult(this, SELECTED_SHOP_KEY, clickedShop);
        });
        ViewUtils.addDivider(getViewBinding().userFavouriteShopsList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserFavouriteShopsViewModel viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(UserFavouriteShopsViewModel.class);

        viewModel.getShops().observe(getViewLifecycleOwner(), event -> {
            getViewBinding().userFavouriteShopsLoading.setVisibility(
                    event.isLoading() ? View.VISIBLE : View.GONE
            );

            if (event.isFailed() && event.hasToBeHandled()) {
                event.handle();
                ErrorDialog.newInstance(requireContext(), event.getError())
                        .show(getChildFragmentManager(), null);
            } else if (event.isSuccessful()) {
                adapter.updateDataSet(event.getData());
            }
        });
    }
}
