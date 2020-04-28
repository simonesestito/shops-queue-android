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

package com.simonesestito.shopsqueue.api;

import com.simonesestito.shopsqueue.api.dto.Booking;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingService {
    @POST("/shops/{shopId}/bookings")
    CompletableFuture<Booking> addBookingToShop(@Path("shopId") int shopId);

    @GET("/shops/{shopId}/bookings")
    CompletableFuture<List<Booking>> getBookingsByShopId(@Path("shopId") int shopId);

    @GET("/users/{userId}/bookings")
    CompletableFuture<List<Booking>> getBookingsByUserId(@Path("userId") int userId);

    @DELETE("/bookings/{id}")
    CompletableFuture<Void> removeBooking(@Path("id") int bookingId);

    @POST("/shops/{shopId}/bookings/next")
    CompletableFuture<Booking> callNextUser(@Path("shopId") int shopId);
}
