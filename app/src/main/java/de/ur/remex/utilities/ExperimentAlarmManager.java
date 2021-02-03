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

    private static final String SURVEY_ID_SUFFIX = "0";
    private static final String STEP_ID_SUFFIX = "1";
    private final Context context;
    private static final int NOTIFICATION_TIMEOUT_ID = -1;


    public ExperimentAlarmManager(Context context) {
        this.context = context;
    }

    public void setRelativeSurveyAlarm(int surveyId, long referenceTime, long surveyRelativeStartTimeInMillis) {
        Log.e("ExperimentAlarmManager", "Relative alarm setted");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_ALARM);
        String pendingIntentId = surveyId + SURVEY_ID_SUFFIX;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(pendingIntentId), intent, PendingIntent.FLAG_ONE_SHOT);
        long alarmTimeInMillis = referenceTime + surveyRelativeStartTimeInMillis;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void setAbsoluteSurveyAlarm(int surveyId, long experimentStartTimeInMillis,
                                       int hour, int minute, int daysOffset) {
        Log.e("ExperimentAlarmManager", "Absolute alarm setted");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        long currentTimeInMillis = c.getTimeInMillis();
        c.setTimeInMillis(experimentStartTimeInMillis);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + daysOffset);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_ALARM);
        String pendingIntentId = surveyId + SURVEY_ID_SUFFIX;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(pendingIntentId), intent, PendingIntent.FLAG_ONE_SHOT);
        long alarmTimeInMillis = c.getTimeInMillis();
        if (alarmTimeInMillis >= currentTimeInMillis) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
        }
        else {
            Log.e("ExperimentAlarmManager", "Absolute alarm time already passed. Alarm wasn't set.");
        }
    }

    public void cancelSurveyAlarm(int surveyId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        String pendingIntentId = surveyId + SURVEY_ID_SUFFIX;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(pendingIntentId), intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
    }

    public void setSurveyTimeoutAlarm(int surveyId, int maxDurationInMin) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_SURVEY_TIMEOUT);
        String pendingIntentId = surveyId + SURVEY_ID_SUFFIX;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(pendingIntentId), intent, PendingIntent.FLAG_ONE_SHOT);
        long alarmTimeInMillis = maxDurationInMin * 60 * 1000 + c.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    public void setNotificationTimeoutAlarm(int notificationDurationInMin) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_NOTIFICATION_TIMEOUT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_TIMEOUT_ID, intent, PendingIntent.FLAG_ONE_SHOT);
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
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Config.ALARM_PURPOSE_KEY, Config.PURPOSE_STEP_TIMER);
        intent.putExtra(Config.STEP_ID_KEY, stepId);
        String pendingIntentId = stepId + STEP_ID_SUFFIX;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(pendingIntentId), intent, PendingIntent.FLAG_ONE_SHOT);
        long alarmTimeInMillis = stepDurationInMin * 60 * 1000 + c.getTimeInMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

}
