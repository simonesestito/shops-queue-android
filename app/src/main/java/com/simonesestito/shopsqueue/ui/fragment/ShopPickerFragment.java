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
import com.simonesestito.shopsqueue.databinding.ShopPickerBinding;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.ShopsAdapter;
import com.simonesestito.shopsqueue.util.NavUtils;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.viewmodel.ShopPickerViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Objects;

import javax.inject.Inject;

public class ShopPickerFragment extends AbstractAppFragment<ShopPickerBinding> {
    static final String PICKED_SHOP_KEY = "picked_shop";
    @Inject ViewModelFactory viewModelFactory;
    private ShopPickerViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @NonNull
    @Override
    protected ShopPickerBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return ShopPickerBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ShopsAdapter adapter = new ShopsAdapter();
        adapter.setItemClickListener(shop -> {
            NavUtils.setFragmentResult(this, PICKED_SHOP_KEY, shop);
        });
        getViewBinding().adminShopsList.setAdapter(adapter);

        ViewUtils.addDivider(getViewBinding().adminShopsList);
        getViewBinding().adminListRefresh.setOnRefreshListener(() -> viewModel.refreshShops());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ShopPickerViewModel.class);

        ViewUtils.onRecyclerViewLoadMore(getViewBinding().adminShopsList, viewModel::loadNextPage);
        viewModel.getShops().observe(getViewLifecycleOwner(), event -> {
            getViewBinding().adminListRefresh.setRefreshing(event.isLoading());

            if (event.isFailed() && event.hasToBeHandled()) {
                event.handle();
                ErrorDialog.newInstance(requireContext(), event.getError())
                        .show(getChildFragmentManager(), null);
            } else if (event.isSuccessful()) {
                ShopsAdapter adapter = (ShopsAdapter) getViewBinding().adminShopsList.getAdapter();
                Objects.requireNonNull(adapter).updateDataSet(event.getData());
            }
        });
    }
}
