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

import com.simonesestito.shopsqueue.api.ApiCallAdapter;
import com.simonesestito.shopsqueue.api.AuthorizationInterceptor;
import com.simonesestito.shopsqueue.api.service.LoginService;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.simonesestito.shopsqueue.Constants.API_BASE_URL;

@Module
public class RetrofitModule {
    @Provides
    OkHttpClient provideOkHttp(AuthorizationInterceptor authorizationInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(authorizationInterceptor)
                .build();
    }

    @Provides
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(new ApiCallAdapter.Factory())
                .baseUrl(API_BASE_URL)
                .build();
    }

    @Provides
    LoginService provideLoginService(Retrofit retrofit) {
        return retrofit.create(LoginService.class);
    }
}
