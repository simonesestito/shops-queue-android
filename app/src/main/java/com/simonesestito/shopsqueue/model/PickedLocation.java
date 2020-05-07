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

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.mapbox.mapboxsdk.geometry.LatLng;

@Keep
public class PickedLocation implements Parcelable {
    public static final Creator<PickedLocation> CREATOR = new Creator<PickedLocation>() {
        @Override
        public PickedLocation createFromParcel(Parcel in) {
            return new PickedLocation(in);
        }

        @Override
        public PickedLocation[] newArray(int size) {
            return new PickedLocation[size];
        }
    };
    private final double latitude;
    private final double longitude;
    private final String address;

    public PickedLocation(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    private PickedLocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }
}
