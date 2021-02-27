package de.ur.remex.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.model.experiment.questionnaire.QuestionType;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class ChoiceQuestionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private QuestionType questionType;
    private String[] answerTexts;
    private ArrayList<String> userAnswers;
    private View lastClickedAnswer;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_question);
        userAnswers = new ArrayList<>();
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
        if (getIntent().getStringArrayExtra(Config.ANSWER_TEXTS_KEY) != null) {
            answerTexts = getIntent().getStringArrayExtra(Config.ANSWER_TEXTS_KEY);
        }
        if (getIntent().getStringExtra(Config.QUESTION_TEXT_KEY) != null) {
            questionText = getIntent().getStringExtra(Config.QUESTION_TEXT_KEY);
        }
        if (getIntent().getSerializableExtra(Config.QUESTION_TYPE_KEY) != null) {
            questionType = (QuestionType) getIntent().getSerializableExtra(Config.QUESTION_TYPE_KEY);
        }
        if (getIntent().getStringExtra(Config.QUESTION_HINT_KEY) != null) {
            questionHint = getIntent().getStringExtra(Config.QUESTION_HINT_KEY);
        }
        else {
            questionHint = "";
        }
    }

    private void initViews() {
        TextView questionTextView = findViewById(R.id.singleChoiceText);
        questionTextView.setText(questionText);
        TextView questionHintView = findViewById(R.id.singleChoiceHint);
        questionHintView.setText(questionHint);
        nextButton = findViewById(R.id.singleChoiceNextButton);
        nextButton.setOnClickListener(this);
        nextButton.setEnabled(false);
        nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button_deactivated));
        nextButton.setTextColor(Color.LTGRAY);
        ListView answerListView = findViewById(R.id.singleChoiceAnswers);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, answerTexts);
        answerListView.setAdapter(adapter);
        answerListView.setOnItemClickListener(this);
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            Event event;
            if (questionType.equals(QuestionType.SINGLE_CHOICE)) {
                event = new Event(this, Config.EVENT_NEXT_QUESTION, userAnswers.get(0));
            }
            else {
                event = new Event(this, Config.EVENT_NEXT_QUESTION, userAnswers);
            }
            OBSERVABLE.notifyExperimentController(event);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (questionType.equals(QuestionType.SINGLE_CHOICE)) {
            view.setBackgroundColor(Color.LTGRAY);
            if (lastClickedAnswer != null && !(userAnswers.get(0).equals(answerTexts[position]))) {
                lastClickedAnswer.setBackgroundColor(this.getResources().getColor(R.color.backgroundColor));
            }
            lastClickedAnswer = view;
            if (userAnswers.isEmpty()) {
                userAnswers.add(answerTexts[position]);
            }
            else {
                userAnswers.set(0, answerTexts[position]);
            }
        }
        else {
            if (userAnswers.contains(answerTexts[position])) {
                view.setBackgroundColor(this.getResources().getColor(R.color.backgroundColor));
                userAnswers.remove(answerTexts[position]);
            }
            else {
                view.setBackgroundColor(Color.LTGRAY);
                userAnswers.add(answerTexts[position]);
            }
        }
        if (userAnswers.isEmpty()) {
            nextButton.setEnabled(false);
            nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button_deactivated));
            nextButton.setTextColor(Color.LTGRAY);
        }
        else {
            nextButton.setEnabled(true);
            nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button));
            nextButton.setTextColor(this.getResources().getColor(R.color.themeColor));
        }
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }
}
