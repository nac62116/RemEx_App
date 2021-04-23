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

import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class TextQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private EditText answerTextField;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_question);
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
        TextView questionTextView = findViewById(R.id.textQuestionText);
        questionTextView.setText(questionText);
        TextView questionHintView = findViewById(R.id.textQuestionHint);
        questionHintView.setText(questionHint);
        nextButton = findViewById(R.id.textQuestionNextButton);
        nextButton.setOnClickListener(this);
        nextButton.setEnabled(false);
        nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button_deactivated));
        nextButton.setTextColor(Color.LTGRAY);
        answerTextField = findViewById(R.id.textQuestionAnswerField);
        answerTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (answerTextField.getText().toString().equals("")) {
                    nextButton.setEnabled(false);
                    nextButton.setBackground(ContextCompat.getDrawable(TextQuestionActivity.this, R.drawable.next_button_deactivated));
                    nextButton.setTextColor(Color.LTGRAY);
                }
                else {
                    nextButton.setEnabled(true);
                    nextButton.setBackground(ContextCompat.getDrawable(TextQuestionActivity.this, R.drawable.next_button));
                    nextButton.setTextColor(TextQuestionActivity.this.getResources().getColor(R.color.themeColor));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            Event event = new Event(this, Config.EVENT_NEXT_QUESTION, answerTextField.getText().toString());
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
