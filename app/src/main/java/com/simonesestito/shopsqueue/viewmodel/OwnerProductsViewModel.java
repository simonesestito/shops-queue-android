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

package com.simonesestito.shopsqueue.viewmodel;

import androidx.lifecycle.ViewModel;

import com.simonesestito.shopsqueue.api.dto.NewProduct;
import com.simonesestito.shopsqueue.api.dto.Product;
import com.simonesestito.shopsqueue.api.service.ProductService;
import com.simonesestito.shopsqueue.model.AuthUserHolder;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import java.util.List;

import javax.inject.Inject;

public class OwnerProductsViewModel extends ViewModel {
    private final ProductService productService;
    private LiveResource<List<Product>> products = new LiveResource<>();
    private LiveResource<Product> singleProduct = new LiveResource<>();
    private int singleProductId;

    @Inject
    OwnerProductsViewModel(ProductService productService) {
        this.productService = productService;
    }

    @SuppressWarnings("ConstantConditions")
    public void loadProducts() {
        int shopId = AuthUserHolder.getCurrentUser().getShopId();
        products.emitLoading();
        productService.getProductsByShopId(shopId)
                .onResult(products::emitResult)
                .onError(products::emitError);
    }

    public LiveResource<List<Product>> getProducts() {
        return products;
    }

    public LiveResource<Product> getProductById(int id) {
        if (id != singleProductId) {
            singleProductId = id;
            productService.getProduct(id)
                    .onResult(singleProduct::emitResult)
                    .onError(singleProduct::emitError);
        }
        return singleProduct;
    }

    public void deleteProduct(int productId) {
        products.emitLoading();
        productService.deleteProductFromMyShop(productId)
                .onResult(v -> loadProducts())
                .onError(products::emitError);
    }

    public void editProduct(int productId, NewProduct newProduct) {
        singleProduct.emitLoading();
        productService.editProductOfMyShop(productId, newProduct)
                .onResult(v -> singleProduct.emitResult(null))
                .onError(singleProduct::emitError);
    }

    public void addProduct(NewProduct newProduct) {
        singleProduct.emitLoading();
        productService.addProductToMyShop(newProduct)
                .onResult(v -> singleProduct.emitResult(null))
                .onError(singleProduct::emitError);
    }
}
