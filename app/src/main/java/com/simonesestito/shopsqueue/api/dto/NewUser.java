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

import androidx.annotation.Nullable;

import com.simonesestito.shopsqueue.model.UserRole;

public class NewUser extends NewSimpleUser {
    @Nullable private Integer shopId;
    private UserRole role;
    private boolean active;

    public NewUser(String name, String surname, String email, String password, @Nullable Integer shopId, UserRole role, boolean active) {
        super(name, surname, email, password);
        this.shopId = shopId;
        this.role = role;
        this.active = active;
    }

    @Nullable
    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(@Nullable Integer shopId) {
        this.shopId = shopId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
