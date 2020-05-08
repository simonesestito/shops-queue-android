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

public class UserUpdate {
    private String name;
    private String surname;
    private String email;
    @Nullable private String password;
    @Nullable private Integer shopId;
    private UserRole role;

    public UserUpdate(String name, String surname, String email, @Nullable String password, @Nullable Integer shopId, UserRole role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.shopId = shopId;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
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
}
