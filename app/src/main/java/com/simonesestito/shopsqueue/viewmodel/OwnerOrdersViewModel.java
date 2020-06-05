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

import com.simonesestito.shopsqueue.api.dto.ShoppingList;
import com.simonesestito.shopsqueue.api.service.ShoppingListService;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import java.util.List;

import javax.inject.Inject;

public class OwnerOrdersViewModel extends ViewModel {
    private final ShoppingListService shoppingListService;
    private LiveResource<List<ShoppingList>> shoppingLists = new LiveResource<>();

    @Inject
    OwnerOrdersViewModel(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
        loadOrders();
    }

    public void loadOrders() {
        shoppingLists.emitLoading();
        shoppingListService.getMyShopOrders()
                .onResult(shoppingLists::emitResult)
                .onError(shoppingLists::emitError);
    }

    public void prepareOrder(int orderId) {
        shoppingLists.emitLoading();
        shoppingListService.prepareShoppingList(orderId)
                .onResult(list -> loadOrders())
                .onError(shoppingLists::emitError);
    }

    public void deleteOrder(int orderId) {
        shoppingLists.emitLoading();
        shoppingListService.deleteShoppingList(orderId)
                .onResult(list -> loadOrders())
                .onError(shoppingLists::emitError);
    }

    public LiveResource<List<ShoppingList>> getShoppingLists() {
        return shoppingLists;
    }
}
