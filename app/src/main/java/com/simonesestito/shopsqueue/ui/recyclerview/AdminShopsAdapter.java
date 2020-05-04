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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.databinding.AdminShopItemBinding;


public class AdminShopsAdapter extends DiffUtilAdapter<Shop, AdminShopsAdapter.ViewHolder> {
    private MenuItemListener menuItemListener;

    @NonNull
    @Override
    public AdminShopsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdminShopItemBinding binding = AdminShopItemBinding.inflate(inflater, parent, false);
        return new AdminShopsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminShopsAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Shop shop = getItemAt(position);
        holder.view.shopItemAddress.setText(shop.getAddress());
        holder.view.shopItemName.setText(shop.getName());

        int queueCount = shop.getCount();
        String queueDisplayCount = holder.view.getRoot().getContext()
                .getString(R.string.shop_queue_count, queueCount);
        holder.view.shopItemQueueCount.setText(queueDisplayCount);

        if (menuItemListener == null) {
            holder.view.userItemMenu.setVisibility(View.GONE);
        } else {
            holder.view.userItemMenu.setVisibility(View.VISIBLE);
            holder.view.userItemMenu.setOnClickListener(menuIcon -> {
                PopupMenu popupMenu = new PopupMenu(menuIcon.getContext(), menuIcon);
                popupMenu.inflate(R.menu.admin_users_popup_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int index = holder.getAdapterPosition();
                    Shop clickedShop = getItemAt(index);
                    menuItemListener.onClick(menuItem, clickedShop.getId());
                    return true;
                });
                popupMenu.show();
            });
        }
    }

    public void setMenuItemListener(MenuItemListener menuItemListener) {
        this.menuItemListener = menuItemListener;
    }

    public interface MenuItemListener {
        void onClick(MenuItem menuItem, int userId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final AdminShopItemBinding view;

        ViewHolder(AdminShopItemBinding view) {
            super(view.getRoot());
            this.view = view;
        }
    }
}
