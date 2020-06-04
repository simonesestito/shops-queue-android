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

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.Product;
import com.simonesestito.shopsqueue.databinding.ProductListItemBinding;
import com.simonesestito.shopsqueue.util.ThemeUtils;

import java.util.Set;

public class ProductsAdapter extends DiffUtilAdapter<Product, ProductsAdapter.ViewHolder> {
    private MenuItemListener<Product> menuListener;
    private Set<Integer> selectedIds;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ProductListItemBinding binding = ProductListItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Product product = getItemAt(position);
        holder.binding.productEan.setText(product.getEan());
        holder.binding.productName.setText(product.getName());
        String displayPrice = holder.itemView.getContext().getString(R.string.price_label, product.getPrice());
        holder.binding.productPrice.setText(displayPrice);

        // We should use ColorStateList instead
        boolean isSelected = selectedIds != null && selectedIds.contains(product.getId());
        Activity activity = (Activity) holder.itemView.getContext();
        @ColorInt int background = isSelected
                ? ThemeUtils.getThemeColor(activity, R.attr.colorPrimaryVariant)
                : Color.TRANSPARENT;
        holder.binding.getRoot().setBackgroundColor(background);

        if (menuListener == null) {
            holder.binding.productMenu.setVisibility(View.GONE);
        } else {
            holder.binding.productMenu.setVisibility(View.VISIBLE);
            holder.binding.productMenu.setOnClickListener(menuIcon -> {
                PopupMenu popupMenu = new PopupMenu(menuIcon.getContext(), menuIcon);
                popupMenu.inflate(R.menu.list_popup_delete_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int index = holder.getAdapterPosition();
                    menuListener.onClick(menuItem, getItemAt(index));
                    return true;
                });
                popupMenu.show();
            });
        }
    }

    public void setSelectedIds(Set<Integer> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    public void setMenuListener(MenuItemListener<Product> menuListener) {
        this.menuListener = menuListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ProductListItemBinding binding;

        public ViewHolder(ProductListItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
