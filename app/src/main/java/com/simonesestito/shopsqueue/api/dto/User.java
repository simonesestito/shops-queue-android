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

import com.simonesestito.shopsqueue.model.Identifiable;
import com.simonesestito.shopsqueue.model.UserRole;

import java.util.Objects;

public class User implements Identifiable {
    private int id;
    private String name;
    private String surname;
    private String email;
    private UserRole role;
    @Nullable private Integer shopId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Nullable
    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(@Nullable Integer shopId) {
        this.shopId = shopId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSurname(), getEmail(), getRole(), getShopId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId() &&
                getName().equals(user.getName()) &&
                getSurname().equals(user.getSurname()) &&
                getEmail().equals(user.getEmail()) &&
                getRole() == user.getRole() &&
                Objects.equals(getShopId(), user.getShopId());
    }
}
