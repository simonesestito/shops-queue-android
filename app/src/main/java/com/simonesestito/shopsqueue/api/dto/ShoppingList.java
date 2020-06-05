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

import android.os.Parcel;
import android.os.Parcelable;

import com.simonesestito.shopsqueue.model.Identifiable;

import java.util.Date;
import java.util.List;


public class ShoppingList implements Identifiable, Parcelable {
    private int id;
    private Date createdAt;
    private int userId;
    private String userName;
    private boolean isReady;
    private double total;
    private Shop shop;
    private List<Product> products;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public static final Creator<ShoppingList> CREATOR = new Creator<ShoppingList>() {
        @Override
        public ShoppingList createFromParcel(Parcel in) {
            return new ShoppingList(in);
        }

        @Override
        public ShoppingList[] newArray(int size) {
            return new ShoppingList[size];
        }
    };

    //region Parcelable
    protected ShoppingList(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        userName = in.readString();
        isReady = in.readByte() != 0;
        total = in.readDouble();
        shop = in.readParcelable(Shop.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeByte((byte) (isReady ? 1 : 0));
        dest.writeDouble(total);
        dest.writeParcelable(shop, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    //endregion
}
