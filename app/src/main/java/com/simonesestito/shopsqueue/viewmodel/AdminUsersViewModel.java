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

package com.simonesestito.shopsqueue.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.simonesestito.shopsqueue.api.dto.Page;
import com.simonesestito.shopsqueue.api.dto.User;
import com.simonesestito.shopsqueue.api.service.UserService;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;
import com.simonesestito.shopsqueue.util.livedata.Resource;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;

public class AdminUsersViewModel extends ViewModel {
    private final UserService userService;
    private final LiveResource<Set<User>> users = new LiveResource<>();
    private Page<User> lastPage;
    private Set<User> lastUsers = new LinkedHashSet<>();

    @Inject
    AdminUsersViewModel(UserService userService) {
        this.userService = userService;
        refreshUsers();
    }

    public void refreshUsers() {
        users.emitLoading();
        int nextPage = lastPage == null ? 0 : lastPage.getPage() + 1;
        userService.listUsers(nextPage, "")
                .onResult(newPage -> {
                    lastPage = newPage;
                    Log.wtf("Adapter", newPage.getData().size() + "");
                    Log.wtf("Adapter", "" + newPage.getTotalItems());
                    lastUsers.addAll(newPage.getData());
                    users.emitResult(lastUsers);
                })
                .onError(err -> {
                    err.printStackTrace();
                    users.emitError(err);
                });
    }

    public LiveData<Resource<Set<User>>> getUsers() {
        return users;
    }
}
