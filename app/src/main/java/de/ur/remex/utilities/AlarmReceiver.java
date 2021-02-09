package de.ur.remex.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.admin.LoginActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final Observable OBSERVABLE = new Observable();

    @Override
    public void onReceive(Context context, Intent intent) {
        String purpose = intent.getStringExtra(Config.ALARM_PURPOSE_KEY);
        if (purpose != null) {
            // Creating notification for a survey
            Event event = null;
            switch (purpose) {
                case Config.PURPOSE_SURVEY_ALARM:
                    event = new Event(null, Config.EVENT_SURVEY_ALARM, null);
                    break;
                case Config.PURPOSE_SURVEY_TIMEOUT:
                    event = new Event(null, Config.EVENT_SURVEY_TIMEOUT, null);
                    break;
                case Config.PURPOSE_NOTIFICATION_TIMEOUT:
                    event = new Event(null, Config.EVENT_NOTIFICATION_TIMEOUT, null);
                    break;
                case Config.PURPOSE_STEP_TIMER:
                    int stepId = intent.getIntExtra(Config.STEP_ID_KEY, 0);
                    event = new Event(null, Config.EVENT_STEP_TIMER, Integer.toString(stepId));
                    break;
                case Config.PURPOSE_ADMIN_TIMEOUT:
                    Log.e("AlarmReceiver", "EVENT_ADMIN_TIMEOUT");
                    Intent destinationIntent = new Intent(context, LoginActivity.class);
                    destinationIntent.putExtra(Config.EXIT_APP_KEY, true);
                    destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(destinationIntent);
                    break;
                default:
                    break;
            }
            if (event != null) {
                OBSERVABLE.notifyExperimentController(event);
            }
        }
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }
}
