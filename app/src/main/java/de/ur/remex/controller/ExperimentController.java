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
import de.ur.remex.model.experiment.questionnaire.Question;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.AlarmReceiver;
import de.ur.remex.utilities.CsvCreator;
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
    private Question currentQuestion;
    private boolean userIsAlreadyWaiting;
    // Current View
    private Context currentContext;
    // Utilities
    private CsvCreator csvCreator;

    public ExperimentController(Context context) {
        currentContext = context;
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        InstructionActivity instructionActivity = new InstructionActivity();
        BreathingExerciseActivity breathingExerciseActivity = new BreathingExerciseActivity();
        WaitingRoomActivity waitingRoomActivity = new WaitingRoomActivity();
        SurveyEntranceActivity surveyEntranceActivity = new SurveyEntranceActivity();
        instructionActivity.addObserver(this);
        breathingExerciseActivity.addObserver(this);
        surveyEntranceActivity.addObserver(this);
        alarmReceiver.addObserver(this);
        waitingRoomActivity.addObserver(this);
        userIsAlreadyWaiting = false;
    }

    public void startExperiment(Experiment experiment) {
        // Init CSV
        InternalStorage storage = new InternalStorage(currentContext);
        String vpId = storage.getFileContent(Config.FILE_NAME_ID);
        String vpGroup = storage.getFileContent(Config.FILE_NAME_GROUP);
        csvCreator = new CsvCreator();
        csvCreator.initCsv(experiment.getSurveys(), vpId, vpGroup);

        // TODO: set start time in CreateVPActivity
        Calendar c = Calendar.getInstance();
        experiment.setStartTimeInMillis(c.getTimeInMillis());
        // Init current state
        currentExperiment = experiment;
        currentSurvey = currentExperiment.getFirstSurvey();
        setSurveyAlarm(currentExperiment.getStartTimeInMillis());
    }

    public void stopExperiment() {
        if (currentSurvey != null) {
            ExperimentAlarmManager alarmManager = new ExperimentAlarmManager(currentContext);
            alarmManager.cancelSurveyAlarm(currentSurvey.getId());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Event event = (Event) arg;
        if (event.getContext() != null) {
            currentContext = event.getContext();
        }
        // Preparing utilities
        ExperimentAlarmManager alarmManager = new ExperimentAlarmManager(currentContext);
        ExperimentNotificationManager notificationManager = new ExperimentNotificationManager(currentContext);
        InternalStorage internalStorage = new InternalStorage(currentContext);
        Calendar calendar = Calendar.getInstance();
        // Checking event type
        switch (event.getType()) {
            case Config.EVENT_SURVEY_STARTED:
                Log.e("ExperimentController", "EVENT_SURVEY_STARTED");
                alarmManager.cancelNotificationTimeoutAlarm();
                alarmManager.setSurveyTimeoutAlarm(currentSurvey.getId(), currentSurvey.getMaxDurationInMin());
                currentStep = currentSurvey.getFirstStep();
                navigateTo(currentStep);
                break;

            case Config.EVENT_NEXT_STEP:
                Log.e("ExperimentController", "EVENT_NEXT_STEP");
                /* Description for the next if scope:
                Checking if the step that was finished just now is an ongoing step.
                That means that another step waits for the completion of this step.
                f.e.:
                - Instruction ("Please take the cotton bud into your mouth and continue answering our questions.")
                    -> Note: The cotton bud in this example has to be inside the mouth for at least 2 minutes to get good results
                - Several other Steps (Questionnaires, Breathing Exercises, Instructions, ...)
                - Instruction ("Please take the cotton bud out of your mouth and put it in the fridge")
                    -> Here we have to check if the "cotton bud"-Instruction is already finished (2 minutes passed)
                */
                // If the step is ongoing we set a step timer and pass the step name to it
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
                    if (currentStep.getWaitForStep() != 0) {
                        Log.e("ExperimentController:", "Next step has to wait");
                        Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(currentStep.getWaitForStep());
                        // The next step waits for the instruction "instructionToWaitFor".
                        // If its not finished the user gets directed to the WaitingRoomActivity
                        if (!instructionToWaitFor.isFinished()) {
                            Log.e("ExperimentController:", "Instruction to wait for is not finished yet");
                            userIsAlreadyWaiting = true;
                            Intent intent = new Intent(currentContext, WaitingRoomActivity.class);
                            intent.putExtra(Config.WAITING_ROOM_TEXT_KEY, instructionToWaitFor.getWaitingText());
                            currentContext.startActivity(intent);
                        } else {
                            navigateTo(currentStep);
                        }
                    } else {
                        navigateTo(currentStep);
                    }
                }
                // There is no next step and the survey gets finished
                else {
                    Log.e("ExperimentController", "EVENT_SURVEY_FINISHED");
                    alarmManager.cancelSurveyAlarm(currentSurvey.getId());
                    updateProgress();
                    prepareNextSurvey(calendar.getTimeInMillis());
                    exitApp();
                }
                break;

            case Config.EVENT_NEXT_QUESTION:
                Log.e("ExperimentController", "EVENT_NEXT_QUESTION");
                // TODO: CsvCreator: Update hashmap with questionName (key) and event.getData() (value).
                //  Prepare next question.
                //  If null switch to next step (Make a function for above code in EVENT_NEXT_STEP)
                break;

            case Config.EVENT_STEP_TIMER:
                Log.e("ExperimentController", "EVENT_STEP_TIMER");
                String stepId = event.getData();
                Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(Integer.parseInt(stepId));
                instructionToWaitFor.setFinished(true);
                if (userIsAlreadyWaiting) {
                    userIsAlreadyWaiting = false;
                    Log.e("ExperimentController:", "Instruction to wait for is finished now and user is in the waiting room");
                    navigateTo(currentStep);
                }
                break;

            case Config.EVENT_SURVEY_TIMEOUT:
                Log.e("ExperimentController", "EVENT_SURVEY_TIMEOUT");
                Toast toast = Toast.makeText(currentContext, Config.MESSAGE_SURVEY_TIMEOUT, Toast.LENGTH_LONG);
                toast.show();
                prepareNextSurvey(calendar.getTimeInMillis());
                exitApp();
                break;

            case Config.EVENT_SURVEY_ALARM:
                Log.e("ExperimentController", "EVENT_SURVEY_ALARM");
                // TODO: Make current experiment accessible via Admin Screen with Local Storage value
                // Making survey accessible via AdminActivity (App Launcher)
                internalStorage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_OPENED);
                // Creating notification
                notificationManager.createNotification();
                // Setting timer to close the AdminActivity (App Launcher) entrance after the notification expired
                alarmManager.setNotificationTimeoutAlarm(currentExperiment.getNotificationDurationInMin());
                break;

            case Config.EVENT_NOTIFICATION_TIMEOUT:
                Log.e("ExperimentController", "EVENT_NOTIFICATION_TIMEOUT");
                // Cancel Notifications
                notificationManager.cancelNotification();
                // Closing the AdminActivity (App Launcher) entrance
                internalStorage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
                // Prepare next survey
                long referenceTime = calendar.getTimeInMillis() - currentExperiment.getNotificationDurationInMin() * 60 * 1000;
                prepareNextSurvey(referenceTime);
                break;

            default:
                break;
        }
    }

    private void updateProgress() {
        InternalStorage internalStorage = new InternalStorage(currentContext);
        String progress = internalStorage.getFileContent(Config.FILE_NAME_PROGRESS);
        int prog = Integer.parseInt(progress) + 1;
        internalStorage.saveFileContent(Config.FILE_NAME_PROGRESS, Integer.toString(prog));
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
            String csv = csvCreator.getCsv();
            InternalStorage internalStorage = new InternalStorage(currentContext);
            internalStorage.saveFileContent(Config.FILE_NAME_CSV, csv);
        }
    }

    private void setSurveyAlarm(long referenceTime) {
        ExperimentAlarmManager alarmManager = new ExperimentAlarmManager(currentContext);
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
