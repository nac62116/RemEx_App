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
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.ActivityEvent;
import de.ur.remex.Config;
import de.ur.remex.utilities.ExperimentAlarmManager;
import de.ur.remex.admin.MainActivity;
import de.ur.remex.view.InstructionActivity;
import de.ur.remex.view.SurveyEntranceActivity;

public class ExperimentController implements Observer {

    // Current Model
    private Experiment currentExperiment;
    private Survey currentSurvey;
    private Step currentStep;
    private InternalStorage storage;
    // Views
    private Context currentContext;
    private InstructionActivity instructionActivity;
    private SurveyEntranceActivity surveyEntranceActivity;
    // Utilities
    private ExperimentAlarmManager alarmManager;

    public ExperimentController(Context context) {
        currentContext = context;
        alarmManager = new ExperimentAlarmManager(context);
        storage = new InternalStorage();
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
        setSurveyAlarm(currentSurvey, true);
    }

    private void setSurveyAlarm(Survey survey, boolean isFirstSurvey) {
        if (survey.isRelative()) {
            long referenceTime;
            if (isFirstSurvey) {
                referenceTime = currentExperiment.getStartTimeInMillis();
            }
            else {
                Calendar c = Calendar.getInstance();
                referenceTime = c.getTimeInMillis();
            }
            alarmManager.setRelativeSurveyAlarm(survey.getId(),
                    referenceTime,
                    survey.getRelativeStartTimeInMillis());
        }
        else {
            alarmManager.setAbsoluteSurveyAlarm(survey.getId(),
                    currentExperiment.getStartTimeInMillis(),
                    survey.getAbsoluteStartAtHour(),
                    survey.getAbsoluteStartAtMinute(),
                    survey.getAbsoluteStartDaysOffset());
        }
    }

    public void stopExperiment() {
        alarmManager.cancelSurveyAlarm(currentSurvey.getId());
    }

    @Override
    public void update(Observable o, Object arg) {
        ActivityEvent event = (ActivityEvent) arg;
        currentContext = event.getContext();
        if (event.getType().equals(Config.EVENT_SURVEY_STARTED)) {
            Log.e("ExperimentController", "EVENT_SURVEY_STARTED");
            // TODO: Start survey timer (cancel in prepareNextSurvey())
            currentStep = currentSurvey.getFirstStep();
            navigateTo(currentStep);
        }
        else if (event.getType().equals(Config.EVENT_NEXT_STEP)) {
            Log.e("ExperimentController", "EVENT_NEXT_STEP");
            if (event.getExperimentData() != null) {
                // TODO: Append experiment data to csv
            }

            currentStep = currentStep.getNextStep();
            if (currentStep != null) {
                navigateTo(currentStep);
            }
            else {
                Log.e("ExperimentController", "EVENT_SURVEY_FINISHED");
                updateProgress();
                prepareNextSurvey();
                exitApp(currentContext);
            }
        }
    }

    private void updateProgress() {
        // TODO: Update Progress in Internal Storage
    }

    private void exitApp(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Config.EXIT_APP_KEY, true);
        context.startActivity(intent);
    }

    private void prepareNextSurvey() {
        // TODO: Cancel survey timer (start in update())
        currentSurvey = currentSurvey.getNextSurvey();
        if (currentSurvey != null) {
            setSurveyAlarm(currentSurvey, false);
        }
    }

    private void navigateTo(Step nextStep) {
        if (nextStep.getType().equals(StepType.INSTRUCTION)) {
            Log.e("ExperimentController", "Init InstructionStep");
            Instruction instruction = (Instruction) nextStep;
            Intent intent = new Intent(currentContext, InstructionActivity.class);
            intent.putExtra(Config.INSTRUCTION_HEADER_KEY, instruction.getHeader());
            intent.putExtra(Config.INSTRUCTION_TEXT_KEY, instruction.getText());
            currentContext.startActivity(intent);
        }
        else if (nextStep.getType().equals(StepType.BREATHING_EXERCISE)) {
            Log.e("ExperimentController", "Init BreathingExercise");
            // TODO: BreathingActivity
        }
        else if (nextStep.getType().equals(StepType.QUESTIONNAIRE)) {
            Log.e("ExperimentController", "Init Questionnaire");
            // TODO: QuestionnaireActivity
        }
    }
}
