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

import com.simonesestito.shopsqueue.api.dto.NewShop;
import com.simonesestito.shopsqueue.api.dto.UserDetails;
import com.simonesestito.shopsqueue.api.service.ShopService;
import com.simonesestito.shopsqueue.api.service.UserService;
import com.simonesestito.shopsqueue.di.module.ShopAdminDetails;
import com.simonesestito.shopsqueue.model.PickedLocation;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import java.util.List;

import javax.inject.Inject;

public class AdminShopEditViewModel extends ViewModel {
    private final LiveResource<ShopAdminDetails> liveShop = new LiveResource<>();
    private final UserService userService;
    private final ShopService shopService;
    public PickedLocation pickedLocation;

    @Inject
    AdminShopEditViewModel(UserService userService, ShopService shopService) {
        this.userService = userService;
        this.shopService = shopService;
    }

    public void loadShop(int liveShopId) {
        if (liveShop.getValue() != null
                && liveShop.getValue().getData() != null
                && liveShop.getValue().getData().getShop().getId() == liveShopId)
            return; // Already loaded

        if (liveShopId == 0) {
            liveShop.emitResult(null);
            return;
        }

        liveShop.emitLoading();
        shopService.getShopById(liveShopId)
                .onResult(shop -> userService
                        .listOwners(0, shop.getId())
                        .onResult(ownersPage -> {
                            // Fetch only the first page
                            List<UserDetails> owners = ownersPage.getData();
                            liveShop.emitResult(new ShopAdminDetails(shop, owners));
                        }).onError(liveShop::emitError))
                .onError(liveShop::emitError);
    }

    public void updateShop(int shopId, NewShop shopUpdate) {
        liveShop.emitLoading();
        shopService.updateShop(shopId, shopUpdate)
                .onResult(user -> liveShop.emitResult(null))
                .onError(liveShop::emitError);
    }

    public void saveNewShop(NewShop newShop) {
        liveShop.emitLoading();
        shopService.addNewShop(newShop)
                .onResult(user -> liveShop.emitResult(null))
                .onError(liveShop::emitError);
    }

    public LiveResource<ShopAdminDetails> getLiveShop() {
        return liveShop;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        liveShop.clearValue();
    }
}
