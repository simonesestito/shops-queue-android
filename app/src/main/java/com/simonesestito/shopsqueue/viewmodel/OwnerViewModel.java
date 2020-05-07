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

import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.api.service.BookingService;
import com.simonesestito.shopsqueue.api.service.ShopService;
import com.simonesestito.shopsqueue.model.ShopOwnerDetails;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import javax.inject.Inject;

public class OwnerViewModel extends ViewModel {
    private final ShopService shopService;
    private final BookingService bookingService;
    // Full data exposed to the UI
    private final LiveResource<ShopOwnerDetails> shopData = new LiveResource<>();
    // Cached data, internal in the ViewModel
    private Shop currentShop;
    private Booking latestCustomer;

    @Inject
    OwnerViewModel(ShopService shopService, BookingService bookingService) {
        this.shopService = shopService;
        this.bookingService = bookingService;
        loadAllData();
    }

    private void loadAllData() {
        shopData.emitLoading();

        // Load shop info
        shopService.getOwnShop()
                // Then, load bookings
                .onResult(shop -> {
                    currentShop = shop;
                    refreshBookings();
                })
                .onError(err -> {
                    shopData.emitError(err);
                    err.printStackTrace();
                });
    }

    public void refreshBookings() {
        if (currentShop == null)
            return;

        shopData.emitLoading();
        bookingService.getBookingsByShopId(currentShop.getId())
                .onResult(bookings -> shopData.emitResult(new ShopOwnerDetails(
                        currentShop, latestCustomer, bookings
                )))
                .onError(err -> {
                    err.printStackTrace();
                    shopData.emitError(err);
                });
    }

    public void callNextUser() {
        shopData.emitLoading();
        bookingService.callNextUser(currentShop.getId())
                .onResult(user -> {
                    latestCustomer = user;
                    refreshBookings();
                })
                .onError(err -> {
                    latestCustomer = null;
                    refreshBookings();
                    err.printStackTrace();
                });
    }

    public void cancelAllBookings() {
        shopData.emitLoading();
        bookingService.deleteBookingsByShop(currentShop.getId())
                .onResult(v -> {
                    refreshBookings();
                })
                .onError(err -> {
                    refreshBookings();
                    err.printStackTrace();
                });
    }

    public LiveResource<ShopOwnerDetails> getShopData() {
        return shopData;
    }
}
