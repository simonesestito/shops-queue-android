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

package com.simonesestito.shopsqueue.model.dto.input;

import androidx.annotation.Nullable;

import com.simonesestito.shopsqueue.model.UserRole;

public class User {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String role;
    @Nullable private Integer shopId;

    public User(int id, String name, String surname, String email, String role, @Nullable Integer shopId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.shopId = shopId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return UserRole.valueOf(role);
    }

    @Nullable
    public Integer getShopId() {
        return shopId;
    }
}
