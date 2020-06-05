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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.simonesestito.shopsqueue.api.dto.Booking;
import com.simonesestito.shopsqueue.api.dto.BookingWithCount;
import com.simonesestito.shopsqueue.api.dto.FcmToken;
import com.simonesestito.shopsqueue.api.dto.ShoppingList;
import com.simonesestito.shopsqueue.api.service.FcmService;
import com.simonesestito.shopsqueue.ui.MainActivity;
import com.squareup.moshi.Moshi;

import java.util.Map;

import javax.inject.Inject;

public class FcmReceiverService extends FirebaseMessagingService {
    private static final String TAG = "Shops Queue FCM";
    private static final String KEY_MESSAGE_TYPE = "type";
    private static final String KEY_MESSAGE_DATA = "data";
    private static final String MESSAGE_TYPE_QUEUE_NOTICE = "queue-notice";
    private static final String MESSAGE_TYPE_BOOKING_CANCELLED = "booking-cancelled";
    private static final String MESSAGE_TYPE_ORDER_READY = "order-ready";
    private static final String MESSAGE_TYPE_ORDER_CANCELLED = "order-cancelled";
    private static final String NOTIFICATION_CHANNEL_BOOKINGS_NOTICE_ID = "bookings-notice";
    private static final String NOTIFICATION_CHANNEL_ORDERS_NOTICE_ID = "orders-notice";
    @Inject FcmService fcmService;
    @Inject SharedPreferencesStore sharedPreferencesStore;

    @Override
    public void onCreate() {
        super.onCreate();
        ShopsQueueApplication.getInjector().inject(this);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        fcmService.addFcmToken(new FcmToken(s)).onError(Throwable::printStackTrace);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (sharedPreferencesStore.getAccessToken() == null) {
            // Discard all messages when not authenticated
            // The app requires the user to be logged it to be used
            // This may have been an error.
            Log.e(TAG, "Message received while logged out");
            return;
        }

        Map<String, String> message = remoteMessage.getData();
        String messageType = message.get(KEY_MESSAGE_TYPE);
        String jsonData = message.get(KEY_MESSAGE_DATA);

        Log.e(TAG, "Type: " + messageType);
        Log.e(TAG, "Data: " + jsonData);

        NotificationManager notificationManager = ContextCompat
                .getSystemService(this, NotificationManager.class);
        if (notificationManager == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel bookingsChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_BOOKINGS_NOTICE_ID,
                    getString(R.string.notification_channel_booking_notice),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(bookingsChannel);

            NotificationChannel ordersChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ORDERS_NOTICE_ID,
                    getString(R.string.notification_channel_orders_notice),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(ordersChannel);
        }

        switch (messageType != null ? messageType : "null") {
            case MESSAGE_TYPE_QUEUE_NOTICE:
                handleQueueNotice(jsonData);
                break;
            case MESSAGE_TYPE_BOOKING_CANCELLED:
                handleBookingCancellation(jsonData);
                break;
            case MESSAGE_TYPE_ORDER_READY:
                handleOrderReady(jsonData);
                break;
            case MESSAGE_TYPE_ORDER_CANCELLED:
                handleOrderCancellation(jsonData);
                break;
            default:
                Log.e(TAG, "Received payload with unknown type: " + messageType);
        }
    }

    private void handleQueueNotice(String jsonString) {
        BookingWithCount data = parseJson(jsonString, BookingWithCount.class);
        if (data == null) {
            Log.e(TAG, "Received payload with invalid Booking data");
            return;
        }

        String message;
        switch (data.getQueueCount()) {
            case 0:
                message = getString(R.string.notification_your_turn_message);
                break;
            case 1:
                message = getString(R.string.notification_next_turn_message);
                break;
            default:
                message = getString(R.string.notification_queue_notice_message, data.getQueueCount());
        }

        showNotification(data.getShop().getId(), data.getShop().getName(), message);
    }

    private void handleBookingCancellation(String jsonString) {
        Booking data = parseJson(jsonString, Booking.class);
        if (data == null) {
            Log.e(TAG, "Received payload with invalid Booking data");
            return;
        }

        showNotification(
                data.getShop().getId(),
                data.getShop().getName(),
                getString(R.string.notification_booking_cancellation_message)
        );
    }

    private void handleOrderReady(String json) {
        ShoppingList data = parseJson(json, ShoppingList.class);
        if (data == null) {
            Log.e(TAG, "Received payload with invalid ShoppingList data");
            return;
        }

        showNotification(
                data.getId() + 100_000,
                data.getShop().getName(),
                getString(R.string.notification_order_ready_message)
        );
    }

    private void handleOrderCancellation(String json) {
        ShoppingList data = parseJson(json, ShoppingList.class);
        if (data == null) {
            Log.e(TAG, "Received payload with invalid ShoppingList data");
            return;
        }

        showNotification(
                data.getId() + 100_000,
                data.getShop().getName(),
                getString(R.string.notification_order_cancelled_message)
        );
    }

    @Nullable
    private <T> T parseJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty())
            return null;

        try {
            return new Moshi.Builder()
                    .build()
                    .adapter(clazz)
                    .fromJson(json);
        } catch (Exception e) {
            Log.e("FcmReceiverService", "Unable to read FCM JSON", e);
            return null;
        }
    }

    private void showNotification(int id, String title, String text) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                id,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_BOOKINGS_NOTICE_ID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setColor(getResources().getColor(R.color.green_500))
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        NotificationManager notificationManager = ContextCompat.getSystemService(this, NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }
}
