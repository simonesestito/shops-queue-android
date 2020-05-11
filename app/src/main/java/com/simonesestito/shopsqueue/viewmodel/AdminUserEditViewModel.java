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

import com.simonesestito.shopsqueue.api.dto.NewUser;
import com.simonesestito.shopsqueue.api.dto.Shop;
import com.simonesestito.shopsqueue.api.dto.UserDetails;
import com.simonesestito.shopsqueue.api.service.UserService;
import com.simonesestito.shopsqueue.util.livedata.LiveResource;

import javax.inject.Inject;

public class AdminUserEditViewModel extends ViewModel {
    private final LiveResource<UserDetails> liveUser = new LiveResource<>();
    private final UserService userService;
    public Shop pickedShop;

    @Inject
    AdminUserEditViewModel(UserService userService) {
        this.userService = userService;
    }

    public void loadUser(int liveUserId) {
        if (liveUser.getValue() != null
                && liveUser.getValue().getData() != null
                && liveUser.getValue().getData().getId() == liveUserId)
            return; // Already loaded

        if (liveUserId == 0) {
            liveUser.emitResult(null);
            return;
        }

        liveUser.emitLoading();
        userService.getUserById(liveUserId)
                .onResult(liveUser::emitResult)
                .onError(liveUser::emitError);
    }

    public void updateUser(int liveUserId, NewUser userUpdate) {
        liveUser.emitLoading();
        userService.updateUser(liveUserId, userUpdate)
                .onResult(user -> liveUser.emitResult(null))
                .onError(liveUser::emitError);
    }

    public void saveNewUser(NewUser newUser) {
        liveUser.emitLoading();
        userService.addNewUser(newUser)
                .onResult(user -> liveUser.emitResult(null))
                .onError(liveUser::emitError);
    }

    public LiveResource<UserDetails> getLiveUser() {
        return liveUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        liveUser.clearValue();
    }
}
