package de.ur.remex.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.utilities.ActivityEvent;
import de.ur.remex.utilities.ActivityObservable;
import de.ur.remex.utilities.Config;

public class SurveyEntranceActivity extends AppCompatActivity {

    private static ActivityObservable observable = new ActivityObservable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        ActivityEvent event = new ActivityEvent(this, Config.EVENT_SURVEY_STARTED, null);
        observable.notifyExperimentController(event);
    }

    public void addObserver(Observer observer) {
        observable.addObserver(observer);
    }
}
