package de.ur.remex.view;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.model.experiment.breathingExercise.BreathingMode;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

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

public class BreathingExerciseActivity extends AppCompatActivity {

    private static final Observable OBSERVABLE = new Observable();

    // Breathing exercise
    private TextView timeTextView;
    private TextView circleTextView;
    private Button nextButton;
    private ImageView breathingCircleView;
    private MediaPlayer mediaPlayer;
    private int progressCounterInMillis;
    private boolean isInhaling;
    private Timer timer;

    // Model
    private BreathingMode mode;
    private int durationInMin;
    private int breathingFrequencyInSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing_exercise);
        getIntentExtras();
        initBreathingExercise();
    }

    private void getIntentExtras() {
        mode = (BreathingMode) getIntent().getSerializableExtra(Config.BREATHING_MODE_KEY);
        durationInMin = getIntent().getIntExtra(Config.BREATHING_DURATION_KEY, 0);
        breathingFrequencyInSec = getIntent().getIntExtra(Config.BREATHING_FREQUENCY_KEY, 0);
    }

    private void initBreathingExercise() {
        circleTextView = findViewById(R.id.circleText);
        timeTextView = findViewById(R.id.breathingHeader);
        breathingCircleView = findViewById(R.id.breathCircle);
        nextButton = findViewById(R.id.breathingNextButton);
        mediaPlayer = null;
        nextButton.setVisibility(View.INVISIBLE);
        String duration;
        if (durationInMin < 10) {
            duration = "0" + durationInMin + ":00";
        }
        else {
            duration = "" + durationInMin + ":00";
        }
        timeTextView.setText(duration);
        if (mode.equals(BreathingMode.MOVING_CIRCLE)) {
            circleTextView.setText(Config.BREATHING_INHALE_TEXT);
        }
        else {
            circleTextView.setText(Config.BREATHING_NEUTRAL_TEXT);
        }
        startExercise();
    }

    // Starting Timer for the breathing exercise
    private void startExercise() {
        timer = new Timer();
        isInhaling = true;
        progressCounterInMillis = 1000;
        playGong();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> onProgress());
            }
        }, 0, Config.BREATHING_TIMER_FREQUENCY_MS);
    }

    private void playGong() {
        if (mode.equals(BreathingMode.MOVING_CIRCLE)) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.tibetian_bowl);
                // Play tibetian bowl sound
                mediaPlayer.start();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }, Config.BREATHING_GONG_LENGTH_SEC * 1000);
            }
        }
    }

    public void onProgress() {
        int breathingFrequencyInMillis = breathingFrequencyInSec * 1000;
        int durationInMillis = durationInMin * 60 * 1000;
        // Update clock
        boolean isTicking = progressCounterInMillis % 1000 == 0;
        if (isTicking) {
            setNewTimeString();
        }
        // Manage circle movement
        if (mode.equals(BreathingMode.MOVING_CIRCLE)) {
            // Switch inhale/exhale, associated texts and play the gong every breathingFrequencyInMillis
            if (progressCounterInMillis % breathingFrequencyInMillis == 0) {
                isInhaling = !isInhaling;
                if (isInhaling) {
                    circleTextView.setText(Config.BREATHING_INHALE_TEXT);
                }
                else {
                    circleTextView.setText(Config.BREATHING_EXHALE_TEXT);
                }
                playGong();
            }
            // Move circle every Config.BREATHING_TIMER_FREQUENCY_MS
            if (isInhaling) {
                breathingCircleView.setAlpha(breathingCircleView.getAlpha() + 0.01f);
                breathingCircleView.setPadding(breathingCircleView.getPaddingLeft() - 1,
                        breathingCircleView.getPaddingTop() - 1,
                        breathingCircleView.getPaddingRight() - 1,
                        breathingCircleView.getPaddingBottom() - 1);
            }
            else {
                breathingCircleView.setAlpha(breathingCircleView.getAlpha() - 0.01f);
                breathingCircleView.setPadding(breathingCircleView.getPaddingLeft() + 1,
                        breathingCircleView.getPaddingTop() + 1,
                        breathingCircleView.getPaddingRight() + 1,
                        breathingCircleView.getPaddingBottom() + 1);
            }
        }

        if (progressCounterInMillis >= durationInMillis) {
            timer.cancel();
            finishExercise();
        }
        progressCounterInMillis += Config.BREATHING_TIMER_FREQUENCY_MS;
    }

    private void setNewTimeString() {
        String currentTimeString = timeTextView.getText().toString();
        String newTimeString;
        String seconds = currentTimeString.substring(3);
        String minutes = currentTimeString.substring(0,2);
        int sec = Integer.parseInt(seconds, 10);
        int min = Integer.parseInt(minutes, 10);

        if (sec == 0) {
            min -= 1;
            sec = 60;
        }
        sec -= 1;
        if (min < 10) {
            minutes = "0" + min;
        }
        else {
            minutes = Integer.toString(min);
        }
        if (sec < 10) {
            seconds = "0" + sec;
        }
        else {
            seconds = Integer.toString(sec);
        }
        newTimeString = minutes + ":" + seconds;
        timeTextView.setText(newTimeString);
    }

    private void finishExercise() {
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setOnClickListener(v -> {
            Event event = new Event(this, Config.EVENT_NEXT_STEP, null);
            OBSERVABLE.notifyExperimentController(event);
        });
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
