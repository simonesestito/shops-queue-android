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
import androidx.recyclerview.widget.RecyclerView;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.api.dto.Session;
import com.simonesestito.shopsqueue.databinding.SessionItemBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SessionsAdapter extends DiffUtilAdapter<Session, SessionsAdapter.ViewHolder> {
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SessionItemBinding binding = SessionItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Context context = holder.itemView.getContext();
        Session session = getItemAt(position);
        String lastUsage = dateFormat.format(session.getLastUsageDate());
        String loginDate = dateFormat.format(session.getLoginDate());
        holder.view.sessionId.setText(context.getString(R.string.session_id_title, session.getId()));
        holder.view.sessionLastUsageDate.setText(context.getString(R.string.session_last_usage, lastUsage));
        holder.view.sessionLoginDate.setText(context.getString(R.string.session_login_date, loginDate));
        holder.view.sessionIsCurrent.setVisibility(
                session.isCurrentSession() ? View.VISIBLE : View.GONE
        );
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private SessionItemBinding view;

        ViewHolder(SessionItemBinding sessionItemBinding) {
            super(sessionItemBinding.getRoot());
            this.view = sessionItemBinding;
        }
    }
}
