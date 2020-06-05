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

package com.simonesestito.shopsqueue.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.ShopsQueueApplication;
import com.simonesestito.shopsqueue.databinding.OwnerOrdersBottomSheetBinding;
import com.simonesestito.shopsqueue.viewmodel.OwnerOrdersViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class OwnerOrdersBottomSheet extends BottomSheetDialogFragment {
    @Inject ViewModelFactory viewModelFactory;
    private OwnerOrdersViewModel viewModel;
    private OwnerOrdersBottomSheetArgs args;
    private OwnerOrdersBottomSheetBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopsQueueApplication.getInjector().inject(this);
        args = OwnerOrdersBottomSheetArgs.fromBundle(requireArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OwnerOrdersBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(OwnerOrdersViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int listId = args.getShoppingList().getId();

        String title = getString(R.string.owner_orders_bottom_sheet_title, args.getShoppingList().getUserName());
        binding.ownerOrdersTitle.setText(title);

        if (args.getShoppingList().isReady()) {
            binding.ownerOrdersCancelButton.setVisibility(View.GONE);
            binding.ownerOrdersPrepareButton.setVisibility(View.GONE);
            binding.ownerOrdersRetiredButton.setVisibility(View.VISIBLE);
        } else {
            binding.ownerOrdersCancelButton.setVisibility(View.VISIBLE);
            binding.ownerOrdersPrepareButton.setVisibility(View.VISIBLE);
            binding.ownerOrdersRetiredButton.setVisibility(View.GONE);
        }

        binding.ownerOrdersPrepareButton.setOnClickListener(v -> {
            viewModel.prepareOrder(listId);
            if (getDialog() != null)
                getDialog().dismiss();
        });

        // Cancel and set order retired, both use the same corresponding API call.
        // The difference is made on the server, if the user needs to be informed or not.
        View.OnClickListener cancelListener = v -> {
            viewModel.deleteOrder(listId);
            if (getDialog() != null)
                getDialog().dismiss();
        };
        binding.ownerOrdersCancelButton.setOnClickListener(cancelListener);
        binding.ownerOrdersRetiredButton.setOnClickListener(cancelListener);
    }
}
