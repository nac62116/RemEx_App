package de.ur.remex.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.model.experiment.questionnaire.ChoiceType;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;

public class ChoiceQuestionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String questionText;
    private String questionHint;
    private ChoiceType choiceType;
    private String[] answerTexts;
    private ArrayList<String> userAnswers;
    private CustomArrayAdapter adapter;
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
        if (getIntent().getSerializableExtra(Config.CHOICE_TYPE_KEY) != null) {
            choiceType = (ChoiceType) getIntent().getSerializableExtra(Config.CHOICE_TYPE_KEY);
        }
        if (getIntent().getStringExtra(Config.QUESTION_HINT_KEY) != null) {
            questionHint = getIntent().getStringExtra(Config.QUESTION_HINT_KEY);
        }
        else {
            questionHint = "";
        }
    }

    private void initViews() {
        TextView questionTextView = findViewById(R.id.choiceQuestionText);
        questionTextView.setText(questionText);
        TextView questionHintView = findViewById(R.id.choiceQuestionHint);
        questionHintView.setText(questionHint);
        nextButton = findViewById(R.id.choiceQuestionNextButton);
        nextButton.setOnClickListener(this);
        nextButton.setEnabled(false);
        nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.next_button_deactivated));
        nextButton.setTextColor(Color.LTGRAY);
        ListView answerListView = findViewById(R.id.choiceAnswerList);
        adapter = new CustomArrayAdapter(this, android.R.layout.simple_list_item_1, answerTexts);
        answerListView.setAdapter(adapter);
        answerListView.setOnItemClickListener(this);
        Runnable fitsOnScreen = () -> {
            int last = answerListView.getLastVisiblePosition();
            if (last == answerListView.getCount() - 1 && answerListView.getChildAt(last).getBottom() <= answerListView.getHeight()) {
                TextView scrollHint = findViewById(R.id.choiceQuestionScrollHint);
                scrollHint.setVisibility(View.INVISIBLE);
            }
        };
        answerListView.post(fitsOnScreen);
        if (choiceType.equals(ChoiceType.SINGLE_CHOICE)) {
            TextView multipleChoiceHint = findViewById(R.id.multipleChoiceHint);
            multipleChoiceHint.setVisibility(View.INVISIBLE);
        }
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            Event event;
            if (choiceType.equals(ChoiceType.SINGLE_CHOICE)) {
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
        if (choiceType.equals(ChoiceType.SINGLE_CHOICE)) {
            if (userAnswers.isEmpty()) {
                userAnswers.add(answerTexts[position]);
            }
            else {
                userAnswers.set(0, answerTexts[position]);
            }
        }
        else {
            if (userAnswers.contains(answerTexts[position])) {
                userAnswers.remove(answerTexts[position]);
            }
            else {
                userAnswers.add(answerTexts[position]);
            }
        }
        adapter.notifyDataSetChanged();
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

    public class CustomArrayAdapter extends ArrayAdapter<String> {

        private final Context context;

        public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
            super(context, resource, objects);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            if (userAnswers.contains(answerTexts[position])) {
                view.setBackgroundColor(Color.LTGRAY);
            }
            else {
                view.setBackgroundColor(context.getResources().getColor(R.color.backgroundColor));
            }

            return view;
        }
    }
}
