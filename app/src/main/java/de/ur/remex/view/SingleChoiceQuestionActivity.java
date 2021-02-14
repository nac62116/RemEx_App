package de.ur.remex.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class SingleChoiceQuestionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String instruction;
    private String questionText;
    private String questionHint;
    private String[] answerTexts;
    private String userAnswer;
    private boolean wasInstructed;
    private Button nextButton;
    private TextView questionTextView;
    private TextView questionHintView;
    private TextView scrollHintView;
    private ListView answerListView;
    private View lastClickedAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_choice_question);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
        if (getIntent().getStringExtra(Config.QUESTIONNAIRE_INSTRUCTION_KEY) != null) {
            instruction = getIntent().getStringExtra(Config.QUESTIONNAIRE_INSTRUCTION_KEY);
            wasInstructed = false;
        }
        else {
            wasInstructed = true;
        }
        if (getIntent().getStringArrayExtra(Config.ANSWER_TEXTS_KEY) != null) {
            answerTexts = getIntent().getStringArrayExtra(Config.ANSWER_TEXTS_KEY);
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
        questionTextView = findViewById(R.id.singleChoiceText);
        questionHintView = findViewById(R.id.singleChoiceHint);
        scrollHintView = findViewById(R.id.singleChoiceScrollHint);
        nextButton = findViewById(R.id.singleChoiceNextButton);
        nextButton.setOnClickListener(this);
        if (!wasInstructed) {
            initInstructionViews();
        }
        else {
            initQuestionViews();
        }
    }

    private void initInstructionViews() {
        questionTextView.setText(instruction);
        questionHintView.setText("");
        scrollHintView.setVisibility(View.INVISIBLE);
    }

    private void initQuestionViews() {
        answerListView = findViewById(R.id.singleChoiceAnswers);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, answerTexts);
        answerListView.setAdapter(adapter);
        answerListView.setOnItemClickListener(this);
        questionTextView.setText(questionText);
        questionHintView.setText(questionHint);
        scrollHintView.setVisibility(View.VISIBLE);
        wasInstructed = true;
        nextButton.setEnabled(false);
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            if (wasInstructed) {
                Event event = new Event(this, Config.EVENT_NEXT_QUESTION, userAnswer);
                OBSERVABLE.notifyExperimentController(event);
            }
            else {
                initQuestionViews();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, answerTexts);
        view.setBackgroundColor(Color.LTGRAY);
        if (lastClickedAnswer != null && !(userAnswer.equals(answerTexts[position]))) {
            lastClickedAnswer.setBackgroundColor(this.getResources().getColor(R.color.backgroundColor));
        }
        lastClickedAnswer = view;
        userAnswer = answerTexts[position];
        nextButton.setEnabled(true);
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }
}
