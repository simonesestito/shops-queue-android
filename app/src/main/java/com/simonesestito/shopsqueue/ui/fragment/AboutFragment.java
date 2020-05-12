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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.simonesestito.shopsqueue.Constants;
import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.databinding.AboutFragmentBinding;

public class AboutFragment extends AbstractAppFragment<AboutFragmentBinding> {
    @NonNull
    @Override
    protected AboutFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AboutFragmentBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewBinding().shareAppListItem.setOnClickListener(v -> shareApp());
        getViewBinding().downloadApkListItem.setOnClickListener(v -> downloadApp());
        getViewBinding().githubListItem.setOnClickListener(v -> openGithub());
        getViewBinding().openLicensesListItem.setOnClickListener(v -> showLicenses());
    }

    private void shareApp() {
        String shareMessage = getString(R.string.app_name) + "\n" + Constants.APK_DOWNLOAD_URL;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        shareIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.action_share_app));
        Intent chooser = Intent.createChooser(shareIntent, getString(R.string.action_share_app));
        startActivity(chooser);
    }

    private void downloadApp() {
        Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APK_DOWNLOAD_URL));
        startActivity(downloadIntent);
    }

    private void openGithub() {
        Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GITHUB_REPO_URL));
        startActivity(downloadIntent);
    }

    private void showLicenses() {
        // TODO
    }
}
