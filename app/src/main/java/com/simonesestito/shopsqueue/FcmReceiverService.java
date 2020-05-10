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

package com.simonesestito.shopsqueue;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.simonesestito.shopsqueue.api.dto.FcmToken;
import com.simonesestito.shopsqueue.api.service.FcmService;

import javax.inject.Inject;

public class FcmReceiverService extends FirebaseMessagingService {
    @Inject FcmService fcmService;

    @Override
    public void onCreate() {
        super.onCreate();
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        fcmService.addFcmToken(new FcmToken(s))
                .onError(Throwable::printStackTrace);
    }
}
