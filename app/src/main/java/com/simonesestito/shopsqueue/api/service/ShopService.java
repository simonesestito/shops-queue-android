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

import com.simonesestito.shopsqueue.api.dto.NewShop;
import com.simonesestito.shopsqueue.api.dto.Page;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.api.dto.ShopWithDistance;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShopService {
    @POST("/shops")
    CompletableFuture<Shop> addNewShop(NewShop newShop);

    @GET("/shops/nearby")
    CompletableFuture<Page<ShopWithDistance>> getShopsNearby(@Query("page") int page,
                                                             @Query("lat") double latitude,
                                                             @Query("lon") double longitude);

    @PUT("/shops/{id}")
    CompletableFuture<Shop> updateShop(@Path("id") int id, NewShop newShop);

    @DELETE("/shops/{id}")
    CompletableFuture<Void> deleteShop(@Path("id") int id);

    @GET("/shops")
    CompletableFuture<Page<Shop>> getAllShops(@Query("page") int page);
}
