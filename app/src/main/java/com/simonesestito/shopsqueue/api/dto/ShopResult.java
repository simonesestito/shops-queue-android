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

package com.simonesestito.shopsqueue.api.dto;

public class ShopResult extends Shop {
    public ShopResult(Shop shop, boolean isFavourite) {
        this.isFavourite = isFavourite;
        setAddress(shop.getAddress());
        setCount(shop.getCount());
        setId(shop.getId());
        setLatitude(shop.getLatitude());
        setLongitude(shop.getLongitude());
        setName(shop.getName());
    }

    public ShopResult() {
    }

    private boolean isFavourite;

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShopResult)) return false;
        if (!super.equals(o)) return false;

        ShopResult that = (ShopResult) o;

        return isFavourite() == that.isFavourite();
    }
}
