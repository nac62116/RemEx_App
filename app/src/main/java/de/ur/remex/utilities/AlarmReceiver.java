package de.ur.remex.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Observer;

import de.ur.remex.Config;

public class AlarmReceiver extends BroadcastReceiver {

    private static Observable observable = new Observable();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmReceiver", "Alarm received");
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
                default:
                    break;
            }
            if (event != null) {
                observable.notifyExperimentController(event);
            }
        }
    }

    public void addObserver(Observer observer) {
        observable.addObserver(observer);
    }
}
