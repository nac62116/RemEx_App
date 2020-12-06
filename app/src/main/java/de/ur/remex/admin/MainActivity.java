package de.ur.remex.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
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
        experiment = new Experiment("Test Experiment", "Experimentgroup", 1);

        Survey survey1 = new Survey(1, "Survey1 +1 Min", 60 * 1000, 3);
        Survey survey2 = new Survey(2, "Survey2 +2 Min", 2 * 60 * 1000, 3);

        ArrayList<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Instruction instruction = new Instruction();
            instruction.setHeader("header1_" + i);
            instruction.setText("text1_" + i);
            instructions.add(instruction);
        }
        for (int i=0; i < instructions.size(); i++) {
            Instruction currInstruction = instructions.get(i);
            Instruction prevInstruction;
            Instruction nextInstruction;
            if (i==0) {
                nextInstruction = instructions.get(i+1);
                currInstruction.setPreviousStep(null);
                currInstruction.setNextStep(nextInstruction);
            }
            else if (i == instructions.size()-1) {
                prevInstruction = instructions.get(i-1);
                currInstruction.setPreviousStep(prevInstruction);
                currInstruction.setNextStep(null);
            }
            else {
                prevInstruction = instructions.get(i - 1);
                nextInstruction = instructions.get(i + 1);
                currInstruction.setPreviousStep(prevInstruction);
                currInstruction.setNextStep(nextInstruction);
            }
            survey1.addStep(currInstruction);
            survey2.addStep(currInstruction);
        }

        survey1.setPreviousSurvey(null);
        survey1.setNextSurvey(survey2);
        survey2.setPreviousSurvey(survey1);
        survey2.setNextSurvey(null);

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