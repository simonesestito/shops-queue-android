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
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.User;
import com.simonesestito.shopsqueue.databinding.AdminUsersItemBinding;

public class AdminUsersAdapter extends DiffUtilAdapter<User, AdminUsersAdapter.ViewHolder> {
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
        holder.view.userItemName.setText(user.getName());
        holder.view.userItemEmail.setText(user.getEmail());

        @DrawableRes int roleIcon;
        switch (user.getRole()) {
            case USER:
                roleIcon = R.drawable.ic_person_add_black_24dp;
                break;
            case ADMIN:
                roleIcon = R.drawable.ic_work_black_24dp;
                break;
            case OWNER:
                roleIcon = R.drawable.ic_shopping_cart_black_24dp;
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + user.getRole().toString());
        }
        holder.view.userItemIcon.setImageResource(roleIcon);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final AdminUsersItemBinding view;

        public ViewHolder(AdminUsersItemBinding view) {
            super(view.getRoot());
            this.view = view;
        }
    }
}
