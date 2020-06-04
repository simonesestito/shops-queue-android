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
import com.simonesestito.shopsqueue.databinding.ProductsListBinding;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.OwnerProductsAdapter;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.viewmodel.OwnerProductsViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class OwnerProductsFragment extends AbstractAppFragment<ProductsListBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private OwnerProductsViewModel viewModel;
    private OwnerProductsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
    }

    @NonNull
    @Override
    protected ProductsListBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return ProductsListBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new OwnerProductsAdapter();
        getViewBinding().productsList.setAdapter(adapter);
        ViewUtils.addDivider(getViewBinding().productsList);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(OwnerProductsViewModel.class);
        viewModel.loadProducts();
        viewModel.getProducts().observe(this, event -> {
            if (event.isLoading()) {
                getViewBinding().productsLoading.setVisibility(View.VISIBLE);
                getViewBinding().productsList.setVisibility(View.INVISIBLE);
                getViewBinding().productsEmptyView.setVisibility(View.INVISIBLE);
                return;
            }

            getViewBinding().productsLoading.setVisibility(View.GONE);
            getViewBinding().productsList.setVisibility(View.VISIBLE);

            if (event.isFailed() && event.hasToBeHandled()) {
                event.handle();
                ErrorDialog.newInstance(requireContext(), event.getError())
                        .show(getChildFragmentManager(), null);
            } else if (event.isSuccessful() && event.getData() != null) {
                adapter.updateDataSet(event.getData());
                if (event.getData().isEmpty()) {
                    getViewBinding().productsEmptyView.setVisibility(View.VISIBLE);
                } else {
                    getViewBinding().productsEmptyView.setVisibility(View.GONE);
                }
            }
        });

        adapter.setItemClickListener(product -> {
            // TODO
        });

        adapter.setMenuListener((menuItem, item) -> {
            // TODO
        });

        getViewBinding().addOwnerProductFab.setOnClickListener(v -> {
            // TODO
        });
    }
}
