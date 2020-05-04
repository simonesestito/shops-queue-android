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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.User;
import com.simonesestito.shopsqueue.databinding.AdminUsersItemBinding;


public class AdminUsersAdapter extends DiffUtilAdapter<User, AdminUsersAdapter.ViewHolder> {
    private MenuItemListener menuItemListener;

    @NonNull
    @Override
    public AdminUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdminUsersItemBinding binding = AdminUsersItemBinding.inflate(inflater, parent, false);
        return new AdminUsersAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUsersAdapter.ViewHolder holder, int position) {
        User user = getItemAt(position);
        holder.view.userItemName.setText(user.getFullName());
        holder.view.userItemEmail.setText(user.getEmail());

        @DrawableRes int roleIcon;
        @StringRes int roleName;
        switch (user.getRole()) {
            case USER:
                roleIcon = R.drawable.ic_person_black_24dp;
                roleName = R.string.role_user;
                break;
            case ADMIN:
                roleIcon = R.drawable.ic_work_black_24dp;
                roleName = R.string.role_admin;
                break;
            case OWNER:
                roleIcon = R.drawable.ic_shopping_cart_black_24dp;
                roleName = R.string.role_owner;
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + user.getRole().toString());
        }
        holder.view.userItemIcon.setImageResource(roleIcon);
        holder.view.userItemRole.setText(roleName);

        if (menuItemListener == null) {
            holder.view.userItemMenu.setVisibility(View.GONE);
        } else {
            holder.view.userItemMenu.setVisibility(View.VISIBLE);
            holder.view.userItemMenu.setOnClickListener(menuIcon -> {
                PopupMenu popupMenu = new PopupMenu(menuIcon.getContext(), menuIcon);
                popupMenu.inflate(R.menu.admin_users_popup_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int index = holder.getAdapterPosition();
                    User clickedUser = getItemAt(index);
                    menuItemListener.onClick(menuItem, clickedUser.getId());
                    return true;
                });
                popupMenu.show();
            });
        }
    }

    public void setMenuItemListener(MenuItemListener menuItemListener) {
        this.menuItemListener = menuItemListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final AdminUsersItemBinding view;

        ViewHolder(AdminUsersItemBinding view) {
            super(view.getRoot());
            this.view = view;
        }
    }
}
