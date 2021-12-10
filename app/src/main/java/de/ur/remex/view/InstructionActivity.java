package de.ur.remex.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.Observable;
import de.ur.remex.Config;

/*
MIT License

Copyright (c) 2021 Colin Nash

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

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
            InternalStorage storage = new InternalStorage(this);
            File imageFile = storage.getFile(imageFileName);
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(imageBitmap);
        }
        else {
            imageView.setVisibility(View.GONE);
        }
        if (videoFileName != null) {
            imageView.setVisibility(View.INVISIBLE);
            mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            InternalStorage storage = new InternalStorage(this);
            File videoFile = storage.getFile(videoFileName);
            if (videoFile.length() > 0) {
                videoView.setVideoPath(videoFile.getAbsolutePath());
                videoView.start();
            }
            else {
                videoView.setVisibility(View.GONE);
            }
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
