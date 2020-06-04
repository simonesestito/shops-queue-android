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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.databinding.AdminShopItemBinding;


public class ShopsAdapter extends DiffUtilAdapter<Shop, ShopsAdapter.ViewHolder> {
    private MenuItemListener<Shop> menuItemListener;

    @NonNull
    @Override
    public ShopsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdminShopItemBinding binding = AdminShopItemBinding.inflate(inflater, parent, false);
        return new ShopsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopsAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Shop shop = getItemAt(position);
        holder.view.shopItemAddress.setText(shop.getAddress());
        holder.view.shopItemName.setText(shop.getName());

        String queueDisplayCount = holder.view.getRoot().getContext()
                .getResources()
                .getQuantityString(R.plurals.shop_queue_count, shop.getCount(), shop.getCount());
        holder.view.shopItemQueueCount.setText(queueDisplayCount);

        if (menuItemListener == null) {
            holder.view.userItemMenu.setVisibility(View.GONE);
        } else {
            holder.view.userItemMenu.setVisibility(View.VISIBLE);
            holder.view.userItemMenu.setOnClickListener(menuIcon -> {
                PopupMenu popupMenu = new PopupMenu(menuIcon.getContext(), menuIcon);
                popupMenu.inflate(R.menu.admin_shops_popup_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int index = holder.getAdapterPosition();
                    Shop clickedShop = getItemAt(index);
                    menuItemListener.onClick(menuItem, clickedShop);
                    return true;
                });
                popupMenu.show();
            });
        }
    }

    public void setMenuItemListener(MenuItemListener<Shop> menuItemListener) {
        this.menuItemListener = menuItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AdminShopItemBinding view;

        ViewHolder(AdminShopItemBinding view) {
            super(view.getRoot());
            this.view = view;
        }
    }
}
