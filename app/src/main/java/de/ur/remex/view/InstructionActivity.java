package de.ur.remex.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Observer;

import de.ur.remex.R;
import de.ur.remex.model.storage.InternalStorage;
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

    private String getRawResource(String fileName) {
        InternalStorage storage = new InternalStorage(this);
        return storage.getFileContent(fileName);
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
            String base64Image = getRawResource(imageFileName);
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
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
            String base64Video = getRawResource(videoFileName);
            byte[] videoBytes = Base64.decode(base64Video, Base64.DEFAULT);
            // Save decoded video as a file to feed the reulting path to videoView
            File videoFile = new File(this.getFilesDir() + Config.FILE_NAME_DECODED_VIDEO);
            boolean success;
            try {
                if (!videoFile.exists()) {
                    success = videoFile.createNewFile();
                }
                else {
                    success = videoFile.delete();
                    if (success) {
                        success = videoFile.createNewFile();
                    }
                }
                if (success) {
                    FileOutputStream fos = new FileOutputStream(videoFile);
                    fos.write(videoBytes);
                    fos.close();
                    success = true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            if (success) {
                videoView.setVideoPath(videoFile.getAbsolutePath());
                videoView.start();
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
