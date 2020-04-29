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

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.simonesestito.shopsqueue.R;

public class ErrorDialog extends DialogFragment {
    private static final String EXTRA_MESSAGE = "error_message";

    public static ErrorDialog newInstance(String message) {
        ErrorDialog errorDialog = new ErrorDialog();
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        errorDialog.setArguments(args);
        return errorDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String message = requireArguments().getString(EXTRA_MESSAGE);

        // TODO Improve dialog design
        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok_button, null)
                .create();
    }
}
