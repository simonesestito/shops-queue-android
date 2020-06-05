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
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.Product;
import com.simonesestito.shopsqueue.api.dto.ShoppingList;
import com.simonesestito.shopsqueue.databinding.OwnerOrdersItemBinding;
import com.simonesestito.shopsqueue.util.ThemeUtils;
import com.simonesestito.shopsqueue.util.ViewUtils;

public class OwnerOrdersAdapter extends DiffUtilAdapter<ShoppingList, OwnerOrdersAdapter.ViewHolder> {
    @ColorInt private int todoStatusColor;
    @ColorInt private int onTodoStatusColor;
    @ColorInt private int readyStatusColor;
    @ColorInt private int onReadyStatusColor;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (todoStatusColor == 0) {
            // Init theme colors
            todoStatusColor = ThemeUtils.getThemeColor(context, R.attr.colorError);
            onTodoStatusColor = ThemeUtils.getThemeColor(context, R.attr.colorOnError);
            readyStatusColor = ThemeUtils.getThemeColor(context, R.attr.colorPrimary);
            onReadyStatusColor = ThemeUtils.getThemeColor(context, R.attr.colorOnPrimary);
        }

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        OwnerOrdersItemBinding binding = OwnerOrdersItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Context context = holder.itemView.getContext();
        ShoppingList shoppingList = getItemAt(position);
        holder.binding.shoppingListUserName.setText(shoppingList.getUserName());
        String productsText = context.getResources().getQuantityString(
                R.plurals.products_count_label,
                shoppingList.getProducts().size(),
                shoppingList.getProducts().size()
        );
        holder.binding.shoppingListItemsCount.setText(productsText);

        String totalPrice = context.getString(R.string.total_price_label, shoppingList.getTotal());
        holder.binding.shoppingListTotal.setText(totalPrice);

        bindProductsList(context, holder.binding.shoppingListProductsList, shoppingList);
        bindShoppingListStatus(holder.binding.shoppingListStatus, shoppingList);
    }

    private void bindProductsList(Context context, TextView productsListView, ShoppingList shoppingList) {
        StringBuilder products = new StringBuilder();

        char newLine = 0;
        for (Product product : shoppingList.getProducts()) {
            // https://stackoverflow.com/a/3395345
            if (newLine == 0)
                newLine = '\n';
            else
                products.append(newLine);

            String productPrice = context.getString(R.string.price_label, product.getPrice());
            products.append("- ")
                    .append(product.getName())
                    .append(" (")
                    .append(productPrice)
                    .append(")");
        }

        productsListView.setText(products.toString());
    }

    private void bindShoppingListStatus(TextView statusView, ShoppingList shoppingList) {
        if (shoppingList.isReady()) {
            ViewUtils.setBackgroundTint(statusView, readyStatusColor);
            statusView.setTextColor(onReadyStatusColor);
            statusView.setText(R.string.shopping_list_status_ready);
        } else {
            ViewUtils.setBackgroundTint(statusView, todoStatusColor);
            statusView.setTextColor(onTodoStatusColor);
            statusView.setText(R.string.shopping_list_status_to_do);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private OwnerOrdersItemBinding binding;

        ViewHolder(OwnerOrdersItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
