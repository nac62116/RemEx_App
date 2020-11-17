package de.ur.remex.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.utilities.ActivityObservable;
import de.ur.remex.utilities.Config;

public class InstructionActivity extends AppCompatActivity implements View.OnClickListener {

    private static ActivityObservable observable = new ActivityObservable();

    private String header;
    private String text;
    private TextView headerTextView;
    private TextView bodyTextView;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        getIntentExtras();
        initViews();
    }

    private void getIntentExtras() {
        if (getIntent().getStringExtra("header") != null) {
            header = getIntent().getStringExtra("header");
        }
        if (getIntent().getStringExtra("text") != null) {
            text = getIntent().getStringExtra("text");
        }
    }

    private void initViews() {
        headerTextView = findViewById(R.id.instructionHeader);
        bodyTextView = findViewById(R.id.instructionText);
        nextButton = findViewById(R.id.instructionNextButton);
        nextButton.setOnClickListener(this);
        if (header != null) {
            headerTextView.setText(header);
        }
        if (text != null) {
            bodyTextView.setText(text);
        }
    }

    public Context getContext() {
        return this;
    }

    public void addObserver(Observer observer) {
        Log.e("InstructionActivity", "observerAdded");
        observable.addObserver(observer);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            // Notify Experiment Controller
            observable.notifyExperimentController(Config.EVENT_NEXT_STEP);
        }
    }
}
