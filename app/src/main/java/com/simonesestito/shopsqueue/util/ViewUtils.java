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

package com.simonesestito.shopsqueue.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.simonesestito.shopsqueue.util.functional.Callback;

public class ViewUtils {
    /**
     * Sync a TabLayout with a ViewPager updating the tabs title
     *
     * @param tabs Titles of the tabs
     */
    public static void setupTabsWithViewPager(TabLayout tabLayout, ViewPager2 viewPager, @StringRes int[] tabs) {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabLayout.getContext().getString(tabs[position]))
        ).attach();
    }

    /**
     * Add a divider between items of a RecyclerView.
     * It must use {@link androidx.recyclerview.widget.LinearLayoutManager}
     */
    public static void addDivider(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int orientation = linearLayoutManager.getOrientation();

        DividerItemDecoration decoration = new DividerItemDecoration(context, orientation);
        recyclerView.addItemDecoration(decoration);
    }

    /**
     * Add a listener on the RecyclerView
     * It will execute the callback when it's necessary to load more data.
     */
    public static void onRecyclerViewLoadMore(RecyclerView recyclerView, Runnable callback) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager == null)
                    return;

                if (!(layoutManager instanceof LinearLayoutManager))
                    return;

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    callback.run();
                }
            }
        });
    }

    /**
     * Add a click listener to a spinner (dropdown menu)
     */
    public static void setSpinnerListener(Spinner spinner, Callback<Integer> callback) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callback.onResult(spinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                callback.onResult(0);
            }
        });
    }

    public static void hideKeyboard(EditText editText) {
        InputMethodManager imm = ContextCompat.getSystemService(
                editText.getContext(),
                InputMethodManager.class
        );
        if (imm != null)
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void setBackgroundTint(View view, @ColorInt int color) {
        view.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
