package de.ur.remex.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;
import de.ur.remex.Config;

public class SurveyEntranceActivity extends AppCompatActivity {

    private static Observable observable = new Observable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        Event event = new Event(this, Config.EVENT_SURVEY_STARTED, null);
        observable.notifyExperimentController(event);
    }

    public void addObserver(Observer observer) {
        observable.addObserver(observer);
    }
}
