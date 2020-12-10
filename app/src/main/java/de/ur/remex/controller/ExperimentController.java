package de.ur.remex.controller;

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
import de.ur.remex.model.experiment.breathingExercise.BreathingExercise;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.AlarmReceiver;
import de.ur.remex.utilities.Event;
import de.ur.remex.Config;
import de.ur.remex.utilities.ExperimentAlarmManager;
import de.ur.remex.admin.MainActivity;
import de.ur.remex.utilities.ExperimentNotificationManager;
import de.ur.remex.view.BreathingExerciseActivity;
import de.ur.remex.view.InstructionActivity;
import de.ur.remex.view.SurveyEntranceActivity;
import de.ur.remex.view.WaitingRoomActivity;

public class ExperimentController implements Observer {

    // Current Model
    private Experiment currentExperiment;
    private Survey currentSurvey;
    private Step currentStep;
    private boolean userIsWaiting;
    private InternalStorage storage;
    // Views
    private Context currentContext;
    private InstructionActivity instructionActivity;
    private SurveyEntranceActivity surveyEntranceActivity;
    private WaitingRoomActivity waitingRoomActivity;
    private BreathingExerciseActivity breathingExerciseActivity;
    // Utilities
    private ExperimentAlarmManager alarmManager;
    private ExperimentNotificationManager notificationManager;
    private AlarmReceiver alarmReceiver;

    public ExperimentController(Context context) {
        currentContext = context;
        alarmManager = new ExperimentAlarmManager(context);
        notificationManager = new ExperimentNotificationManager(context);
        storage = new InternalStorage();
        instructionActivity = new InstructionActivity();
        breathingExerciseActivity = new BreathingExerciseActivity();
        waitingRoomActivity = new WaitingRoomActivity();
        surveyEntranceActivity = new SurveyEntranceActivity();
        alarmReceiver = new AlarmReceiver();
        instructionActivity.addObserver(this);
        breathingExerciseActivity.addObserver(this);
        surveyEntranceActivity.addObserver(this);
        alarmReceiver.addObserver(this);
        waitingRoomActivity.addObserver(this);
        userIsWaiting = false;
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
            if (event.getData() != null) {
                // TODO: Append experiment data to csv
            }
            /* Description for the next if scope:
            Checking if the step that was finished just now is an ongoing step.
            That means that another step could wait for the completion of this step.
            f.e.:
            - Instruction ("Please take the cotton bud into your mouth and continue answering our questions.")
                -> Note: The cotton bud in this example has to be at least 2 minutes in the mouth to get good results
            - Several other Steps (Questionnaires, Breathing Exercises, Instructions, ...)
            - Instruction ("Please take the cotton bud out of your mouth and put it in the fridge")
                -> Here we have to check if the "cotton bud"-Instruction is already finished (2 minutes passed)
            */
            // If the step is ongoing we set a step timer with passing the step id
            if (currentStep.getType().equals(StepType.INSTRUCTION)) {
                Instruction instructionStep = (Instruction) currentStep;
                if (instructionStep.getDurationInMin() != 0) {
                    Log.e("ExperimentController:", "StepTimer set");
                    alarmManager.setStepTimer(instructionStep.getId(), instructionStep.getDurationInMin());
                }
            }
            // Switching to next step
            currentStep = currentStep.getNextStep();
            // There is a next step
            if (currentStep != null) {
                // Here we're checking if the next step waits for another step, like in the above "cotton-bud"-example explained
                if (currentStep.getWaitForId() != 0) {
                    Log.e("ExperimentController:", "Next step has to wait");
                    Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(currentStep.getWaitForId());
                    // The next step waits for the instruction "instructionToWaitFor".
                    // If its not finished the user gets directed to the WaitingRoomActivity
                    if (!instructionToWaitFor.isFinished()) {
                        Log.e("ExperimentController:", "Instruction to wait for is not finished yet");
                        userIsWaiting = true;
                        Intent intent = new Intent(currentContext, WaitingRoomActivity.class);
                        intent.putExtra(Config.WAITING_ROOM_TEXT_KEY, instructionToWaitFor.getWaitingText());
                        currentContext.startActivity(intent);
                    }
                    else {
                        navigateTo(currentStep);
                    }
                }
                else {
                    navigateTo(currentStep);
                }
            }
            // There is no next step and the survey gets finished
            else {
                Log.e("ExperimentController", "EVENT_SURVEY_FINISHED");
                alarmManager.cancelSurveyAlarm(currentSurvey.getId());
                updateProgress();
                Calendar c = Calendar.getInstance();
                prepareNextSurvey(c.getTimeInMillis());
                exitApp();
            }
        }
        else if (event.getType().equals(Config.EVENT_STEP_TIMER)) {
            Log.e("ExperimentController", "EVENT_STEP_TIMER");
            String stepId = event.getData();
            Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(Integer.parseInt(stepId));
            instructionToWaitFor.setFinished(true);
            if (userIsWaiting) {
                userIsWaiting = false;
                Log.e("ExperimentController:", "Instruction to wait for is finished now and user is in the waiting room");
                navigateTo(currentStep);
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
        else if (event.getType().equals(Config.EVENT_SURVEY_ALARM)) {
            Log.e("ExperimentController", "EVENT_SURVEY_ALARM");
            // TODO: Make current experiment accessible via Admin Screen with Local Storage value
            notificationManager.createNotification();
            alarmManager.setNotificationTimeoutAlarm(currentExperiment.getNotificationDurationInMin());
        }
        else if (event.getType().equals(Config.EVENT_NOTIFICATION_TIMEOUT)) {
            Log.e("ExperimentController", "EVENT_NOTIFICATION_TIMEOUT");
            // Cancel Notifications
            notificationManager.cancelNotification();
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
            BreathingExercise breathingExercise = (BreathingExercise) nextStep;
            Intent intent = new Intent(currentContext, BreathingExerciseActivity.class);
            intent.putExtra(Config.BREATHING_INSTRUCTION_HEADER_KEY, breathingExercise.getInstructionHeader());
            intent.putExtra(Config.BREATHING_INSTRUCTION_TEXT_KEY, breathingExercise.getInstructionText());
            intent.putExtra(Config.BREATHING_MODE_KEY, breathingExercise.getMode());
            intent.putExtra(Config.BREATHING_DURATION_KEY, breathingExercise.getDurationInMin());
            intent.putExtra(Config.BREATHING_FREQUENCY_KEY, breathingExercise.getBreathingFrequencyInSec());
            intent.putExtra(Config.BREATHING_DISCHARGE_HEADER_KEY, breathingExercise.getDischargeHeader());
            intent.putExtra(Config.BREATHING_DISCHARGE_TEXT_KEY, breathingExercise.getDischargeText());
            currentContext.startActivity(intent);
        }
        else if (nextStep.getType().equals(StepType.QUESTIONNAIRE)) {
            Log.e("ExperimentController", "Init Questionnaire");
            // TODO: QuestionnaireActivity
        }
    }
}
