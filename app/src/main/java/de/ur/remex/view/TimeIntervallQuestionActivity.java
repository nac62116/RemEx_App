package de.ur.remex.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class TimeIntervallQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private EditText answerInputField;
    private TextView answerTextView;
    private Button nextButton;
    private int currentPlaceValue;
    private StringBuilder currentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_intervall_question);
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
        TextView questionTextView = findViewById(R.id.timeIntervallQuestionText);
        questionTextView.setText(questionText);
        TextView questionHintView = findViewById(R.id.timeIntervallQuestionHint);
        questionHintView.setText(questionHint);
        nextButton = findViewById(R.id.timeIntervallQuestionNextButton);
        nextButton.setOnClickListener(this);
        answerTextView = findViewById(R.id.timeIntervallQuestionAnswerView);
        answerInputField = findViewById(R.id.timeIntervallQuestionAnswerField);
        currentText = new StringBuilder("000000");
        currentPlaceValue = currentText.length() - 1;
        answerInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // User wrote a new number
                if (count != 0) {
                    if (currentPlaceValue >= 0) {
                        StringBuilder inputString = new StringBuilder(s);
                        char userInput = inputString.charAt(start);
                        currentText.deleteCharAt(0);
                        currentText.append(userInput);
                        currentPlaceValue -= 1;
                    }
                    else {
                        return;
                    }
                }
                // User deleted a number
                else {
                    if (currentPlaceValue != currentText.length() - 1) {
                        currentText.deleteCharAt(currentText.length() - 1);
                        currentText.insert(0, "0");
                        currentPlaceValue += 1;
                    }
                    else {
                        return;
                    }
                }
                String newText = currentText.substring(0,2) + "d "
                        + currentText.substring(2,4) + "h "
                        + currentText.substring(4,6) + "m";
                answerTextView.setText(newText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            // TODO: Answer Format (currently "00d 00h 00m")
            Event event = new Event(this, Config.EVENT_NEXT_QUESTION, answerTextView.getText().toString());
            OBSERVABLE.notifyExperimentController(event);
        }
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }
}
