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

package com.simonesestito.shopsqueue.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.SessionsFragmentBinding;
import com.simonesestito.shopsqueue.ui.dialog.ConfirmDialog;
import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.ui.recyclerview.SessionsAdapter;
import com.simonesestito.shopsqueue.util.ViewUtils;
import com.simonesestito.shopsqueue.viewmodel.SessionsViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class SessionsFragment extends AbstractAppFragment<SessionsFragmentBinding> {
    private static final int REQUEST_REVOKE_SESSION = 3;
    private static final String KEY_SESSION_ID = "sessionId";
    @Inject ViewModelFactory viewModelFactory;
    private SessionsAdapter adapter = new SessionsAdapter();
    private SessionsViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(SessionsViewModel.class);
    }

    @NonNull
    @Override
    protected SessionsFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return SessionsFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().userSessionsList.setAdapter(adapter);
        ViewUtils.addDivider(getViewBinding().userSessionsList);
        adapter.setItemClickListener(clickedSession -> {
            if (clickedSession.isCurrentSession())
                return;

            Bundle args = new Bundle();
            args.putInt(KEY_SESSION_ID, clickedSession.getId());

            ConfirmDialog.showForResult(
                    this,
                    REQUEST_REVOKE_SESSION,
                    getString(R.string.revoke_session_message),
                    args);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.getSessions().observe(getViewLifecycleOwner(), event -> {
            getViewBinding().userSessionsLoading.setVisibility(
                    event.isLoading() ? View.VISIBLE : View.GONE
            );

            if (event.isFailed() && event.hasToBeHandled()) {
                event.handle();
                ErrorDialog.newInstance(requireContext(), event.getError())
                        .show(getChildFragmentManager(), null);
            } else if (event.isSuccessful()) {
                adapter.updateDataSet(event.getData());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REVOKE_SESSION
                && resultCode == Activity.RESULT_OK
                && data != null) {
            // Revoke session
            int sessionId = data.getIntExtra(KEY_SESSION_ID, 0);
            if (sessionId > 0)
                viewModel.revokeSession(sessionId);
        }
    }
}
