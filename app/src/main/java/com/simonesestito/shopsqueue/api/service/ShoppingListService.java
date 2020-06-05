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
import com.simonesestito.shopsqueue.api.dto.NewShoppingList;
import com.simonesestito.shopsqueue.api.dto.ShoppingList;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ShoppingListService {
    @POST("/lists")
    ApiResponse<ShoppingList> createShoppingList(@Body NewShoppingList newShoppingList);

    @GET("/users/me/lists")
    ApiResponse<List<ShoppingList>> getMyShoppingLists();

    @GET("/shops/me/lists")
    ApiResponse<List<ShoppingList>> getMyShopOrders();

    @DELETE("/lists/{id}")
    ApiResponse<Void> deleteShoppingList(@Path("id") int listId);

    @POST("/lists/{id}")
    ApiResponse<ShoppingList> prepareShoppingList(@Path("id") int listId);
}
