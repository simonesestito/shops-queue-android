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

import com.simonesestito.shopsqueue.api.dto.Page;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.api.service.ShopService;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;

public class ShopPickerViewModel extends ViewModel {
    private final LiveResource<Set<Shop>> shops = new LiveResource<>();
    private final ShopService shopService;
    private Page<Shop> lastPage;
    private final Set<Shop> lastShops = new LinkedHashSet<>();

    @Inject
    ShopPickerViewModel(ShopService shopService) {
        this.shopService = shopService;
        refreshShops();
    }

    public void refreshShops() {
        shops.emitLoading();
        shopService.getAllShops(0)
                .onResult(newPage -> {
                    lastPage = newPage;
                    lastShops.clear();
                    lastShops.addAll(newPage.getData());
                    shops.emitResult(lastShops);
                })
                .onError(err -> {
                    err.printStackTrace();
                    shops.emitError(err);
                });
    }

    public void loadNextPage() {
        if (shops.getValue() != null && shops.getValue().isLoading())
            return;

        if (lastPage != null && lastShops.size() == lastPage.getTotalItems())
            return;

        shops.emitLoading();
        int nextPage = lastPage == null ? 0 : lastPage.getPage() + 1;
        shopService.getAllShops(nextPage)
                .onResult(newPage -> {
                    lastPage = newPage;
                    lastShops.addAll(newPage.getData());
                    shops.emitResult(lastShops);
                })
                .onError(err -> {
                    err.printStackTrace();
                    shops.emitError(err);
                });
    }

    public LiveResource<Set<Shop>> getShops() {
        return shops;
    }
}
