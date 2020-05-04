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

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.simonesestito.shopsqueue.R;

public enum UserRole {
    USER,
    OWNER,
    ADMIN;

    @StringRes
    public int getDisplayName() {
        switch (this) {
            case ADMIN:
                return R.string.role_admin;
            case USER:
                return R.string.role_user;
            case OWNER:
                return R.string.role_owner;
            default:
                throw new IllegalArgumentException();
        }
    }

    @DrawableRes
    public int getIcon() {
        switch (this) {
            case ADMIN:
                return R.drawable.ic_work_black_24dp;
            case USER:
                return R.drawable.ic_person_black_24dp;
            case OWNER:
                return R.drawable.ic_shopping_cart_black_24dp;
            default:
                throw new IllegalArgumentException();
        }
    }
}
