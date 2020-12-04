package de.ur.remex.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import de.ur.remex.model.experiment.Experiment;
import de.ur.remex.model.experiment.Instruction;
import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.utilities.ActivityEvent;
import de.ur.remex.utilities.Config;
import de.ur.remex.utilities.ExperimentAlarmManager;
import de.ur.remex.view.InstructionActivity;
import de.ur.remex.view.SurveyEntranceActivity;

public class ExperimentController implements Observer {

    // Current Model
    private Experiment currentExperiment;
    private Survey currentSurvey;
    private Step currentStep;
    // Views
    private Context currentContext;
    private InstructionActivity instructionActivity;
    private SurveyEntranceActivity surveyEntranceActivity;
    // Utilities
    private ExperimentAlarmManager alarmManager;

    public ExperimentController(Context context) {
        currentContext = context;
        alarmManager = new ExperimentAlarmManager(context);
        instructionActivity = new InstructionActivity();
        surveyEntranceActivity = new SurveyEntranceActivity();
        instructionActivity.addObserver(this);
        surveyEntranceActivity.addObserver(this);
    }

    public void startExperiment(Experiment experiment) {

        // TODO: set start time in CreateVPActivity
        Calendar c = Calendar.getInstance();
        experiment.setStartTimeInMillis(c.getTimeInMillis());

        currentExperiment = experiment;

        currentSurvey = currentExperiment.getFirstSurvey();
        if (currentSurvey.isRelative()) {
            alarmManager.setRelativeSurveyAlarm(currentSurvey.getId(),
                    currentExperiment.getStartTimeInMillis(),
                    currentSurvey.getRelativeStartTimeInMillis());
        }
        else {
            alarmManager.setAbsoluteSurveyAlarm(currentSurvey.getId(),
                    currentExperiment.getStartTimeInMillis(),
                    currentSurvey.getAbsoluteStartAtHour(),
                    currentSurvey.getAbsoluteStartAtMinute(),
                    currentSurvey.getAbsoluteStartDaysOffset());
        }
    }

    public void stopExperiment() {
        // TODO: Cancel all alarms
    }

    @Override
    public void update(Observable o, Object arg) {
        ActivityEvent event = (ActivityEvent) arg;
        currentContext = event.getContext();
        if (event.getType().equals(Config.EVENT_NEXT_STEP)) {
            Log.e("ExperimentController", "EVENT_NEXT_STEP");
        }
        else if (event.getType().equals(Config.EVENT_SURVEY_STARTED)) {
            Log.e("ExperimentController", "EVENT_SURVEY_STARTED");
            // TODO: Start survey timer
            currentStep = currentSurvey.getFirstStep();
            if (currentStep.getType().equals(StepType.INSTRUCTION)) {
                Instruction instruction = (Instruction) currentStep;
                Intent intent = new Intent(currentContext, InstructionActivity.class);
                intent.putExtra(Config.INSTRUCTION_HEADER_KEY, instruction.getHeader());
                intent.putExtra(Config.INSTRUCTION_TEXT_KEY, instruction.getText());
                currentContext.startActivity(intent);
            }
            else if (currentStep.getType().equals(StepType.BREATHING_EXERCISE)) {
                // TODO: BreathingActivity
            }
            else if (currentStep.getType().equals(StepType.QUESTIONNAIRE)) {
                // TODO: QuestionnaireActivity
            }
        }
    }
}
