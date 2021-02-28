package de.ur.remex.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import de.ur.remex.admin.AdminActivity;
import de.ur.remex.admin.LoginActivity;
import de.ur.remex.model.experiment.ExperimentGroup;
import de.ur.remex.model.experiment.Instruction;
import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.model.experiment.breathingExercise.BreathingExercise;
import de.ur.remex.model.experiment.questionnaire.DaytimeQuestion;
import de.ur.remex.model.experiment.questionnaire.MultipleChoiceQuestion;
import de.ur.remex.model.experiment.questionnaire.Question;
import de.ur.remex.model.experiment.questionnaire.QuestionType;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;
import de.ur.remex.model.experiment.questionnaire.SingleChoiceQuestion;
import de.ur.remex.model.experiment.questionnaire.TextQuestion;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.AlarmReceiver;
import de.ur.remex.utilities.CsvCreator;
import de.ur.remex.utilities.Event;
import de.ur.remex.Config;
import de.ur.remex.utilities.AlarmSender;
import de.ur.remex.utilities.NotificationSender;
import de.ur.remex.view.BreathingExerciseActivity;
import de.ur.remex.view.DaytimeQuestionActivity;
import de.ur.remex.view.InstructionActivity;
import de.ur.remex.view.ChoiceQuestionActivity;
import de.ur.remex.view.SurveyEntranceActivity;
import de.ur.remex.view.TextQuestionActivity;
import de.ur.remex.view.WaitingRoomActivity;

public class ExperimentController implements Observer {

