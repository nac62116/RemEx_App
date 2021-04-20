package de.ur.remex.utilities;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Observer;

import de.ur.remex.Config;

public class AppDestroyCallbackService extends Service {

    private static final Observable OBSERVABLE = new Observable();

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Event event = new Event(null, Config.EVENT_APP_KILLED, null);
        OBSERVABLE.notifyExperimentController(event);
        stopSelf();
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }
}
