package de.ur.remex.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.ur.remex.R;
import de.ur.remex.controller.ExperimentController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loadExperimentButton;
    private Button startExperimentButton;
    private ExperimentController experimentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        loadExperimentButton = findViewById(R.id.loadExperimentButton);
        startExperimentButton = findViewById(R.id.startExperimentButton);
        loadExperimentButton.setOnClickListener(this);
        startExperimentButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(loadExperimentButton)) {
            experimentController = new ExperimentController(this);
        }
        else if (v.equals(startExperimentButton)) {
            if (experimentController != null) {
                experimentController.startExperiment();
            }
            else {
                Log.e("Error", "Please load experiment first");
            }
        }
    }
}