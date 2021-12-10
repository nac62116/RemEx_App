package de.ur.remex.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
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

public class LikertQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private String scaleLabelMinText;
    private String scaleLabelMaxText;
    private int initialScaleValue;
    private int scaleItemCount;
    private Button nextButton;
    private TextView scaleValueView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likert_question);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
        if (getIntent().getStringExtra(Config.SCALE_LABEL_MIN_KEY) != null) {
            scaleLabelMinText = getIntent().getStringExtra(Config.SCALE_LABEL_MIN_KEY);
        }
        if (getIntent().getStringExtra(Config.SCALE_LABEL_MAX_KEY) != null) {
            scaleLabelMaxText = getIntent().getStringExtra(Config.SCALE_LABEL_MAX_KEY);
        }
        initialScaleValue = getIntent().getIntExtra(Config.INITIAL_SCALE_VALUE_KEY, 1);
        scaleItemCount = getIntent().getIntExtra(Config.SCALE_ITEM_COUNT_KEY, 5);
        if (getIntent().getStringExtra(Config.QUESTION_TEXT_KEY) != null) {
            questionText = getIntent().getStringExtra(Config.QUESTION_TEXT_KEY);
        }
        if (getIntent().getStringExtra(Config.QUESTION_HINT_KEY) != null) {
            questionHint = getIntent().getStringExtra(Config.QUESTION_HINT_KEY);
        }
        else {
            questionHint = "";
        }
    }

    private void initViews() {
        TextView questionTextView = findViewById(R.id.likertQuestionText);
        questionTextView.setText(questionText);
        TextView questionHintView = findViewById(R.id.likertQuestionHint);
        questionHintView.setText(questionHint);
        TextView scaleLabelMinView = findViewById(R.id.likertQuestionScaleLabelMin);
        scaleLabelMinView.setText(scaleLabelMinText);
        TextView scaleLabelMaxView = findViewById(R.id.likertQuestionScaleLabelMax);
        scaleLabelMaxView.setText(scaleLabelMaxText);
        scaleValueView = findViewById(R.id.likertQuestionScaleValue);
        String initialScaleValueString = Integer.toString(initialScaleValue);
        scaleValueView.setText(initialScaleValueString);
        SeekBar likertScale = findViewById(R.id.likertQuestionScale);
        likertScale.setMax(scaleItemCount - 1);
        likertScale.setProgress(initialScaleValue - 1);
        likertScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String progressString = Integer.toString(progress + 1);
                scaleValueView.setText(progressString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        nextButton = findViewById(R.id.likertQuestionNextButton);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            Event event = new Event(this, Config.EVENT_NEXT_QUESTION, scaleValueView.getText().toString());
            OBSERVABLE.notifyExperimentController(event);
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