    // Current Model
    private ExperimentGroup currentExperimentGroup;
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
        AdminActivity adminActivity = new AdminActivity();
        ChoiceQuestionActivity choiceQuestionActivity = new ChoiceQuestionActivity();
        TextQuestionActivity textQuestionActivity = new TextQuestionActivity();
        DaytimeQuestionActivity daytimeQuestionActivity = new DaytimeQuestionActivity();
        instructionActivity.addObserver(this);
        breathingExerciseActivity.addObserver(this);
        surveyEntranceActivity.addObserver(this);
        alarmReceiver.addObserver(this);
        waitingRoomActivity.addObserver(this);
        adminActivity.addObserver(this);
        choiceQuestionActivity.addObserver(this);
        textQuestionActivity.addObserver(this);
        daytimeQuestionActivity.addObserver(this);
        userIsAlreadyWaiting = false;
    }

    public void startExperiment(ExperimentGroup experimentGroup, long startTimeInMs) {
        Log.e("ExperimentController", "EVENT_EXPERIMENT_STARTED");
        InternalStorage storage = new InternalStorage(currentContext);
        // Cancel possible ongoing alarms
        AlarmSender alarmManager = new AlarmSender(currentContext);
        alarmManager.cancelAllAlarms();
        // Set internal storage values
        storage.saveFileContent(Config.FILE_NAME_EXPERIMENT_STATUS, Config.EXPERIMENT_RUNNING);
        storage.saveFileContent(Config.FILE_NAME_CSV_STATUS, Config.CSV_NOT_SAVED);
        // Init CSV
        String vpId = storage.getFileContent(Config.FILE_NAME_ID);
        String vpGroup = storage.getFileContent(Config.FILE_NAME_GROUP);
        csvCreator = new CsvCreator();
        csvCreator.initCsvMap(experimentGroup.getSurveys(), vpId, vpGroup);
        storage.saveFileContent(Config.FILE_NAME_CSV, Config.INITIAL_CSV_VALUE);
        // Init current state
        currentExperimentGroup = experimentGroup;
        currentExperimentGroup.setStartTimeInMillis(startTimeInMs);
        currentSurvey = currentExperimentGroup.getFirstSurvey();
        setSurveyAlarm(startTimeInMs);
        // Inform user
        Toast toast = Toast.makeText(currentContext, Config.EXPERIMENT_STARTED_TOAST, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        Event event = (Event) arg;
        if (event.getContext() != null) {
            currentContext = event.getContext();
        }
        // Preparing utilities
        AlarmSender alarmManager = new AlarmSender(currentContext);
        NotificationSender notificationManager = new NotificationSender(currentContext);
        InternalStorage internalStorage = new InternalStorage(currentContext);
        Calendar calendar = Calendar.getInstance();
        // Checking event type
        switch (event.getType()) {

            case Config.EVENT_SURVEY_ALARM:
                Log.e("ExperimentController", "EVENT_SURVEY_ALARM");
                // Making survey accessible via AdminActivity (App Launcher)
                internalStorage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_OPENED);
                // Creating notification
                notificationManager.createNotification();
                // Setting timer to close the AdminActivity (App Launcher) entrance after the notification expired
                alarmManager.setNotificationTimeoutAlarm(currentSurvey.getNotificationDurationInMin());
                break;

            case Config.EVENT_NOTIFICATION_TIMEOUT:
                Log.e("ExperimentController", "EVENT_NOTIFICATION_TIMEOUT");
                // Cancel Notifications
                notificationManager.cancelNotification();
                // Closing the AdminActivity (App Launcher) entrance
                internalStorage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
                // Prepare next survey
                long referenceTime = calendar.getTimeInMillis() - currentSurvey.getNotificationDurationInMin() * 60 * 1000;
                prepareNextSurvey(referenceTime);
                break;

            case Config.EVENT_SURVEY_STARTED:
                Log.e("ExperimentController", "EVENT_SURVEY_STARTED");
                alarmManager.cancelNotificationTimeoutAlarm();
                alarmManager.setSurveyTimeoutAlarm(currentSurvey.getId(), currentSurvey.getMaxDurationInMin());
                currentStep = currentSurvey.getFirstStep();
                navigateToStep(currentStep);
                break;

            case Config.EVENT_NEXT_STEP:
                Log.e("ExperimentController", "EVENT_NEXT_STEP");
                switchToNextStep(alarmManager, calendar);
                break;

            case Config.EVENT_NEXT_QUESTION:
                Log.e("ExperimentController", "EVENT_NEXT_QUESTION");
                // TODO: Outsource in method
                if (currentQuestion.getType().equals(QuestionType.SINGLE_CHOICE)) {
                    SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion) currentQuestion;
                    String answerText = (String) event.getData();
                    String answerCode = singleChoiceQuestion.getCodeByAnswerText(answerText);
                    csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                            answerCode, calendar.getTime().toString());
                    currentQuestion = singleChoiceQuestion.getNextQuestionByAnswerText(answerText);
                }
                else if (currentQuestion.getType().equals(QuestionType.MULTIPLE_CHOICE)) {
                    MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) currentQuestion;
                    @SuppressWarnings("unchecked")
                    ArrayList<String> answerTexts = (ArrayList<String>) event.getData();
                    StringBuilder answerCode = new StringBuilder();
                    for (String answerText: answerTexts) {
                        answerCode.append(multipleChoiceQuestion.getCodeByAnswerText(answerText));
                    }
                    csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                            answerCode.toString(), calendar.getTime().toString());
                    currentQuestion = multipleChoiceQuestion.getNextQuestion();
                }
                else if (currentQuestion.getType().equals(QuestionType.TEXT)) {
                    TextQuestion textQuestion = (TextQuestion) currentQuestion;
                    String answerText = (String) event.getData();
                    csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                            answerText, calendar.getTime().toString());
                    currentQuestion = textQuestion.getNextQuestion();
                }
                else if (currentQuestion.getType().equals(QuestionType.DAYTIME)) {
                    DaytimeQuestion daytimeQuestion = (DaytimeQuestion) currentQuestion;
                    String answerText = (String) event.getData();
                    csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                            answerText, calendar.getTime().toString());
                    currentQuestion = daytimeQuestion.getNextQuestion();
                }
                if (currentQuestion != null) {
                    navigateToQuestion(currentQuestion);
                }
                else {
                    switchToNextStep(alarmManager, calendar);
                }
                break;

            case Config.EVENT_STEP_TIMER:
                Log.e("ExperimentController", "EVENT_STEP_TIMER");
                int stepId = (int) event.getData();
                Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(stepId);
                instructionToWaitFor.setFinished(true);
                if (userIsAlreadyWaiting) {
                    userIsAlreadyWaiting = false;
                    Log.e("ExperimentController:", "Instruction to wait for is finished now and user is in the waiting room");
                    navigateToStep(currentStep);
                }
                break;

            case Config.EVENT_SURVEY_TIMEOUT:
                Log.e("ExperimentController", "EVENT_SURVEY_TIMEOUT");
                Toast toast = Toast.makeText(currentContext, Config.MESSAGE_SURVEY_TIMEOUT, Toast.LENGTH_LONG);
                toast.show();
                prepareNextSurvey(calendar.getTimeInMillis());
                exitApp();
                break;

            case Config.EVENT_CSV_REQUEST:
                Log.e("ExperimentController", "EVENT_CSV_REQUEST");
                saveCsvInternalStorage();

            default:
                break;
        }
    }



    private void prepareNextSurvey(long referenceTime) {
        currentSurvey = currentSurvey.getNextSurvey();
        if (currentSurvey != null) {
            setSurveyAlarm(referenceTime);
        }
        else {
            finishExperiment();
        }
    }
    private void setSurveyAlarm(long referenceTime) {
        AlarmSender alarmManager = new AlarmSender(currentContext);
        if (currentSurvey.isRelative()) {
            alarmManager.setRelativeSurveyAlarm(currentSurvey.getId(),
                    referenceTime,
                    currentSurvey.getRelativeStartTimeInMillis());
        }
        else {
            alarmManager.setAbsoluteSurveyAlarm(currentSurvey.getId(),
                    currentExperimentGroup.getStartTimeInMillis(),
                    currentSurvey.getAbsoluteStartAtHour(),
                    currentSurvey.getAbsoluteStartAtMinute(),
                    currentSurvey.getAbsoluteStartDaysOffset());
        }
    }
    private void finishExperiment() {
        Log.e("ExperimentController", "EVENT_EXPERIMENT_FINISHED");
        // Set running value
        InternalStorage storage = new InternalStorage(currentContext);
        storage.saveFileContent(Config.FILE_NAME_EXPERIMENT_STATUS, Config.EXPERIMENT_FINISHED);

    }

    private void navigateToStep(Step nextStep) {
        if (nextStep.getType().equals(StepType.INSTRUCTION)) {
            Log.e("ExperimentController", "Init InstructionStep");
            Instruction instruction = (Instruction) nextStep;
            Intent intent = new Intent(currentContext, InstructionActivity.class);
            intent.putExtra(Config.INSTRUCTION_HEADER_KEY, instruction.getHeader());
            intent.putExtra(Config.INSTRUCTION_TEXT_KEY, instruction.getText());
            intent.putExtra(Config.INSTRUCTION_IMAGE_KEY, instruction.getImageFileName());
            intent.putExtra(Config.INSTRUCTION_VIDEO_KEY, instruction.getVideoFileName());
            currentContext.startActivity(intent);
        }
        else if (nextStep.getType().equals(StepType.BREATHING_EXERCISE)) {
            Log.e("ExperimentController", "Init BreathingExercise");
            BreathingExercise breathingExercise = (BreathingExercise) nextStep;
            Intent intent = new Intent(currentContext, BreathingExerciseActivity.class);
            intent.putExtra(Config.BREATHING_MODE_KEY, breathingExercise.getMode());
            intent.putExtra(Config.BREATHING_DURATION_KEY, breathingExercise.getDurationInMin());
            intent.putExtra(Config.BREATHING_FREQUENCY_KEY, breathingExercise.getBreathingFrequencyInSec());
            currentContext.startActivity(intent);
        }
        else if (nextStep.getType().equals(StepType.QUESTIONNAIRE)) {
            Log.e("ExperimentController", "Init Questionnaire");
            Questionnaire questionnaire = (Questionnaire) nextStep;
            currentQuestion = questionnaire.getFirstQuestion();
            navigateToQuestion(currentQuestion);
        }
    }

    private void navigateToQuestion(Question nextQuestion) {
        Intent intent = new Intent();
        if (nextQuestion.getType().equals(QuestionType.SINGLE_CHOICE)) {
            SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion) currentQuestion;
            intent = new Intent(currentContext, ChoiceQuestionActivity.class);
            intent.putExtra(Config.ANSWER_TEXTS_KEY, singleChoiceQuestion.getAnswerTexts());
            intent.putExtra(Config.QUESTION_TYPE_KEY, singleChoiceQuestion.getType());
        }
        else if (nextQuestion.getType().equals(QuestionType.MULTIPLE_CHOICE)) {
            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) currentQuestion;
            intent = new Intent(currentContext, ChoiceQuestionActivity.class);
            intent.putExtra(Config.ANSWER_TEXTS_KEY, multipleChoiceQuestion.getAnswerTexts());
            intent.putExtra(Config.QUESTION_TYPE_KEY, multipleChoiceQuestion.getType());
        }
        else if (nextQuestion.getType().equals(QuestionType.TEXT)) {
            intent = new Intent(currentContext, TextQuestionActivity.class);
        }
        else if (nextQuestion.getType().equals(QuestionType.DAYTIME)) {
            intent = new Intent(currentContext, DaytimeQuestionActivity.class);
        }
        intent.putExtra(Config.QUESTION_TEXT_KEY, currentQuestion.getText());
        intent.putExtra(Config.QUESTION_HINT_KEY, currentQuestion.getHint());
        currentContext.startActivity(intent);
    }

    private void switchToNextStep(AlarmSender alarmManager, Calendar calendar) {
        // Set step timer if the current step was an ongoing step
        setStepTimer(alarmManager);
        // Switching to next step
        currentStep = currentStep.getNextStep();
        // There is a next step
        if (currentStep != null) {
            // Moving on to next step after a little checkup
            checkWaitingRequestAndNavigateToNextStep();
        }
        // There is no next step and the survey gets finished
        else {
            Log.e("ExperimentController", "EVENT_SURVEY_FINISHED");
            alarmManager.cancelAllAlarms();
            updateProgress();
            prepareNextSurvey(calendar.getTimeInMillis());
            exitApp();
        }
    }
    private void setStepTimer(AlarmSender alarmManager) {
        /* Description:
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
    }
    private void checkWaitingRequestAndNavigateToNextStep() {
        // Here we're checking if the next step waits for another step,
        // like in the "cotton-bud"-example in "setStepTimer()" explained.
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
            }
            else {
                navigateToStep(currentStep);
            }
        }
        else {
            navigateToStep(currentStep);
        }
    }

    private void updateProgress() {
        InternalStorage internalStorage = new InternalStorage(currentContext);
        String progress = internalStorage.getFileContent(Config.FILE_NAME_PROGRESS);
        int prog = Integer.parseInt(progress) + 1;
        internalStorage.saveFileContent(Config.FILE_NAME_PROGRESS, Integer.toString(prog));
    }

    private void exitApp() {
        InternalStorage storage = new InternalStorage(currentContext);
        storage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
        Intent intent = new Intent(currentContext, LoginActivity.class);
        intent.putExtra(Config.EXIT_APP_KEY, true);
        currentContext.startActivity(intent);
    }

    private void saveCsvInternalStorage() {
        String csv = csvCreator.getCsvString();
        InternalStorage internalStorage = new InternalStorage(currentContext);
        internalStorage.saveFileContent(Config.FILE_NAME_CSV, csv);
    }
}
