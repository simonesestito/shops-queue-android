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

package com.simonesestito.shopsqueue.model;

import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.api.dto.Shop;

import java.util.List;

/**
 * Full POJO with all the info a shop owner should get access to.
 * It removes the needs of multiple different
 * LiveData, listeners, etc on the view side
 */
public class ShopOwnerDetails {
    private Shop shop;
    private List<Booking> queue;

    public ShopOwnerDetails(Shop shop, List<Booking> queue) {
        this.shop = shop;
        this.queue = queue;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<Booking> getQueue() {
        return queue;
    }

    public void setQueue(List<Booking> queue) {
        this.queue = queue;
    }
}
