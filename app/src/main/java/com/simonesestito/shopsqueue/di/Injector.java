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

package com.simonesestito.shopsqueue.di;

import android.content.Context;

import com.simonesestito.shopsqueue.FcmReceiverService;
import com.simonesestito.shopsqueue.di.module.RetrofitModule;
import com.simonesestito.shopsqueue.di.module.SharedPreferencesModule;
import com.simonesestito.shopsqueue.di.module.ViewModelModule;
import com.simonesestito.shopsqueue.ui.MainActivity;
import com.simonesestito.shopsqueue.ui.fragment.AdminShopEditFragment;
import com.simonesestito.shopsqueue.ui.fragment.AdminShopsFragment;
import com.simonesestito.shopsqueue.ui.fragment.AdminUserEditFragment;
import com.simonesestito.shopsqueue.ui.fragment.AdminUsersFragment;
import com.simonesestito.shopsqueue.ui.fragment.LocationPickerFragment;
import com.simonesestito.shopsqueue.ui.fragment.LoginFragment;
import com.simonesestito.shopsqueue.ui.fragment.MainFragment;
import com.simonesestito.shopsqueue.ui.fragment.OwnerFragment;
import com.simonesestito.shopsqueue.ui.fragment.ShopPickerFragment;
import com.simonesestito.shopsqueue.ui.fragment.SignUpFragment;
import com.simonesestito.shopsqueue.ui.fragment.UserFavouriteShopsFragment;
import com.simonesestito.shopsqueue.ui.fragment.UserMainFragment;
import com.simonesestito.shopsqueue.ui.fragment.UserProfileFragment;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {
        RetrofitModule.class,
        SharedPreferencesModule.class,
        ViewModelModule.class
})
public interface Injector {
    void inject(MainActivity mainActivity);

    void inject(MainFragment mainFragment);

    void inject(LoginFragment loginFragment);

    void inject(SignUpFragment signUpFragment);

    void inject(OwnerFragment ownerFragment);

    void inject(AdminUsersFragment adminUsersFragment);

    void inject(AdminUserEditFragment adminUserEditFragment);

    void inject(ShopPickerFragment shopPickerFragment);

    void inject(AdminShopsFragment adminShopsFragment);

    void inject(AdminShopEditFragment adminShopEditFragment);

    void inject(LocationPickerFragment locationPickerFragment);

    void inject(UserMainFragment userMainFragment);

    void inject(UserFavouriteShopsFragment userFavouriteShopsFragment);

    void inject(FcmReceiverService fcmReceiverService);

    void inject(UserProfileFragment userProfileFragment);

    @Component.Builder
    interface Builder {
        Injector build();

        @BindsInstance
        Builder provideContext(Context context);
    }
}
