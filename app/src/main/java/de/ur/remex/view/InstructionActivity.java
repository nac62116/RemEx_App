package de.ur.remex.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.utilities.ActivityEvent;
import de.ur.remex.utilities.ActivityObservable;
import de.ur.remex.Config;

// TODO: Image and video support

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
        if (getIntent().getStringExtra(Config.INSTRUCTION_HEADER_KEY) != null) {
            header = getIntent().getStringExtra(Config.INSTRUCTION_HEADER_KEY);
        }
        if (getIntent().getStringExtra(Config.INSTRUCTION_TEXT_KEY) != null) {
            text = getIntent().getStringExtra(Config.INSTRUCTION_TEXT_KEY);
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
        else {
            headerTextView.setVisibility(View.GONE);
        }
        if (text != null) {
            bodyTextView.setText(text);
        }
        else {
            bodyTextView.setVisibility(View.GONE);
        }
    }

    public void addObserver(Observer observer) {
        observable.addObserver(observer);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            ActivityEvent event = new ActivityEvent(this, Config.EVENT_NEXT_STEP, null);
            observable.notifyExperimentController(event);
        }
    }
}
