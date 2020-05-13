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

import androidx.lifecycle.ViewModel;

import com.simonesestito.shopsqueue.api.dto.NewSimpleUser;
import com.simonesestito.shopsqueue.api.dto.UserDetails;
import com.simonesestito.shopsqueue.api.service.UserService;
import com.simonesestito.shopsqueue.model.AuthUserHolder;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import javax.inject.Inject;

public class UserProfileViewModel extends ViewModel {
    private final UserService userService;
    private LiveResource<UserDetails> currentUser = new LiveResource<>();

    @Inject
    UserProfileViewModel(UserService userService) {
        this.userService = userService;
        currentUser.emitResult(AuthUserHolder.getCurrentUser());
    }

    public void updateUser(NewSimpleUser update) {
        currentUser.emitLoading();
        userService.updateCurrentUser(update)
                .onResult(user -> {
                    AuthUserHolder.setCurrentUser(user);
                    currentUser.emitResult(null);
                })
                .onError(err -> currentUser.emitError(err));
    }

    public LiveResource<UserDetails> getCurrentUser() {
        return currentUser;
    }
}
