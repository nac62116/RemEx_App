package de.ur.remex.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.admin.LoginActivity;

/*
MIT License

Copyright (c) 2021 Colin Nash

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

public class AlarmReceiver extends BroadcastReceiver {

    private static final Observable OBSERVABLE = new Observable();

    @Override
    public void onReceive(Context context, Intent intent) {
        String purpose = intent.getStringExtra(Config.ALARM_PURPOSE_KEY);
        if (purpose != null) {
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
                    event = new Event(null, Config.EVENT_STEP_TIMER, stepId);
                    break;
                case Config.PURPOSE_ADMIN_TIMEOUT:
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
