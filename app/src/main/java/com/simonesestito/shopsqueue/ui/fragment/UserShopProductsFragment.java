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
import com.simonesestito.shopsqueue.api.dto.NewShoppingList;
import com.simonesestito.shopsqueue.databinding.ProductsListBinding;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.ProductsAdapter;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.viewmodel.UserShopProductsViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import java.util.Set;

import javax.inject.Inject;

public class UserShopProductsFragment extends AbstractAppFragment<ProductsListBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private UserShopProductsViewModel viewModel;
    private ProductsAdapter adapter;
    private UserShopProductsFragmentArgs args;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        args = UserShopProductsFragmentArgs.fromBundle(requireArguments());
    }

    @NonNull
    @Override
    protected ProductsListBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return ProductsListBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(UserShopProductsViewModel.class);
        adapter = new ProductsAdapter();
        getViewBinding().productsList.setAdapter(adapter);
        ViewUtils.addDivider(getViewBinding().productsList);

        getViewBinding().addOwnerProductFab.setVisibility(View.GONE);
        onSelectedItem();

        viewModel.getProductsByShopId(args.getShopId()).observe(getViewLifecycleOwner(), event -> {
            if (event.isLoading()) {
                getViewBinding().productsLoading.setVisibility(View.VISIBLE);
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
            } else {
                requireActivity().onBackPressed();
            }
        });

        adapter.setItemClickListener(product -> {
            Set<Integer> selections = viewModel.getSelectedProductIds();
            if (selections.contains(product.getId()))
                selections.remove(product.getId());
            else
                selections.add(product.getId());
            adapter.setSelectedIds(selections);
            onSelectedItem();
        });

        getViewBinding().sendOrder.setOnClickListener(v -> {
            int[] productIds = new int[viewModel.getSelectedProductIds().size()];
            int i = 0;
            for (int id : viewModel.getSelectedProductIds())
                productIds[i++] = id;

            NewShoppingList shoppingList = new NewShoppingList(productIds);
            viewModel.sendOrder(shoppingList);
        });
    }

    private void onSelectedItem() {
        Set<Integer> selections = viewModel.getSelectedProductIds();
        if (selections.size() > 0) {
            getViewBinding().sendOrder.show();
        } else {
            getViewBinding().sendOrder.hide();
        }
    }
}
