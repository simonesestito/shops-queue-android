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

package com.simonesestito.shopsqueue.di.module;

import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.api.dto.UserDetails;

import java.util.List;

public class ShopAdminDetails {
    private Shop shop;
    private List<UserDetails> owners;

    public ShopAdminDetails(Shop shop, List<UserDetails> owners) {
        this.shop = shop;
        this.owners = owners;
    }

    public Shop getShop() {
        return shop;
    }

    public List<UserDetails> getOwners() {
        return owners;
    }
}
