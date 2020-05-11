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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.BookingWithCount;
import com.simonesestito.shopsqueue.databinding.UserBookingItemBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserBookingsAdapter extends DiffUtilAdapter<BookingWithCount, UserBookingsAdapter.ViewHolder> {
    private static final String DATE_FORMAT = "HH:mm";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private MenuItemListener menuItemListener;

    @NonNull
    @Override
    public UserBookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UserBookingItemBinding binding = UserBookingItemBinding.inflate(inflater, parent, false);
        return new UserBookingsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Context context = holder.view.getRoot().getContext();
        BookingWithCount data = getItemAt(position);
        holder.view.bookingShopName.setText(data.getShop().getName());

        String formattedTime = dateFormat.format(data.getCreatedAt());
        String displayTime = context.getString(R.string.booking_item_created_at_date, formattedTime);
        holder.view.bookingCreatedAtDate.setText(displayTime);

        if (data.getQueueCount() == 0) {
            holder.view.bookingQueueCount.setText(R.string.user_your_turn_message);
        } else {
            String queueCount = context.getResources().getQuantityString(
                    R.plurals.user_booking_queue_count, data.getQueueCount(), data.getQueueCount());
            holder.view.bookingQueueCount.setText(queueCount);
        }

        if (menuItemListener == null) {
            holder.view.bookingItemMenu.setVisibility(View.GONE);
        } else {
            View.OnClickListener showMenuListener = v -> {
                PopupMenu popupMenu = new PopupMenu(
                        holder.view.bookingItemMenu.getContext(),
                        holder.view.bookingItemMenu);
                popupMenu.inflate(R.menu.admin_shops_popup_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int index = holder.getAdapterPosition();
                    BookingWithCount clickedBooking = getItemAt(index);
                    menuItemListener.onClick(menuItem, clickedBooking);
                    return true;
                });
                popupMenu.show();
            };
            holder.view.bookingItemMenu.setVisibility(View.VISIBLE);
            holder.view.bookingItemMenu.setOnClickListener(showMenuListener);
            holder.view.getRoot().setOnClickListener(showMenuListener);
        }
    }

    public void setMenuItemListener(MenuItemListener menuItemListener) {
        this.menuItemListener = menuItemListener;
    }

    public interface MenuItemListener {
        void onClick(MenuItem menuItem, BookingWithCount booking);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final UserBookingItemBinding view;

        ViewHolder(UserBookingItemBinding userBookingItemBinding) {
            super(userBookingItemBinding.getRoot());
            this.view = userBookingItemBinding;
        }
    }
}
