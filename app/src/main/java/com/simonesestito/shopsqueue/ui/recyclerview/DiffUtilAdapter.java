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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.model.Identifiable;

import java.util.Objects;

public abstract class DiffUtilAdapter<T extends Identifiable, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private final AsyncListDiffer<T> listDiffer;

    public DiffUtilAdapter() {
        listDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<T>() {
            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return Objects.equals(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem.getId() == newItem.getId();
            }
        });
    }
}
