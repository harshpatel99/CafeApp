package com.hash.cafeapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.hash.cafeapp.MainActivity;
import com.hash.cafeapp.R;

import java.util.Objects;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String messageBody = remoteMessage.getNotification().getBody();

        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLights(Color.RED, 3000, 3000)
                .setVibrate(new long[]{500})
                .setAutoCancel(true);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_layout_heads_up_notif);

        remoteViews.setTextViewText(R.id.cust_noti_title, messageTitle);
        remoteViews.setTextViewText(R.id.cust_noti_subtitle, messageBody);
        //builder.setCustomHeadsUpContentView(remoteViews);

        int notificationID = (int) System.currentTimeMillis();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(notificationID, builder.build());

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);

            NotificationChannel channel =
                    new NotificationChannel(getString(R.string.default_notification_channel_id),
                            name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

}
