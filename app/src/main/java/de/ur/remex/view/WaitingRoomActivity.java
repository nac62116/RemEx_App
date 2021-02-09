package de.ur.remex.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;
import de.ur.remex.Config;

public class WaitingRoomActivity extends AppCompatActivity {

    private static final Observable OBSERVABLE = new Observable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        Event event = new Event(this, Config.EVENT_WAITING_ROOM_ENTERED, null);
        OBSERVABLE.notifyExperimentController(event);

        TextView waitingRoomText = findViewById(R.id.waitingRoomText);
        if (getIntent().getStringExtra(Config.WAITING_ROOM_TEXT_KEY) != null) {
            String text = getIntent().getStringExtra(Config.WAITING_ROOM_TEXT_KEY);
            waitingRoomText.setText(text);
        }
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
