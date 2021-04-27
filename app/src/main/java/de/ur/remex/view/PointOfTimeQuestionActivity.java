package de.ur.remex.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.model.experiment.questionnaire.PointOfTimeType;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class PointOfTimeQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private ArrayList<String> pointOfTimeTypeNames;
    private EditText dateAnswerTextField;
    private EditText daytimeAnswerTextField;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_time_question);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
        if (getIntent().getStringArrayListExtra(Config.POINT_OF_TIME_TYPES_KEY) != null) {
            pointOfTimeTypeNames = getIntent().getStringArrayListExtra(Config.POINT_OF_TIME_TYPES_KEY);
        }
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
        TextView questionTextView = findViewById(R.id.pointOfTimeQuestionText);
        questionTextView.setText(questionText);
        TextView questionHintView = findViewById(R.id.pointOfTimeQuestionHint);
        questionHintView.setText(questionHint);
        nextButton = findViewById(R.id.pointOfTimeQuestionNextButton);
        nextButton.setOnClickListener(this);
        nextButton.setEnabled(false);
        nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button_deactivated));
        nextButton.setTextColor(Color.LTGRAY);
        dateAnswerTextField = findViewById(R.id.pointOfTimeQuestionDateAnswerField);
        dateAnswerTextField.setOnClickListener(this);
        daytimeAnswerTextField = findViewById(R.id.pointOfTimeQuestionDaytimeAnswerField);
        daytimeAnswerTextField.setOnClickListener(this);
        if (!pointOfTimeTypeNames.contains(PointOfTimeType.DATE.name())) {
            dateAnswerTextField.setVisibility(View.GONE);
        }
        if (!pointOfTimeTypeNames.contains(PointOfTimeType.DAYTIME.name())) {
            daytimeAnswerTextField.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            String answerString = dateAnswerTextField.getText().toString() + " " + daytimeAnswerTextField.getText().toString();
            Event event = new Event(this, Config.EVENT_NEXT_QUESTION, answerString.trim());
            OBSERVABLE.notifyExperimentController(event);
        }
        else if (v.equals(dateAnswerTextField)) {
            DatePickerDialog datePickerDialog = createDatePickerDialog();
            datePickerDialog.show();
        }
        else if (v.equals(daytimeAnswerTextField)) {
            TimePickerDialog timePickerDialog = createTimePickerDialog();
            timePickerDialog.show();
        }
    }

    private DatePickerDialog createDatePickerDialog() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        final int currentYear = c.get(Calendar.YEAR);
        final int currentMonth = c.get(Calendar.MONTH);
        final int currentDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog;

        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            month += 1;
            String dayString, monthString;
            if (month < 10) {
                monthString = "0" + month;
            }
            else {
                monthString = "" + month;
            }
            if (dayOfMonth < 10) {
                dayString = "0" + dayOfMonth;
            }
            else {
                dayString = "" + dayOfMonth;
            }
            String dateString = dayString + "." + monthString + "." + year;
            dateAnswerTextField.setText(dateString);
            dateAnswerTextField.setHint("");
            updateNextButton();
        }, currentYear, currentMonth, currentDay);
        return datePickerDialog;
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
            daytimeAnswerTextField.setText(timeString);
            daytimeAnswerTextField.setHint("");
            updateNextButton();
        }, currentHour, currentMinute, true);
        return timePickerDialog;
    }

    private void updateNextButton() {
        if (pointOfTimeTypeNames.contains(PointOfTimeType.DATE.name()) &&
                pointOfTimeTypeNames.contains(PointOfTimeType.DAYTIME.name())) {
            if (!dateAnswerTextField.getText().toString().equals("") &&
                    !daytimeAnswerTextField.getText().toString().equals("")) {
                nextButton.setEnabled(true);
                nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button));
                nextButton.setTextColor(this.getResources().getColor(R.color.themeColor));
            }
        }
        else if (pointOfTimeTypeNames.contains(PointOfTimeType.DATE.name())) {
            if (!dateAnswerTextField.getText().toString().equals("")) {
                nextButton.setEnabled(true);
                nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button));
                nextButton.setTextColor(this.getResources().getColor(R.color.themeColor));
            }
        }
        else {
            if (!daytimeAnswerTextField.getText().toString().equals("")) {
                nextButton.setEnabled(true);
                nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button));
                nextButton.setTextColor(this.getResources().getColor(R.color.themeColor));
            }
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
