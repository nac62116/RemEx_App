package de.ur.remex.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import de.ur.remex.R;
import de.ur.remex.controller.ExperimentController;
import de.ur.remex.model.experiment.Experiment;
import de.ur.remex.model.experiment.Instruction;
import de.ur.remex.model.experiment.Survey;

// General TODOS
// TODO: Disable Back Buttons

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button startExperimentButton;
    private Experiment experiment;
    private ExperimentController experimentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createExperiment();
        initViews();
    }

    private void initViews() {
        startExperimentButton = findViewById(R.id.startExperimentButton);
        startExperimentButton.setOnClickListener(this);
    }

    // TODO: The experiment object will be created by the experiment editor Interface in the future
    private void createExperiment() {
        experiment = new Experiment("Test Experiment", "Experimentgroup", 15);

        Survey survey1 = new Survey(1, "Survey1 +1 Min", 60 * 1000, 3);
        Survey survey2 = new Survey(2, "Survey2 +1 Min", 60 * 1000, 3);
        survey1.setPreviousSurvey(null);
        survey1.setNextSurvey(survey2);
        survey2.setPreviousSurvey(survey1);
        survey2.setNextSurvey(null);

        for (int i = 0; i < 3; i++) {
            Instruction instruction1 = new Instruction();
            instruction1.setHeader("header1_" + i);
            instruction1.setText("text1_" + i);
            survey1.addStep(instruction1);
            Instruction instruction2 = new Instruction();
            instruction2.setHeader("header2_" + i);
            instruction2.setText("text2_" + i);
            survey2.addStep(instruction1);
        }

        experiment.addSurvey(survey1);
        experiment.addSurvey(survey2);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(startExperimentButton)) {
            if (experimentController != null) {
                experimentController.stopExperiment();
            }
            experimentController = new ExperimentController(this);
            experimentController.startExperiment(experiment);
        }
    }
}