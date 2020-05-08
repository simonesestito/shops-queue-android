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

import com.simonesestito.shopsqueue.api.dto.BookingWithCount;
import com.simonesestito.shopsqueue.api.dto.ShopWithDistance;
import com.simonesestito.shopsqueue.api.service.BookingService;
import com.simonesestito.shopsqueue.api.service.ShopService;
import com.simonesestito.shopsqueue.model.AuthUserHolder;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class UserMainViewModel extends ViewModel {
    private final BookingService bookingService;
    private final ShopService shopService;
    private LiveResource<List<BookingWithCount>> bookings = new LiveResource<>();
    private LiveResource<Set<ShopWithDistance>> shops = new LiveResource<>();
    private Set<ShopWithDistance> lastShops = new LinkedHashSet<>();
    private String query;

    @Inject
    UserMainViewModel(BookingService bookingService, ShopService shopService) {
        this.bookingService = bookingService;
        this.shopService = shopService;
        loadBookings();
    }

    public void loadNearShops(double lat, double lon) {
        shops.emitLoading();
        shopService.getShopsNearby(lat, lon, query)
                .onResult(result -> {
                    lastShops.addAll(result);
                    shops.emitResult(lastShops);
                })
                .onError(err -> {
                    shops.emitError(err);
                });
    }

    public void loadBookings() {
        bookings.emitLoading();
        bookingService.getBookingsByUserId(AuthUserHolder.getCurrentUser().getId())
                .onResult(bookings::emitResult)
                .onError(bookings::emitError);
    }

    public void book(int shopId) {
        bookings.emitLoading();
        bookingService.addBookingToShop(shopId)
                .onResult(booking -> loadBookings())
                .onError(err -> {
                    bookings.emitError(err);
                    loadBookings();
                });
    }

    public void deleteBooking(int id) {
        bookings.emitLoading();
        bookingService.deleteBooking(id)
                .onResult(v -> loadBookings())
                .onError(err -> {
                    bookings.emitError(err);
                    loadBookings();
                });
    }

    public void searchShops(double lat, double lon, String query) {
        this.query = query;
        lastShops.clear();
        loadNearShops(lat, lon);
    }

    public void clearQuery() {
        query = "";
    }

    public LiveResource<List<BookingWithCount>> getBookings() {
        return bookings;
    }

    public LiveResource<Set<ShopWithDistance>> getShops() {
        return shops;
    }
}
