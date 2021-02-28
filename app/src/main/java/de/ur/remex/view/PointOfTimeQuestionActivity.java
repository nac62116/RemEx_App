package de.ur.remex.view;

import android.app.AlertDialog;
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

import java.util.Calendar;
import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.admin.CreateVPActivity;
import de.ur.remex.model.experiment.questionnaire.QuestionType;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class PointOfTimeQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private QuestionType questionType;
    private EditText answerTextField;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_time_question);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
        if (getIntent().getSerializableExtra(Config.QUESTION_TYPE_KEY) != null) {
            questionType = (QuestionType) getIntent().getSerializableExtra(Config.QUESTION_TYPE_KEY);
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
        answerTextField = findViewById(R.id.pointOfTimeQuestionAnswerField);
        answerTextField.setOnClickListener(this);
        if (questionType.equals(QuestionType.DAYTIME)) {
            answerTextField.setHint(Config.DAYTIME_ANSWER_HINT);
        }
        else {
            answerTextField.setHint(Config.DATE_ANSWER_HINT);
        }
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
            if (questionType.equals(QuestionType.DAYTIME)) {
                TimePickerDialog timePickerDialog = createTimePickerDialog();
                timePickerDialog.show();
            }
            else {
                DatePickerDialog datePickerDialog = createDatePickerDialog();
                datePickerDialog.show();
            }
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
            answerTextField.setText(dateString);
        }, currentYear, currentMonth, currentDay);
        return datePickerDialog;
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }
}
