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

package com.simonesestito.shopsqueue.ui.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.databinding.OwnerBookingItemBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OwnerBookingsAdapter extends DiffUtilAdapter<Booking, OwnerBookingsAdapter.ViewHolder> {
    private static final String DATE_FORMAT = "HH:mm:ss";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        OwnerBookingItemBinding binding = OwnerBookingItemBinding.inflate(inflater, parent, false);
        return new OwnerBookingsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = getItemAt(position);
        OwnerBookingItemBinding view = holder.ownerBookingItemBinding;
        Context context = view.getRoot().getContext();

        view.bookingItemUserName.setText(booking.getUser().getName());

        String formattedTime = dateFormat.format(booking.getCreatedAt());
        String displayTime = context.getString(R.string.booking_item_created_at_date, formattedTime);
        view.bookingItemDate.setText(displayTime);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final OwnerBookingItemBinding ownerBookingItemBinding;

        ViewHolder(OwnerBookingItemBinding ownerBookingItemBinding) {
            super(ownerBookingItemBinding.getRoot());
            this.ownerBookingItemBinding = ownerBookingItemBinding;
        }
    }
}
