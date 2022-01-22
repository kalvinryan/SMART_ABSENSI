package com.example.smart_absensi;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            Intent i = new Intent(context,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
            String result = intent.getStringExtra("absenku");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"smart")
                    .setSmallIcon(R.drawable.torajaku)
                    .setContentTitle("SMART ABSENSI")
                    .setContentText("Saatnya Check In")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123,builder.build());


    }
}
