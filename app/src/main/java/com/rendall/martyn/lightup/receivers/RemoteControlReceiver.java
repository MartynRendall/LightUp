package com.rendall.martyn.lightup.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.rendall.martyn.lightup.R;
import com.rendall.martyn.lightup.services.ExtensionsAllOffService;
import com.rendall.martyn.lightup.services.ExtensionsAllOnService;

import static android.content.Context.MODE_PRIVATE;
import static com.rendall.martyn.lightup.Constants.NOTIFICATIONS.REMOTE_CONTROL_NOTIFICATION_ID;
import static com.rendall.martyn.lightup.Constants.SHARED_PREFS.AT_HOME_FLAG;
import static com.rendall.martyn.lightup.Constants.SHARED_PREFS.NAME;

/**
 * Created by Martyn on 10/04/2016.
 */
public class RemoteControlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //TODO make this an option in preferences to switch on lights automatically at sunset, perhaps allow a tolerance? i.e. 10 mins after sunset
        if (atHome(context)) {
            displayRemoteControl(context);
        }
    }

    private boolean atHome(Context context) {
        return context.getSharedPreferences(NAME, MODE_PRIVATE).getBoolean(AT_HOME_FLAG, false);
    }

    private void displayRemoteControl(Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentText("Control lights")
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContent(getComplexNotificationView(context));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        Notification notification = builder.build();
        notificationManager.notify(REMOTE_CONTROL_NOTIFICATION_ID, notification);
    }

    private RemoteViews getComplexNotificationView(Context context) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(
                context.getPackageName(),
                R.layout.remote_control
        );

        // Locate and set the Image into customnotificationtext.xml ImageViews
        notificationView.setImageViewResource(R.id.imagenotileft, R.mipmap.ic_launcher);

        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.title, "Control home");
        notificationView.setTextViewText(R.id.text, "");

        notificationView.setOnClickPendingIntent(R.id.button_off, PendingIntent.getService(context, 25, new Intent(context, ExtensionsAllOffService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationView.setOnClickPendingIntent(R.id.button_on, PendingIntent.getService(context, 28, new Intent(context, ExtensionsAllOnService.class), PendingIntent.FLAG_UPDATE_CURRENT));

        return notificationView;
    }
}
