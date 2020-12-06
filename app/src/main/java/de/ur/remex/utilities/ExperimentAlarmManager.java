package de.ur.remex.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import de.ur.remex.Config;

import static android.content.Context.ALARM_SERVICE;

public class ExperimentAlarmManager {

    private Context context;
    private AlarmManager alarmManager;
    private static final int NOTIFICATION_TIMEOUT_ID = -1;


    public ExperimentAlarmManager(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    }

    public void setRelativeSurveyAlarm(int surveyId, long referenceTime, long surveyRelativeStartTimeInMillis) {
        Log.e("ExperimentAlarmManager", "Relative alarm setted");
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_NOTIFY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, surveyId, intent, PendingIntent.FLAG_ONE_SHOT);
        long alarmTimeInMillis = referenceTime + surveyRelativeStartTimeInMillis;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void setAbsoluteSurveyAlarm(int surveyId, long experimentStartTimeInMillis,
                                       int hour, int minute, int daysOffset) {
        Calendar c = Calendar.getInstance();
        long currentTimeInMillis = c.getTimeInMillis();
        c.setTimeInMillis(experimentStartTimeInMillis);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + daysOffset);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_NOTIFY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, surveyId, intent, PendingIntent.FLAG_ONE_SHOT);
        long alarmTimeInMillis = c.getTimeInMillis();
        if (alarmTimeInMillis >= currentTimeInMillis) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
        }
        else {
            Log.e("ExperimentAlarmManager", "Absolute alarm time already passed. Alarm wasn't set.");
        }
    }

    public void cancelSurveyAlarm(int surveyId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, surveyId, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
    }

    public void setSurveyTimeoutAlarm(int surveyId, int maxDurationInMin) {
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_TIMEOUT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, surveyId, intent, PendingIntent.FLAG_ONE_SHOT);
        long alarmTimeInMillis = maxDurationInMin * 60 * 1000 + c.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void setNotificationTimeoutAlarm(int notificationDurationInMin) {
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_NOTIFICATION_TIMEOUT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_TIMEOUT_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        long alarmTimeInMillis = notificationDurationInMin * 60 * 1000 + c.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void cancelNotificationTimeoutAlarm() {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_TIMEOUT_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
    }
}
