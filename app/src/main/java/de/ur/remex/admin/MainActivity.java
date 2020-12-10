package de.ur.remex.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import de.ur.remex.R;
import de.ur.remex.controller.ExperimentController;
import de.ur.remex.model.experiment.breathingExercise.BreathingExercise;
import de.ur.remex.model.experiment.Experiment;
import de.ur.remex.model.experiment.Instruction;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.model.experiment.breathingExercise.BreathingMode;

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

        Survey survey1 = new Survey(1, "Survey1 +1 Min", 0, 3);
        Survey survey2 = new Survey(2, "Survey2 +2 Min", 60 * 1000, 3);

        // Building instruction steps
        ArrayList<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Instruction instruction = new Instruction();
            instruction.setId(i+1);
            instruction.setHeader("header1_" + i);
            instruction.setText("Super, vielen Dank. Deine aktive Teilnahme wird vermerkt. Toll gemacht! Denke daran, dass die zwei Tage der Befragung am Smartphone nun vorbei sind und dein Besuch am Lehrstuhl für Kinder- und Jugendpsychiatrie und -psychotherapie bevorsteht.");
            instruction.setImageFileName("salivette1");

            /*
            // Setting an ongoing instruction
            if (i == 0) {
                instruction.setDurationInMin(1);
                instruction.setWaitingText("Bitte warte noch wegen der ersten Instruktion.");
            }
            // Defining a step that has to wait for that instruction to finish
            if (i == 2) {
                instruction.setWaitForId(1);
            }
             */
            instructions.add(instruction);
        }
        // Building breathing exercises
        BreathingExercise breathingExercise = new BreathingExercise();
        breathingExercise.setId(6);
        breathingExercise.setMode(BreathingMode.MOVING_CIRCLE);
        breathingExercise.setInstructionHeader("Atemübung");
        breathingExercise.setInstructionText("Erklärung der Atemübung.");
        breathingExercise.setDischargeHeader("Atemübung");
        breathingExercise.setDischargeText("Verabschiedung der Atemübung.");
        breathingExercise.setDurationInMin(1);
        breathingExercise.setBreathingFrequencyInSec(5);
        // Filling surveys with steps
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
                currInstruction.setNextStep(breathingExercise);
                breathingExercise.setPreviousStep(currInstruction);
                breathingExercise.setNextStep(null);
            }
            else {
                prevInstruction = instructions.get(i - 1);
                nextInstruction = instructions.get(i + 1);
                currInstruction.setPreviousStep(prevInstruction);
                currInstruction.setNextStep(nextInstruction);
            }
            survey1.addStep(currInstruction);
            survey2.addStep(currInstruction);
            if (i == instructions.size()-1) {
                survey1.addStep(breathingExercise);
                survey2.addStep(breathingExercise);
            }
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