package de.ur.remex.view;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.model.experiment.questionnaire.TimeIntervalType;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class TimeIntervalQuestionActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private Button nextButton;
    private ArrayList<String> timeIntervalTypeNames;
    private ArrayList<EditText> activeAnswerFields;
    private HashMap<EditText, TextView> suffixMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_interval_question);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
        if (getIntent().getStringArrayListExtra(Config.TIME_INTERVALL_TYPES_KEY) != null) {
            timeIntervalTypeNames = getIntent().getStringArrayListExtra(Config.TIME_INTERVALL_TYPES_KEY);
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
        TextView questionTextView = findViewById(R.id.timeIntervalQuestionText);
        questionTextView.setText(questionText);
        TextView questionHintView = findViewById(R.id.timeIntervalQuestionHint);
        questionHintView.setText(questionHint);
        nextButton = findViewById(R.id.timeIntervalQuestionNextButton);
        nextButton.setOnClickListener(this);
        nextButton.setEnabled(false);
        nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button_deactivated));
        nextButton.setTextColor(Color.LTGRAY);
        activeAnswerFields = new ArrayList<>();
        suffixMap = new HashMap<>();
        EditText yearsAnswerField = findViewById(R.id.timeIntervalQuestionYearAnswerField);
        TextView yearsSuffix = findViewById(R.id.timeIntervalQuestionYearSuffix);
        if (timeIntervalTypeNames.contains(TimeIntervalType.YEARS.name())) {
            yearsAnswerField.addTextChangedListener(this);
            activeAnswerFields.add(yearsAnswerField);
            suffixMap.put(yearsAnswerField, yearsSuffix);
        }
        else {
            yearsAnswerField.setVisibility(View.GONE);
            yearsSuffix.setVisibility(View.GONE);
        }
        EditText monthsAnswerField = findViewById(R.id.timeIntervalQuestionMonthAnswerField);
        TextView monthsSuffix = findViewById(R.id.timeIntervalQuestionMonthSuffix);
        if (timeIntervalTypeNames.contains(TimeIntervalType.MONTHS.name())) {
            monthsAnswerField.addTextChangedListener(this);
            activeAnswerFields.add(monthsAnswerField);
            suffixMap.put(monthsAnswerField, monthsSuffix);
        }
        else {
            monthsAnswerField.setVisibility(View.GONE);
            monthsSuffix.setVisibility(View.GONE);
        }
        EditText daysAnswerField = findViewById(R.id.timeIntervalQuestionDayAnswerField);
        TextView daysSuffix = findViewById(R.id.timeIntervalQuestionDaySuffix);
        if (timeIntervalTypeNames.contains(TimeIntervalType.DAYS.name())) {
            daysAnswerField.addTextChangedListener(this);
            activeAnswerFields.add(daysAnswerField);
            suffixMap.put(daysAnswerField, daysSuffix);
        }
        else {
            daysAnswerField.setVisibility(View.GONE);
            daysSuffix.setVisibility(View.GONE);
        }
        EditText hoursAnswerField = findViewById(R.id.timeIntervalQuestionHourAnswerField);
        TextView hoursSuffix = findViewById(R.id.timeIntervalQuestionHourSuffix);
        if (timeIntervalTypeNames.contains(TimeIntervalType.HOURS.name())) {
            hoursAnswerField.addTextChangedListener(this);
            activeAnswerFields.add(hoursAnswerField);
            suffixMap.put(hoursAnswerField, hoursSuffix);
        }
        else {
            hoursAnswerField.setVisibility(View.GONE);
            hoursSuffix.setVisibility(View.GONE);
        }
        EditText minutesAnswerField = findViewById(R.id.timeIntervalQuestionMinuteAnswerField);
        TextView minutesSuffix = findViewById(R.id.timeIntervalQuestionMinuteSuffix);
        if (timeIntervalTypeNames.contains(TimeIntervalType.MINUTES.name())) {
            minutesAnswerField.addTextChangedListener(this);
            activeAnswerFields.add(minutesAnswerField);
            suffixMap.put(minutesAnswerField, minutesSuffix);
        }
        else {
            minutesAnswerField.setVisibility(View.GONE);
            minutesSuffix.setVisibility(View.GONE);
        }
        EditText secondsAnswerField = findViewById(R.id.timeIntervalQuestionSecondAnswerField);
        TextView secondsSuffix = findViewById(R.id.timeIntervalQuestionSecondSuffix);
        if (timeIntervalTypeNames.contains(TimeIntervalType.SECONDS.name())) {
            secondsAnswerField.addTextChangedListener(this);
            activeAnswerFields.add(secondsAnswerField);
            suffixMap.put(secondsAnswerField, secondsSuffix);
        }
        else {
            secondsAnswerField.setVisibility(View.GONE);
            secondsSuffix.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            StringBuilder answer = new StringBuilder();
            for (EditText activeAnswerField: activeAnswerFields) {
                TextView suffix = suffixMap.get(activeAnswerField);
                assert suffix != null;
                answer.append(activeAnswerField.getText().toString())
                        .append(" ")
                        .append(suffix.getText().toString())
                        .append(" ");
            }
            Event event = new Event(this, Config.EVENT_NEXT_QUESTION, answer.toString().trim());
            OBSERVABLE.notifyExperimentController(event);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean allAnswersProvided = true;
        for (EditText activeAnswerField: activeAnswerFields) {
            if (activeAnswerField.getText().toString().equals("")) {
                allAnswersProvided = false;
            }
            TextView suffix = suffixMap.get(activeAnswerField);
            assert suffix != null;
            String newSuffix = null;
            if (activeAnswerField.getText().toString().equals("1")) {
                if (!(suffix.getText().toString().equals("Jahr") ||
                        suffix.getText().toString().equals("Monat") ||
                        suffix.getText().toString().equals("Tag") ||
                        suffix.getText().toString().equals("Stunde") ||
                        suffix.getText().toString().equals("Minute") ||
                        suffix.getText().toString().equals("Sekunde"))) {
                    newSuffix = suffix.getText().toString().substring(0, suffix.getText().toString().length() - 1);
                }
            }
            else {
                if (suffix.getText().toString().equals("Jahr") ||
                        suffix.getText().toString().equals("Monat") ||
                        suffix.getText().toString().equals("Tag")) {
                    newSuffix = suffix.getText().toString() + "e";
                }
                else if (suffix.getText().toString().equals("Stunde") ||
                        suffix.getText().toString().equals("Minute") ||
                        suffix.getText().toString().equals("Sekunde")){
                    newSuffix = suffix.getText().toString() + "n";
                }
            }
            if (newSuffix != null) {
                suffix.setText(newSuffix);
            }
        }
        if (allAnswersProvided) {
            nextButton.setEnabled(true);
            nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button));
            nextButton.setTextColor(this.getResources().getColor(R.color.themeColor));
        }
        else {
            nextButton.setEnabled(false);
            nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button_deactivated));
            nextButton.setTextColor(Color.LTGRAY);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

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
