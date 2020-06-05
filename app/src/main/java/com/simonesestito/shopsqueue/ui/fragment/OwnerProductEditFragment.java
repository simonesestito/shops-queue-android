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

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.api.dto.NewProduct;
import com.simonesestito.shopsqueue.api.dto.Product;
import com.simonesestito.shopsqueue.databinding.OwnerProductEditFragmentBinding;
import com.simonesestito.shopsqueue.util.ArrayUtils;
import com.simonesestito.shopsqueue.util.FormValidators;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;
import com.simonesestito.shopsqueue.viewmodel.OwnerProductsViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class OwnerProductEditFragment extends EditFragment<Product, OwnerProductEditFragmentBinding> {
    @Inject ViewModelFactory viewModelFactory;
    private OwnerProductEditFragmentArgs args;
    private OwnerProductsViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        args = OwnerProductEditFragmentArgs.fromBundle(requireArguments());
        viewModel = new ViewModelProvider(this, viewModelFactory).get(OwnerProductsViewModel.class);
    }

    @NonNull
    @Override
    protected OwnerProductEditFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return OwnerProductEditFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    protected void showData(@Nullable Product data) {
        super.showData(data);
        if (data == null) {
            requireActivity().setTitle(R.string.new_product_title);
        } else {
            requireActivity().setTitle(data.getName());
            getViewBinding().nameInput.setText(data.getName());
            getViewBinding().eanInput.setText(data.getEan());
            getViewBinding().priceInput.setText(String.valueOf(data.getPrice()));
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onSaveForm() {
        super.onSaveForm();
        // Validate input
        boolean isInputValid = ArrayUtils.allTrue(
                FormValidators.isString(getViewBinding().nameInputLayout),
                FormValidators.isEan(getViewBinding().eanInputLayout),
                FormValidators.isPrice(getViewBinding().priceInputLayout)
        );

        if (!isInputValid)
            return;

        String name = getViewBinding().nameInputLayout.getEditText().getText().toString().trim();
        String ean = getViewBinding().eanInputLayout.getEditText().getText().toString().trim();
        String priceString = getViewBinding().priceInputLayout.getEditText().getText().toString().trim();
        double price = Double.parseDouble(priceString);

        NewProduct newProduct = new NewProduct(name, ean, price);
        if (getArgumentId() == 0) {
            viewModel.addProduct(newProduct);
        } else {
            viewModel.editProduct(getArgumentId(), newProduct);
        }
    }

    @Override
    protected int getArgumentId() {
        return args.getProductId();
    }

    @Override
    protected View getSaveButton() {
        return getViewBinding().productSaveEdit;
    }

    @Override
    protected View getLoadingView() {
        return getViewBinding().contentLoading;
    }

    @Override
    protected void setFormVisibility(int visibility) {
        super.setFormVisibility(visibility);
        getViewBinding().productForm.setVisibility(visibility);
    }

    @Override
    protected LiveResource<Product> getLiveData() {
        return viewModel.getProductById(getArgumentId());
    }
}
