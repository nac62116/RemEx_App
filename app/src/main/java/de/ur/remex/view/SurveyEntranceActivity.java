package de.ur.remex.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;
import de.ur.remex.Config;

public class SurveyEntranceActivity extends AppCompatActivity {

    private static final Observable OBSERVABLE = new Observable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        Event event = new Event(this, Config.EVENT_SURVEY_STARTED, null);
        OBSERVABLE.notifyExperimentController(event);
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }
}
