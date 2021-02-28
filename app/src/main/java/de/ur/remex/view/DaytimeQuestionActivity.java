package de.ur.remex.view;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class DaytimeQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private EditText answerTextField;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daytime_question);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
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
        TextView questionTextView = findViewById(R.id.daytimeQuestionText);
        questionTextView.setText(questionText);
        TextView questionHintView = findViewById(R.id.daytimeQuestionHint);
        questionHintView.setText(questionHint);
        nextButton = findViewById(R.id.daytimeQuestionNextButton);
        nextButton.setOnClickListener(this);
        nextButton.setEnabled(false);
        nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button_deactivated));
        nextButton.setTextColor(Color.LTGRAY);
        answerTextField = findViewById(R.id.daytimeQuestionAnswerField);
        answerTextField.setOnClickListener(this);
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            Event event = new Event(this, Config.EVENT_NEXT_QUESTION, answerTextField.getText().toString());
            OBSERVABLE.notifyExperimentController(event);
        }
        else if (v.equals(answerTextField)) {
            nextButton.setEnabled(true);
            nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button));
            nextButton.setTextColor(this.getResources().getColor(R.color.themeColor));
            TimePickerDialog timePickerDialog = createTimePickerDialog();
            timePickerDialog.show();
        }
    }

    private TimePickerDialog createTimePickerDialog() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;

        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String hour, min;
            if (hourOfDay < 10) {
                hour = "0" + hourOfDay;
            }
            else {
                hour = "" + hourOfDay;
            }
            if (minute < 10) {
                min = "0" + minute;
            }
            else {
                min = "" + minute;
            }
            String timeString = hour + ":" + min + " Uhr";
            answerTextField.setText(timeString);
        }, currentHour, currentMinute, true);
        return timePickerDialog;
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }
}
