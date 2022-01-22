package com.example.smart_absensi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiverOut extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            Intent i = new Intent(context,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
            String result = intent.getStringExtra("absenku");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"smartku")
                    .setSmallIcon(R.drawable.torajaku)
                    .setContentTitle("SMART ABSENSI")
                    .setContentText("Waktunya Cek Out")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123,builder.build());


    }
}
