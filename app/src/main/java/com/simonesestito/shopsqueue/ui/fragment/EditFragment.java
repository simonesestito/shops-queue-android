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

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.simonesestito.shopsqueue.ui.dialog.ErrorDialog;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

public abstract class EditFragment<T, V extends ViewBinding> extends AbstractAppFragment<V> {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSaveButton().setOnClickListener(v -> onSaveForm());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArgumentId() == 0)
            showData(null);
        else
            onLoadData();

        getLiveData().observe(getViewLifecycleOwner(), event -> {
            if (event.isLoading()) {
                getLoadingView().setVisibility(View.VISIBLE);
                setFormVisibility(View.GONE);
                getSaveButton().setEnabled(false);
            } else {
                getLoadingView().setVisibility(View.GONE);
                setFormVisibility(View.VISIBLE);
                getSaveButton().setEnabled(true);
            }

            if (event.isFailed() && event.hasToBeHandled()) {
                event.handle();
                handleError(event.getError());
            }

            if (event.isSuccessful()) {
                if (event.getData() == null) {
                    // Successful update request
                    requireActivity().onBackPressed();
                } else if (event.hasToBeHandled()) {
                    // Data fetched
                    event.handle();
                    showData(event.getData());
                }
            }
        });
    }

    protected int getArgumentId() {
        return 0;
    }

    protected View getSaveButton() {
        return null;
    }

    protected View getLoadingView() {
        return null;
    }

    protected void setFormVisibility(int visibility) {
    }

    protected void handleError(Throwable error) {
        ErrorDialog.newInstance(requireContext(), error)
                .show(getChildFragmentManager(), null);
    }

    protected void onSaveForm() {
    }

    protected void showData(@Nullable T data) {
        getLoadingView().setVisibility(View.GONE);
    }

    protected void onLoadData() {
    }

    protected LiveResource<T> getLiveData() {
        return null;
    }
}
