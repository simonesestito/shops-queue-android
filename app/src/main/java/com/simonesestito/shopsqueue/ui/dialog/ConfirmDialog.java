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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.simonesestito.shopsqueue.R;

public class ConfirmDialog extends DialogFragment {
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_RESULT_DATA = "result_data";

    public static void showForResult(Fragment fragment, int requestCode, String message) {
        showForResult(fragment, requestCode, message, null);
    }

    public static void showForResult(Fragment fragment, int requestCode, String message, @Nullable Bundle resultData) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTargetFragment(fragment, requestCode);

        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        args.putBundle(EXTRA_RESULT_DATA, resultData);
        dialog.setArguments(args);

        dialog.show(fragment.getParentFragmentManager(), null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(requireArguments().getString(EXTRA_MESSAGE))
                .setPositiveButton(R.string.confirm_yes_button, (d, b) -> onResult(true))
                .setNegativeButton(R.string.confirm_no_button, (d, b) -> onResult(false))
                .setOnCancelListener(d -> onResult(false))
                .create();
    }

    private void onResult(boolean confirmation) {
        int result = confirmation ? Activity.RESULT_OK : Activity.RESULT_CANCELED;
        Fragment target = getTargetFragment();

        Bundle data = requireArguments().getBundle(EXTRA_RESULT_DATA);
        Intent resultIntent = null;
        if (data != null) {
            resultIntent = new Intent();
            resultIntent.putExtras(data);
        }

        if (target != null)
            target.onActivityResult(getTargetRequestCode(), result, resultIntent);
    }
}
