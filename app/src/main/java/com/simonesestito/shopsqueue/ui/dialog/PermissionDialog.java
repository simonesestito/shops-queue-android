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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.simonesestito.shopsqueue.R;

public class PermissionDialog extends DialogFragment {
    private static final String EXTRA_PERMISSIONS = "permissions";

    public static void showForResult(Fragment fragment, int requestCode, String... permissions) {
        PermissionDialog permissionDialog = new PermissionDialog();
        permissionDialog.setTargetFragment(fragment, requestCode);

        Bundle args = new Bundle();
        args.putStringArray(EXTRA_PERMISSIONS, permissions);
        permissionDialog.setArguments(args);

        permissionDialog.show(fragment.getParentFragmentManager(), null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.permission_dialog_title)
                .setMessage(R.string.permission_dialog_message)
                .setPositiveButton(R.string.permission_dialog_allow_button, (d, b) -> onAllow())
                .setNegativeButton(R.string.permission_dialog_deny_button, (d, b) -> onDeny())
                .create();
    }

    private void onAllow() {
        Fragment target = getTargetFragment();
        String[] permissions = requireArguments().getStringArray(EXTRA_PERMISSIONS);
        if (target != null && permissions != null) {
            target.requestPermissions(permissions, getTargetRequestCode());
        }
    }

    private void onDeny() {
        Fragment target = getTargetFragment();
        if (target != null) {
            target.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
        }
    }
}
