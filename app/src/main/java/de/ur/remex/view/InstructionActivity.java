package de.ur.remex.view;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;
import de.ur.remex.Config;

// TODO: Video support

public class InstructionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String header;
    private String text;
    private String imageFileName;
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
        if (getIntent().getStringExtra(Config.INSTRUCTION_IMAGE_KEY) != null) {
            imageFileName = getIntent().getStringExtra(Config.INSTRUCTION_IMAGE_KEY);
        }
    }

    private void initViews() {
        TextView headerTextView = findViewById(R.id.instructionHeader);
        ImageView imageView = findViewById(R.id.instructionImageView);
        TextView bodyTextView = findViewById(R.id.instructionText);
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
        if (imageFileName != null) {
            Uri image = Uri.parse("android.resource://" + this.getPackageName() + "/drawable/" + imageFileName);
            imageView.setImageURI(image);
        }
        else {
            imageView.setVisibility(View.GONE);
        }
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            Event event = new Event(this, Config.EVENT_NEXT_STEP, null);
            OBSERVABLE.notifyExperimentController(event);
        }
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }
}
