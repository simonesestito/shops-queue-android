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

package com.simonesestito.shopsqueue.api.service;

import com.simonesestito.shopsqueue.api.ApiResponse;
import com.simonesestito.shopsqueue.api.dto.NewProduct;
import com.simonesestito.shopsqueue.api.dto.Product;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductService {
    @GET("/shops/{shopId}/products")
    ApiResponse<List<Product>> getProductsByShopId(@Path("shopId") int shopId);

    @POST("/products")
    ApiResponse<Product> addProductToMyShop(@Body NewProduct newProduct);

    @GET("/products/{id}")
    ApiResponse<Product> getProduct(@Path("id") int productId);

    @DELETE("/products/{id}")
    ApiResponse<Void> deleteProductFromMyShop(@Path("id") int productId);

    @PUT("/products/{id}")
    ApiResponse<Product> editProductOfMyShop(@Path("id") int productId, @Body NewProduct newProduct);
}
