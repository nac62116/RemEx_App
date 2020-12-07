package de.ur.remex.controller;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import de.ur.remex.model.experiment.Experiment;
import de.ur.remex.model.experiment.Instruction;
import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.AlarmReceiver;
import de.ur.remex.utilities.Event;
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
    private AlarmReceiver alarmReceiver;

    public ExperimentController(Context context) {
        currentContext = context;
        alarmManager = new ExperimentAlarmManager(context);
        storage = new InternalStorage();
        instructionActivity = new InstructionActivity();
        surveyEntranceActivity = new SurveyEntranceActivity();
        alarmReceiver = new AlarmReceiver();
        instructionActivity.addObserver(this);
        surveyEntranceActivity.addObserver(this);
        alarmReceiver.addObserver(this);
    }

    public void startExperiment(Experiment experiment) {

        // TODO: set start time in CreateVPActivity
        Calendar c = Calendar.getInstance();
        experiment.setStartTimeInMillis(c.getTimeInMillis());

        currentExperiment = experiment;
        currentSurvey = currentExperiment.getFirstSurvey();
        setSurveyAlarm(currentExperiment.getStartTimeInMillis());
    }



    public void stopExperiment() {
        if (currentSurvey != null) {
            alarmManager.cancelSurveyAlarm(currentSurvey.getId());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Event event = (Event) arg;
        if (event.getContext() != null) {
            currentContext = event.getContext();
        }
        if (event.getType().equals(Config.EVENT_SURVEY_STARTED)) {
            Log.e("ExperimentController", "EVENT_SURVEY_STARTED");
            alarmManager.cancelNotificationTimeoutAlarm();
            alarmManager.setSurveyTimeoutAlarm(currentSurvey.getId(), currentSurvey.getMaxDurationInMin());
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
                alarmManager.cancelSurveyAlarm(currentSurvey.getId());
                updateProgress();
                Calendar c = Calendar.getInstance();
                prepareNextSurvey(c.getTimeInMillis());
                exitApp();
            }
        }
        else if (event.getType().equals(Config.EVENT_SURVEY_TIMEOUT)) {
            Log.e("ExperimentController", "EVENT_SURVEY_TIMEOUT");
            Toast toast = Toast.makeText(currentContext, Config.MESSAGE_SURVEY_TIMEOUT, Toast.LENGTH_LONG);
            toast.show();
            Calendar c = Calendar.getInstance();
            prepareNextSurvey(c.getTimeInMillis());
            exitApp();
        }
        else if (event.getType().equals(Config.EVENT_NOTIFICATION_CREATED)) {
            Log.e("ExperimentController", "EVENT_NOTIFICATION_CREATED");
            alarmManager.setNotificationTimeoutAlarm(currentExperiment.getNotificationDurationInMin());
        }
        else if (event.getType().equals(Config.EVENT_NOTIFICATION_TIMEOUT)) {
            Log.e("ExperimentController", "EVENT_NOTIFICATION_TIMEOUT");
            // Cancel Notifications
            NotificationManager notificationManager = (NotificationManager) currentContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            // Prepare next survey
            Calendar c = Calendar.getInstance();
            long referenceTime = c.getTimeInMillis() - currentExperiment.getNotificationDurationInMin() * 60 * 1000;
            prepareNextSurvey(referenceTime);
        }
    }

    private void updateProgress() {
        // TODO: Update Progress in Internal Storage
    }

    private void exitApp() {
        Intent intent = new Intent(currentContext, MainActivity.class);
        intent.putExtra(Config.EXIT_APP_KEY, true);
        currentContext.startActivity(intent);
    }

    private void prepareNextSurvey(long referenceTime) {
        currentSurvey = currentSurvey.getNextSurvey();
        if (currentSurvey != null) {
            setSurveyAlarm(referenceTime);
        }
        else {
            Log.e("ExperimentController", "EVENT_EXPERIMENT_FINISHED");
        }
    }

    private void setSurveyAlarm(long referenceTime) {
        if (currentSurvey.isRelative()) {
            alarmManager.setRelativeSurveyAlarm(currentSurvey.getId(),
                    referenceTime,
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

    private void navigateTo(Step nextStep) {
        if (nextStep.getType().equals(StepType.INSTRUCTION)) {
            Log.e("ExperimentController", "Init InstructionStep");
            Instruction instruction = (Instruction) nextStep;
            Intent intent = new Intent(currentContext, InstructionActivity.class);
            intent.putExtra(Config.INSTRUCTION_HEADER_KEY, instruction.getHeader());
            intent.putExtra(Config.INSTRUCTION_TEXT_KEY, instruction.getText());
            intent.putExtra(Config.INSTRUCTION_IMAGE_KEY, instruction.getImageFileName());
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
