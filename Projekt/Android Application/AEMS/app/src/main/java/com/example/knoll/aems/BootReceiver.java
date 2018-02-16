package com.example.knoll.aems;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by knoll on 25.11.2017.
 */

public class BootReceiver extends BroadcastReceiver {

    NotificationCompat.Builder builder = null;
    private int notificationID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean warning = true;
        boolean notification = true;

        builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.logo_icon);

        if(warning){
            builder.setContentTitle("Warnung");
            builder.setContentText("Kontrollieren Sie Ihre Verbrauchswerte!");

            notificationID ++;
            loadNotification();
        }

        if (notification)
        {
            builder.setContentTitle("Benachrichtigung");
            builder.setContentText("Es gibt Abweichungen Ihrer Verbrauchswerte");

            notificationID ++;
            loadNotification();

        }
    }

    private void loadNotification() {

        Intent in = new Intent(builder.mContext, AllNotifications.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(builder.mContext);
        stackBuilder.addParentStack(AllNotifications.class);
        stackBuilder.addNextIntent(in);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notificationID, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) builder.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationID, builder.build());

    }
}
