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

package com.simonesestito.shopsqueue.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.simonesestito.shopsqueue.di.annotation.ViewModelKey;
import com.simonesestito.shopsqueue.viewmodel.AdminShopEditViewModel;
import com.simonesestito.shopsqueue.viewmodel.AdminShopsViewModel;
import com.simonesestito.shopsqueue.viewmodel.AdminUserEditViewModel;
import com.simonesestito.shopsqueue.viewmodel.AdminUsersViewModel;
import com.simonesestito.shopsqueue.viewmodel.LocationPickerViewModel;
import com.simonesestito.shopsqueue.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.viewmodel.OwnerProductsViewModel;
import com.simonesestito.shopsqueue.viewmodel.OwnerViewModel;
import com.simonesestito.shopsqueue.viewmodel.SessionsViewModel;
import com.simonesestito.shopsqueue.viewmodel.ShopPickerViewModel;
import com.simonesestito.shopsqueue.viewmodel.UserFavouriteShopsViewModel;
import com.simonesestito.shopsqueue.viewmodel.UserMainViewModel;
import com.simonesestito.shopsqueue.viewmodel.UserProfileViewModel;
import com.simonesestito.shopsqueue.viewmodel.ViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public interface ViewModelModule {
    @Binds
    ViewModelProvider.Factory bindFactory(ViewModelFactory viewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OwnerViewModel.class)
    ViewModel bindOwnerViewModel(OwnerViewModel ownerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AdminUsersViewModel.class)
    ViewModel bindAdminUserViewModel(AdminUsersViewModel adminUsersViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AdminUserEditViewModel.class)
    ViewModel bindAdminUsersEditViewModel(AdminUserEditViewModel adminUserEditViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ShopPickerViewModel.class)
    ViewModel bindShopsPickerViewModel(ShopPickerViewModel shopPickerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AdminShopsViewModel.class)
    ViewModel bindAdminShopsViewModel(AdminShopsViewModel adminShopsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AdminShopEditViewModel.class)
    ViewModel bindAdminShopEditViewModel(AdminShopEditViewModel adminShopEditViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LocationPickerViewModel.class)
    ViewModel bindLocationPickerViewModel(LocationPickerViewModel locationPickerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserMainViewModel.class)
    ViewModel bindUserMainViewModel(UserMainViewModel userMainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserFavouriteShopsViewModel.class)
    ViewModel bindUserFavouriteShopsViewModel(UserFavouriteShopsViewModel userFavouriteShopsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel.class)
    ViewModel bindUserProfileViewModel(UserProfileViewModel userProfileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SessionsViewModel.class)
    ViewModel bindSessionsViewModel(SessionsViewModel sessionsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OwnerProductsViewModel.class)
    ViewModel bindOwnerProductsViewModel(OwnerProductsViewModel ownerProductsViewModel);
}
