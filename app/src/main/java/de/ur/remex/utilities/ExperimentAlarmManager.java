package de.ur.remex.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import de.ur.remex.Config;

import static android.content.Context.ALARM_SERVICE;

public class ExperimentAlarmManager {

    private final Context context;


    private static final String SURVEY_ALARM_SUFFIX = "0";
    private static final String SURVEY_TIMEOUT_SUFFIX = "1";
    private static final int NOTIFICATION_TIMEOUT_ID = -1;
    private static final int ADMIN_TIMEOUT_ID = -2;
    private static final ArrayList<PendingIntent> pendingIntents = new ArrayList<>();


    public ExperimentAlarmManager(Context context) {
        this.context = context;
    }

    public void setRelativeSurveyAlarm(int surveyId, long referenceTime, long surveyRelativeStartTimeInMillis) {
        Log.e("AlarmManager", "EVENT_SURVEY_ALARM_SET");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_ALARM);
        String pendingIntentId = surveyId + SURVEY_ALARM_SUFFIX;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(pendingIntentId), intent, PendingIntent.FLAG_ONE_SHOT);
        pendingIntents.add(pendingIntent);
        long alarmTimeInMillis = referenceTime + surveyRelativeStartTimeInMillis;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void setAbsoluteSurveyAlarm(int surveyId, long experimentStartTimeInMillis,
                                       int hour, int minute, int daysOffset) {
        Log.e("AlarmManager", "EVENT_SURVEY_ALARM_SET");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(experimentStartTimeInMillis);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + daysOffset);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_ALARM);
        String pendingIntentId = surveyId + SURVEY_ALARM_SUFFIX;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(pendingIntentId), intent, PendingIntent.FLAG_ONE_SHOT);
        pendingIntents.add(pendingIntent);
        long alarmTimeInMillis = c.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void setSurveyTimeoutAlarm(int surveyId, int maxDurationInMin) {
        Log.e("AlarmManager", "EVENT_SURVEY_TIMEOUT_ALARM_SET");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_TIMEOUT);
        String pendingIntentId = surveyId + SURVEY_TIMEOUT_SUFFIX;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(pendingIntentId), intent, PendingIntent.FLAG_ONE_SHOT);
        pendingIntents.add(pendingIntent);
        long alarmTimeInMillis = maxDurationInMin * 60 * 1000 + c.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void setNotificationTimeoutAlarm(int notificationDurationInMin) {
        Log.e("AlarmManager", "EVENT_NOTIFICATION_TIMEOUT_ALARM_SET");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_NOTIFICATION_TIMEOUT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_TIMEOUT_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        pendingIntents.add(pendingIntent);
        long alarmTimeInMillis = notificationDurationInMin * 60 * 1000 + c.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void cancelNotificationTimeoutAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_TIMEOUT_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
    }

    public void setStepTimer(int stepId, int stepDurationInMin) {
        Log.e("AlarmManager", "EVENT_STEP_TIMER_SET");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_STEP_TIMER);
        intent.putExtra(Config.STEP_ID_KEY, stepId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, stepId, intent, PendingIntent.FLAG_ONE_SHOT);
        pendingIntents.add(pendingIntent);
        long alarmTimeInMillis = stepDurationInMin * 60 * 1000 + c.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void setAdminTimeoutAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_ADMIN_TIMEOUT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ADMIN_TIMEOUT_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        int adminTimeoutInMs = Config.ADMIN_TIMEOUT_MIN * 60 * 1000;
        long alarmTimeInMillis = c.getTimeInMillis() + adminTimeoutInMs;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void cancelAdminTimeoutAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent adminTimeoutIntent = new Intent(context, AlarmReceiver.class);
        adminTimeoutIntent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_ADMIN_TIMEOUT);
        PendingIntent adminTimeoutPendingIntent = PendingIntent.getBroadcast(context, ADMIN_TIMEOUT_ID, adminTimeoutIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(adminTimeoutPendingIntent);
    }

    public void cancelAllAlarms() {
        Log.e("AlarmManager", "EVENT_ALARMS_CANCELLED");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        for (PendingIntent pendingIntent: pendingIntents) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntents.clear();
    }
}
