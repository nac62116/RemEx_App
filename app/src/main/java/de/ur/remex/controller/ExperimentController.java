package de.ur.remex.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

import de.ur.remex.model.Experiment;
import de.ur.remex.model.Instruction;
import de.ur.remex.model.Step;
import de.ur.remex.model.StepType;
import de.ur.remex.model.Survey;
import de.ur.remex.utilities.Config;
import de.ur.remex.view.InstructionActivity;

public class ExperimentController implements Observer {

    // Current Model
    private Experiment currentExperiment;
    private Survey currentSurvey;
    private Step currentStep;
    // Views
    private Context currentContext;
    private InstructionActivity instructionActivity;

    public ExperimentController(Context context) {
        currentContext = context;
        loadExperiment();
        instructionActivity = new InstructionActivity();
        instructionActivity.addObserver(this);
    }

    // TODO: The experiment object will be created by the experiment editor Interface in the future
    private void loadExperiment() {
        currentExperiment = new Experiment("Test Experiment", "Experimentgroup", 15);

        Survey survey1 = new Survey("Survey +5 Min", 5, 3);
        Survey survey2 = new Survey("Survey +10 Min", 10, 3);

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

        currentExperiment.addSurvey(survey1);
        currentExperiment.addSurvey(survey2);
    }

    public void startExperiment() {
        currentSurvey = currentExperiment.getSurveyByName("Survey +5 Min");
        currentStep = currentSurvey.getFirstStep();
        if (currentStep.getType() == StepType.INSTRUCTION) {
            Instruction instruction = (Instruction) currentStep;
            Intent intent = new Intent(currentContext, InstructionActivity.class);
            intent.putExtra("header", instruction.getHeader());
            intent.putExtra("text", instruction.getText());
            currentContext.startActivity(intent);
        }
        currentContext = instructionActivity.getContext();
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.e("ExperimentController", "notified");
        if (arg.equals(Config.EVENT_NEXT_STEP)) {
            Log.e("ExperimentController", "eventCaptured");
        }
    }
}
