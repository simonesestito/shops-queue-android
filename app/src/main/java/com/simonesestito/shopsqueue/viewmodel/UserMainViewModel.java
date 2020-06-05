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

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.simonesestito.shopsqueue.api.ApiResponse;
import com.simonesestito.shopsqueue.api.dto.ShopResult;
import com.simonesestito.shopsqueue.api.service.BookingService;
import com.simonesestito.shopsqueue.api.service.FavouritesService;
import com.simonesestito.shopsqueue.api.service.ShopService;
import com.simonesestito.shopsqueue.api.service.ShoppingListService;
import com.simonesestito.shopsqueue.model.AuthUserHolder;
import com.simonesestito.shopsqueue.model.Identifiable;
import com.simonesestito.shopsqueue.util.NumberUtils;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class UserMainViewModel extends ViewModel {
    private final BookingService bookingService;
    private final ShopService shopService;
    private final FavouritesService favouritesService;
    private final ShoppingListService shoppingListService;
    private LiveResource<List<Identifiable>> bookings = new LiveResource<>();
    private LiveResource<Set<ShopResult>> shops = new LiveResource<>();
    private Set<ShopResult> lastShops = new LinkedHashSet<>();
    private String query;
    private LatLng lastUserLocation = new LatLng(0, 0);

    @Inject
    UserMainViewModel(BookingService bookingService,
                      ShopService shopService,
                      FavouritesService favouritesService,
                      ShoppingListService shoppingListService) {
        this.bookingService = bookingService;
        this.shopService = shopService;
        this.favouritesService = favouritesService;
        this.shoppingListService = shoppingListService;
        loadBookings();
    }

    private void loadNearShops() {
        loadNearShops(lastUserLocation.getLatitude(), lastUserLocation.getLongitude());
    }

    public void loadNearShops(double lat, double lon) {
        lat = NumberUtils.roundCoordinate(lat);
        lon = NumberUtils.roundCoordinate(lon);
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
                .onResult(bookings -> {
                    List<Identifiable> result = new ArrayList<>(bookings);
                    shoppingListService.getMyShoppingLists()
                            .onResult(lists -> {
                                result.addAll(lists);
                                this.bookings.emitResult(result);
                            })
                            .onError(this.bookings::emitError);
                })
                .onError(bookings::emitError);
    }

    public void book(int shopId) {
        bookings.emitLoading();
        bookingService.addBookingToShop(shopId)
                .onResult(booking -> {
                    loadBookings();
                    loadNearShops();
                })
                .onError(err -> {
                    bookings.emitError(err);
                    loadBookings();
                });
    }

    public void cancelBooking(int id) {
        bookings.emitLoading();
        bookingService.deleteBooking(id)
                .onResult(v -> loadBookings())
                .onError(err -> {
                    bookings.emitError(err);
                    loadBookings();
                });
    }

    public void cancelOrder(int order) {
        bookings.emitLoading();
        shoppingListService.deleteShoppingList(order)
                .onResult(v -> loadBookings())
                .onError(err -> {
                    bookings.emitError(err);
                    loadBookings();
                });
    }

    public void setFavouriteShop(int shopId, boolean isFavourite) {
        shops.emitLoading();
        int userId = AuthUserHolder.getCurrentUser().getId();
        ApiResponse<Void> response = isFavourite
                ? favouritesService.addShopToUserFavourites(userId, shopId)
                : favouritesService.removeShopFromUserFavourites(userId, shopId);
        response.onResult(v -> loadNearShops())
                .onError(err -> {
                    shops.emitError(err);
                    loadNearShops();
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

    public LatLng getLastUserLocation() {
        return lastUserLocation;
    }

    public void updateUserLocation(double lat, double lon) {
        lastUserLocation = new LatLng(lat, lon);
        loadNearShops(lat, lon);
    }

    public LiveResource<List<Identifiable>> getAllBookings() {
        return bookings;
    }

    public LiveResource<Set<ShopResult>> getShops() {
        return shops;
    }
}
