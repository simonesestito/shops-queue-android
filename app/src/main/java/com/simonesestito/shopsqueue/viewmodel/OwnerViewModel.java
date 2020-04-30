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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.api.service.BookingService;
import com.simonesestito.shopsqueue.api.service.ShopService;

import javax.inject.Inject;

public class OwnerViewModel extends ViewModel {
    private final ShopService shopService;
    private final BookingService bookingService;
    private final MutableLiveData<Shop> currentShop = new MutableLiveData<>();
    private final MutableLiveData<Booking> currentUser = new MutableLiveData<>(null);

    @Inject
    OwnerViewModel(ShopService shopService, BookingService bookingService) {
        this.shopService = shopService;
        this.bookingService = bookingService;
        init();
    }

    private void init() {
        this.shopService.getOwnShop()
                .onResult(currentShop::setValue)
                .onError(err -> {
                    currentShop.setValue(null);
                    err.printStackTrace();
                });
    }

    public void callNextUser() {
        // TODO
    }

    public LiveData<Shop> getCurrentShop() {
        return currentShop;
    }

    public LiveData<Booking> getCurrentCalledUser() {
        return currentUser;
    }
}
