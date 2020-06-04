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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.BookingWithCount;
import com.simonesestito.shopsqueue.api.dto.ShoppingList;
import com.simonesestito.shopsqueue.databinding.UserBookingItemBinding;
import com.simonesestito.shopsqueue.model.Identifiable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserBookingsAdapter extends DiffUtilAdapter<Identifiable, UserBookingsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_BOOKING = 1;
    private static final int VIEW_TYPE_ORDER = 2;
    private static final String DATE_FORMAT = "HH:mm";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private MenuItemListener<Identifiable> menuItemListener;

    @NonNull
    @Override
    public UserBookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UserBookingItemBinding binding = UserBookingItemBinding.inflate(inflater, parent, false);
        return new UserBookingsAdapter.ViewHolder(binding);
    }

    @Override
    public int getItemViewType(int position) {
        Identifiable item = getItemAt(position);
        if (item instanceof BookingWithCount)
            return VIEW_TYPE_BOOKING;
        else if (item instanceof ShoppingList)
            return VIEW_TYPE_ORDER;
        else
            throw new RuntimeException("Invalid object provided of type " + item.getClass().getName());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Identifiable data = getItemAt(position);
        if (data instanceof BookingWithCount)
            bindBooking(holder, (BookingWithCount) data);
        else if (data instanceof ShoppingList)
            bindShoppingList(holder, (ShoppingList) data);

        if (menuItemListener == null || (data instanceof ShoppingList && ((ShoppingList) data).isReady())) {
            holder.view.bookingItemMenu.setVisibility(View.GONE);
        } else {
            View.OnClickListener showMenuListener = v -> {
                PopupMenu popupMenu = new PopupMenu(
                        holder.view.bookingItemMenu.getContext(),
                        holder.view.bookingItemMenu);
                popupMenu.inflate(R.menu.list_popup_delete_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int index = holder.getAdapterPosition();
                    menuItemListener.onClick(menuItem, getItemAt(index));
                    return true;
                });
                popupMenu.show();
            };
            holder.view.bookingItemMenu.setVisibility(View.VISIBLE);
            holder.view.bookingItemMenu.setOnClickListener(showMenuListener);
            holder.view.getRoot().setOnClickListener(showMenuListener);
        }
    }

    private void bindBooking(ViewHolder holder, BookingWithCount data) {
        Context context = holder.view.getRoot().getContext();
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
    }

    private void bindShoppingList(ViewHolder holder, ShoppingList data) {
        Context context = holder.view.getRoot().getContext();
        holder.view.bookingShopName.setText(data.getShop().getName());

        int listCount = data.getProducts().size();
        String formattedTime = dateFormat.format(data.getCreatedAt());
        String displayTime = context.getString(R.string.shopping_order_created_at_date, listCount, formattedTime);
        holder.view.bookingCreatedAtDate.setText(displayTime);

        if (data.isReady()) {
            holder.view.bookingQueueCount.setText(R.string.shopping_order_ready);
        } else {
            holder.view.bookingQueueCount.setText(R.string.shopping_order_not_ready);

        }
    }

    public void setMenuItemListener(MenuItemListener<Identifiable> menuItemListener) {
        this.menuItemListener = menuItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final UserBookingItemBinding view;

        ViewHolder(UserBookingItemBinding userBookingItemBinding) {
            super(userBookingItemBinding.getRoot());
            this.view = userBookingItemBinding;
        }
    }
}
