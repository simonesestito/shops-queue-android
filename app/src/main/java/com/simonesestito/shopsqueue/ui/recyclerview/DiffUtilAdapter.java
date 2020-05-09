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

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.model.Identifiable;
import com.simonesestito.shopsqueue.util.functional.Callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public abstract class DiffUtilAdapter<T extends Identifiable, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private final AsyncListDiffer<T> listDiffer;
    private Callback<T> clickListener;

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

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.itemView.setTag(getItemAt(position));
        holder.itemView.setOnClickListener(v -> {
            T clickedItem = (T) v.getTag();
            if (clickListener != null)
                clickListener.onResult(clickedItem);
        });
    }

    public void setItemClickListener(Callback<T> itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public List<T> getDataSet() {
        return listDiffer.getCurrentList();
    }

    @Override
    public int getItemCount() {
        return getDataSet().size();
    }

    public T getItemAt(int index) {
        return getDataSet().get(index);
    }

    @MainThread
    public void updateDataSet(List<T> newList) {
        listDiffer.submitList(newList);
    }

    @MainThread
    public void updateDataSet(Collection<T> newList) {
        this.updateDataSet(new ArrayList<>(newList));
    }
}
