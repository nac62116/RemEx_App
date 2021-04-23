package de.ur.remex.view;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;
import de.ur.remex.Config;

public class InstructionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Observable OBSERVABLE = new Observable();

    private String header;
    private String text;
    private String imageFileName;
    private String videoFileName;
    private Button nextButton;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        getIntentExtras();
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hiding mediaController to prevent window leaked error which leads to app crash
        if (mediaController != null) {
            mediaController.hide();
        }
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
        if (getIntent().getStringExtra(Config.INSTRUCTION_VIDEO_KEY) != null) {
            videoFileName = getIntent().getStringExtra(Config.INSTRUCTION_VIDEO_KEY);
        }
    }

    private void initViews() {
        TextView headerTextView = findViewById(R.id.instructionHeader);
        ImageView imageView = findViewById(R.id.instructionImageView);
        VideoView videoView = findViewById(R.id.instructionVideoView);
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
            Uri imageUri = Uri.parse("android.resource://" + this.getPackageName() + "/drawable/" + imageFileName);
            imageView.setImageURI(imageUri);
        }
        else {
            imageView.setVisibility(View.GONE);
        }
        if (videoFileName != null) {
            imageView.setVisibility(View.INVISIBLE);
            mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            Uri videoUri = Uri.parse("android.resource://" + this.getPackageName() + "/raw/" + videoFileName);
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
        else {
            videoView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(nextButton)) {
            Event event = new Event(this, Config.EVENT_NEXT_STEP, null);
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
