package de.ur.remex.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.view.SurveyEntranceActivity;

public class NotificationHandler {

    private final static String CHANNEL_ID = "RemExReminder";
    private final Context context;

    public NotificationHandler(Context context) {
        this.context = context;
    }

    public void sendNotification() {
        // Ring Alarm
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.notification_clock);
        mediaPlayer.start();
        // Build pending intent
        Intent destinationIntent = new Intent(context, SurveyEntranceActivity.class);
        destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, destinationIntent, 0);
        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.remex_logo_black)
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
            boolean channelAlreadyCreated = false;
            for (NotificationChannel notificationChannel : notificationManager.getNotificationChannels()) {
                if (notificationChannel.getId().equals(CHANNEL_ID)) {
                    channelAlreadyCreated = true;
                }
            }
            if (!channelAlreadyCreated) {
                CharSequence name = Config.NOTIFICATION_CHANNEL_NAME;
                String description = Config.NOTIFICATION_CHANNEL_DESCRIPTION;
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000, 1000});

                notificationManager.createNotificationChannel(channel);
            }
        }
        notificationManager.notify(0, builder.build());
    }

    public void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
