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
import com.simonesestito.shopsqueue.api.dto.NewUser;
import com.simonesestito.shopsqueue.api.dto.Page;
import com.simonesestito.shopsqueue.api.dto.User;
import com.simonesestito.shopsqueue.api.dto.UserUpdate;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @POST("/users")
    ApiResponse<User> registerUser(@Body NewUser newUser);

    @GET("/users/{id}")
    ApiResponse<User> getUserById(@Path("id") int id);

    @DELETE("/users/{id}")
    ApiResponse<Void> deleteUser(@Path("id") int id);

    @GET("/users")
    ApiResponse<Page<User>> listUsers(@Query("page") int page/*, @Query("query") String name*/);

    @GET("/users")
    ApiResponse<Page<User>> listOwners(@Query("page") int page,
                                       @Query("shopId") int shopId);

    @PUT("/users/{id}")
    ApiResponse<User> updateUser(@Path("id") String id,
                                 @Body UserUpdate update);
}
