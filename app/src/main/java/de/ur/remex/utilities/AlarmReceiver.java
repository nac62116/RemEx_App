package de.ur.remex.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.view.SurveyEntranceActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private final static String CHANNEL_ID = "expReminder";
    private static Observable observable = new Observable();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmReceiver", "Alarm received");
        String purpose = intent.getStringExtra(Config.ALARM_PURPOSE_KEY);
        if (purpose != null) {
            // Creating notification for a survey
            if (purpose.equals(Config.PURPOSE_SURVEY_NOTIFY)) {
                createNotification(context, intent);
                Event event = new Event(null, Config.EVENT_NOTIFICATION_CREATED, null);
                observable.notifyExperimentController(event);
            }
            else if (purpose.equals(Config.PURPOSE_SURVEY_TIMEOUT)) {
                Event event = new Event(null, Config.EVENT_SURVEY_TIMEOUT, null);
                observable.notifyExperimentController(event);
            }
            else if (purpose.equals(Config.PURPOSE_NOTIFICATION_TIMEOUT)) {
                Event event = new Event(null, Config.EVENT_NOTIFICATION_TIMEOUT, null);
                observable.notifyExperimentController(event);
            }
        }
    }

    private void createNotification(Context context, Intent intent) {

        // TODO: Make current experiment accessible via Admin Screen with Local Storage value

        // Unlocking screen to guarantee the notification sound
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "RemEx: Notification");
        wakeLock.acquire(1000);

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/notification_clock");

        Intent destinationIntent = new Intent(context, SurveyEntranceActivity.class);
        destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, destinationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.medbo_logo)
                .setContentTitle(Config.NOTIFICATION_HEADER)
                .setContentText(Config.NOTIFICATION_TEXT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(Config.NOTIFICATION_TEXT))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Config.NOTIFICATION_CHANNEL_NAME;
            String description = Config.NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            // Creating an Audio Attribute
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(sound, audioAttributes);

            notificationManager.createNotificationChannel(channel);
            // For test cases:
            /*
            for (NotificationChannel notificationChannel : notificationManager.getNotificationChannels()) {
                notificationManager.deleteNotificationChannel(notificationChannel.getId());
            }*/
            //Log.e("notificationChannel", notificationManager.getNotificationChannel(CHANNEL_ID).toString());
        }

        notificationManager.notify(0, builder.build());
    }

    public void addObserver(Observer observer) {
        observable.addObserver(observer);
    }
}
