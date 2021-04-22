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
import de.ur.remex.model.experiment.questionnaire.ChoiceType;
import de.ur.remex.model.experiment.questionnaire.LikertQuestion;
import de.ur.remex.model.experiment.questionnaire.PointOfTimeQuestion;
import de.ur.remex.model.experiment.questionnaire.Question;
import de.ur.remex.model.experiment.questionnaire.QuestionType;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;
import de.ur.remex.model.experiment.questionnaire.ChoiceQuestion;
import de.ur.remex.model.experiment.questionnaire.TextQuestion;
import de.ur.remex.model.experiment.questionnaire.TimeIntervallQuestion;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.AlarmReceiver;
import de.ur.remex.utilities.AppKillCallbackService;
import de.ur.remex.utilities.CsvCreator;
import de.ur.remex.utilities.Event;
import de.ur.remex.Config;
import de.ur.remex.utilities.AlarmSender;
import de.ur.remex.utilities.NotificationSender;
import de.ur.remex.view.BreathingExerciseActivity;
import de.ur.remex.view.LikertQuestionActivity;
import de.ur.remex.view.PointOfTimeQuestionActivity;
import de.ur.remex.view.InstructionActivity;
import de.ur.remex.view.ChoiceQuestionActivity;
import de.ur.remex.view.SurveyEntranceActivity;
import de.ur.remex.view.TextQuestionActivity;
import de.ur.remex.view.TimeIntervallQuestionActivity;
import de.ur.remex.view.WaitingRoomActivity;

public class ExperimentController implements Observer {

    // Current Model
    private ExperimentGroup currentExperimentGroup;
    // Current State
    private int currentSurveyId;
    private int currentStepId;
    private int currentQuestionId;
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
        PointOfTimeQuestionActivity pointOfTimeQuestionActivity = new PointOfTimeQuestionActivity();
        TimeIntervallQuestionActivity timeIntervallQuestionActivity = new TimeIntervallQuestionActivity();
        LikertQuestionActivity likertQuestionActivity = new LikertQuestionActivity();
        AppKillCallbackService service = new AppKillCallbackService();
        instructionActivity.addObserver(this);
        breathingExerciseActivity.addObserver(this);
        surveyEntranceActivity.addObserver(this);
        alarmReceiver.addObserver(this);
        waitingRoomActivity.addObserver(this);
        adminActivity.addObserver(this);
        choiceQuestionActivity.addObserver(this);
        textQuestionActivity.addObserver(this);
        pointOfTimeQuestionActivity.addObserver(this);
        timeIntervallQuestionActivity.addObserver(this);
        likertQuestionActivity.addObserver(this);
        service.addObserver(this);
        userIsAlreadyWaiting = false;
    }

    public void startExperiment(ExperimentGroup experimentGroup, long startTimeInMs) {
        Log.e("ExperimentController", "EVENT_EXPERIMENT_STARTED");
        InternalStorage internalStorage = new InternalStorage(currentContext);
        // Cancel possible ongoing alarms
        AlarmSender alarmSender = new AlarmSender(currentContext);
        alarmSender.cancelAllAlarms();
        // Set internal storage values
        internalStorage.saveFileContent(Config.FILE_NAME_EXPERIMENT_STATUS, Config.EXPERIMENT_RUNNING);
        internalStorage.saveFileContent(Config.FILE_NAME_CSV_STATUS, Config.CSV_NOT_SAVED);
        // Init CSV
        String vpId = internalStorage.getFileContent(Config.FILE_NAME_ID);
        String vpGroup = internalStorage.getFileContent(Config.FILE_NAME_GROUP);
        csvCreator = new CsvCreator();
        csvCreator.initCsvMap(experimentGroup.getSurveys(), vpId, vpGroup);
        internalStorage.saveFileContent(Config.FILE_NAME_CSV, Config.INITIAL_CSV_VALUE);
        // Init current state
        currentExperimentGroup = experimentGroup;
        currentExperimentGroup.setStartTimeInMillis(startTimeInMs);
        Survey currentSurvey = currentExperimentGroup.getFirstSurvey();
        currentSurveyId = currentSurvey.getId();
        setSurveyAlarm(currentSurvey, alarmSender, startTimeInMs);
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
        AlarmSender alarmSender = new AlarmSender(currentContext);
        NotificationSender notificationSender = new NotificationSender(currentContext);
        InternalStorage internalStorage = new InternalStorage(currentContext);
        Calendar calendar = Calendar.getInstance();
        // Preparing current state
        Survey currentSurvey = getCurrentSurvey();
        Step currentStep;
        // Checking event type
        switch (event.getType()) {

            case Config.EVENT_SURVEY_ALARM:
                Log.e("ExperimentController", "EVENT_SURVEY_ALARM");
                // Making survey accessible via AdminActivity (App Launcher)
                internalStorage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_OPENED);
                // Creating notification
                notificationSender.sendNotification();
                // Setting timer to close the AdminActivity (App Launcher) entrance after the notification expired
                alarmSender.setNotificationTimeoutAlarm(currentSurvey.getNotificationDurationInMin());
                break;

            case Config.EVENT_NOTIFICATION_TIMEOUT:
                Log.e("ExperimentController", "EVENT_NOTIFICATION_TIMEOUT");
                // Cancel Notifications
                notificationSender.cancelNotification();
                // Closing the AdminActivity (App Launcher) entrance
                internalStorage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
                // Prepare next survey
                long referenceTime = calendar.getTimeInMillis() - currentSurvey.getNotificationDurationInMin() * 60 * 1000;
                prepareNextSurvey(currentSurvey, internalStorage, alarmSender, referenceTime);
                break;

            case Config.EVENT_SURVEY_STARTED:
                Log.e("ExperimentController", "EVENT_SURVEY_STARTED");
                // Start service to receive a callback, when the user kills the app by swiping it in the recent apps list.
                currentContext.startService(new Intent(currentContext, AppKillCallbackService.class));
                alarmSender.cancelNotificationTimeoutAlarm();
                alarmSender.setSurveyTimeoutAlarm(currentSurvey.getId(), currentSurvey.getMaxDurationInMin());
                currentStep = currentSurvey.getFirstStep();
                currentStepId = currentStep.getId();
                navigateToStep(currentStep);
                break;

            case Config.EVENT_NEXT_STEP:
                Log.e("ExperimentController", "EVENT_NEXT_STEP");
                currentStep = getCurrentStep();
                switchToNextStep(currentSurvey, currentStep, internalStorage, alarmSender, calendar);
                break;

            case Config.EVENT_NEXT_QUESTION:
                Log.e("ExperimentController", "EVENT_NEXT_QUESTION");
                currentStep = getCurrentStep();
                Question nextQuestion = switchToNextQuestion(currentSurvey, currentStep, event, calendar);
                if (nextQuestion != null) {
                    currentQuestionId = nextQuestion.getId();
                    navigateToQuestion(nextQuestion);
                }
                else {
                    switchToNextStep(currentSurvey, currentStep, internalStorage, alarmSender, calendar);
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
                    currentStep = getCurrentStep();
                    navigateToStep(currentStep);
                }
                break;

            case Config.EVENT_SURVEY_TIMEOUT:
                Log.e("ExperimentController", "EVENT_SURVEY_TIMEOUT");
                Toast toast = Toast.makeText(currentContext, Config.MESSAGE_SURVEY_TIMEOUT, Toast.LENGTH_LONG);
                toast.show();
                exitApp(currentSurvey, internalStorage, alarmSender, calendar);
                break;

            case Config.EVENT_CSV_REQUEST:
                Log.e("ExperimentController", "EVENT_CSV_REQUEST");
                saveCsvInternalStorage(internalStorage);

            case Config.EVENT_APP_KILLED:
                Log.e("ExperimentController", "EVENT_APP_KILLED");
                exitApp(currentSurvey, internalStorage, alarmSender, calendar);

            default:
                break;
        }
    }

    private Question switchToNextQuestion(Survey currentSurvey, Step currentStep,
                                          Event event, Calendar calendar) {
        Questionnaire currentQuestionnaire = (Questionnaire) currentStep;
        Question currentQuestion = currentQuestionnaire.getQuestionById(currentQuestionId);
        Question nextQuestion = null;
        if (currentQuestion.getType().equals(QuestionType.CHOICE)) {
            ChoiceQuestion choiceQuestion = (ChoiceQuestion) currentQuestion;
            if (choiceQuestion.getChoiceType().equals(ChoiceType.SINGLE_CHOICE)) {
                String answerText = (String) event.getData();
                String answerCode = choiceQuestion.getCodeByAnswerText(answerText);
                csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                        answerCode, calendar.getTime().toString());
                nextQuestion = currentQuestionnaire.getQuestionById(choiceQuestion.getNextQuestionIdByAnswerText(answerText));
            }
            else {
                @SuppressWarnings("unchecked")
                ArrayList<String> answerTexts = (ArrayList<String>) event.getData();
                StringBuilder answerCode = new StringBuilder();
                for (String answerText: answerTexts) {
                    answerCode.append(choiceQuestion.getCodeByAnswerText(answerText));
                }
                csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                        answerCode.toString(), calendar.getTime().toString());
                nextQuestion = currentQuestionnaire.getQuestionById(choiceQuestion.getNextQuestionId());
            }
        }
        else if (currentQuestion.getType().equals(QuestionType.TEXT)) {
            TextQuestion textQuestion = (TextQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(textQuestion.getNextQuestionId());
        }
        else if (currentQuestion.getType().equals(QuestionType.POINT_OF_TIME)) {
            PointOfTimeQuestion pointOfTimeQuestion = (PointOfTimeQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(pointOfTimeQuestion.getNextQuestionId());
        }
        else if (currentQuestion.getType().equals(QuestionType.TIME_INTERVALL)) {
            TimeIntervallQuestion timeIntervallQuestion = (TimeIntervallQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(timeIntervallQuestion.getNextQuestionId());
        }
        else if (currentQuestion.getType().equals(QuestionType.LIKERT)) {
            LikertQuestion likertQuestion = (LikertQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(likertQuestion.getNextQuestionId());
        }
        return nextQuestion;
    }

    private Step getCurrentStep() {
        return currentExperimentGroup.getSurveyById(currentSurveyId).getStepById(currentStepId);
    }

    private Survey getCurrentSurvey() {
        return currentExperimentGroup.getSurveyById(currentSurveyId);
    }

    private void prepareNextSurvey(Survey currentSurvey, InternalStorage internalStorage,
                                   AlarmSender alarmSender, long referenceTime) {
        Survey nextSurvey = currentExperimentGroup.getSurveyById(currentSurvey.getNextSurveyId());
        if (nextSurvey != null) {
            currentSurveyId = nextSurvey.getId();
            setSurveyAlarm(nextSurvey, alarmSender, referenceTime);
        }
        else {
            finishExperiment(internalStorage);
        }
    }
    private void setSurveyAlarm(Survey survey, AlarmSender alarmSender, long referenceTime) {
        if (survey.isRelative()) {
            long relativeStartTimeInMillis = survey.getRelativeStartTimeInMin() * 60 * 1000;
            alarmSender.setRelativeSurveyAlarm(survey.getId(),
                    referenceTime,
                    relativeStartTimeInMillis);
        }
        else {
            alarmSender.setAbsoluteSurveyAlarm(survey.getId(),
                    currentExperimentGroup.getStartTimeInMillis(),
                    survey.getAbsoluteStartAtHour(),
                    survey.getAbsoluteStartAtMinute(),
                    survey.getAbsoluteStartDaysOffset());
        }
    }
    private void finishExperiment(InternalStorage internalStorage) {
        Log.e("ExperimentController", "EVENT_EXPERIMENT_FINISHED");
        // Set running value
        internalStorage.saveFileContent(Config.FILE_NAME_EXPERIMENT_STATUS, Config.EXPERIMENT_FINISHED);
    }

    private void navigateToStep(Step step) {
        if (step.getType().equals(StepType.INSTRUCTION)) {
            Log.e("ExperimentController", "Init InstructionStep");
            Instruction instruction = (Instruction) step;
            Intent intent = new Intent(currentContext, InstructionActivity.class);
            intent.putExtra(Config.INSTRUCTION_HEADER_KEY, instruction.getHeader());
            intent.putExtra(Config.INSTRUCTION_TEXT_KEY, instruction.getText());
            intent.putExtra(Config.INSTRUCTION_IMAGE_KEY, instruction.getImageFileName());
            intent.putExtra(Config.INSTRUCTION_VIDEO_KEY, instruction.getVideoFileName());
            currentContext.startActivity(intent);
        }
        else if (step.getType().equals(StepType.BREATHING_EXERCISE)) {
            Log.e("ExperimentController", "Init BreathingExercise");
            BreathingExercise breathingExercise = (BreathingExercise) step;
            Intent intent = new Intent(currentContext, BreathingExerciseActivity.class);
            intent.putExtra(Config.BREATHING_MODE_KEY, breathingExercise.getMode());
            intent.putExtra(Config.BREATHING_DURATION_KEY, breathingExercise.getDurationInMin());
            intent.putExtra(Config.BREATHING_FREQUENCY_KEY, breathingExercise.getBreathingFrequencyInSec());
            currentContext.startActivity(intent);
        }
        else if (step.getType().equals(StepType.QUESTIONNAIRE)) {
            Log.e("ExperimentController", "Init Questionnaire");
            Questionnaire questionnaire = (Questionnaire) step;
            Question currentQuestion = questionnaire.getFirstQuestion();
            currentQuestionId = currentQuestion.getId();
            navigateToQuestion(currentQuestion);
        }
    }

    private void navigateToQuestion(Question question) {
        Intent intent = new Intent();
        if (question.getType().equals(QuestionType.CHOICE)) {
            ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
            intent = new Intent(currentContext, ChoiceQuestionActivity.class);
            intent.putExtra(Config.ANSWER_TEXTS_KEY, choiceQuestion.getAnswerTexts());
            intent.putExtra(Config.CHOICE_TYPE_KEY, choiceQuestion.getChoiceType());
        }
        else if (question.getType().equals(QuestionType.TEXT)) {
            intent = new Intent(currentContext, TextQuestionActivity.class);
        }
        else if (question.getType().equals(QuestionType.POINT_OF_TIME)) {
            PointOfTimeQuestion pointOfTimeQuestion = (PointOfTimeQuestion) question;
            intent = new Intent(currentContext, PointOfTimeQuestionActivity.class);
            intent.putExtra(Config.POINT_OF_TIME_TYPES_KEY, pointOfTimeQuestion.getPointOfTimeTypeNames());
        }
        else if (question.getType().equals(QuestionType.TIME_INTERVALL)) {
            TimeIntervallQuestion timeIntervallQuestion = (TimeIntervallQuestion) question;
            intent = new Intent(currentContext, TimeIntervallQuestionActivity.class);
            intent.putExtra(Config.TIME_INTERVALL_TYPES_KEY, timeIntervallQuestion.getTimeIntervallTypeNames());
        }
        else if (question.getType().equals(QuestionType.LIKERT)) {
            LikertQuestion likertQuestion = (LikertQuestion) question;
            intent = new Intent(currentContext, LikertQuestionActivity.class);
            intent.putExtra(Config.SCALE_LABEL_MIN_KEY, likertQuestion.getScaleMinimumLabel());
            intent.putExtra(Config.SCALE_LABEL_MAX_KEY, likertQuestion.getScaleMaximumLabel());
            intent.putExtra(Config.INITIAL_SCALE_VALUE_KEY, likertQuestion.getInitialValue());
            intent.putExtra(Config.SCALE_ITEM_COUNT_KEY, likertQuestion.getItemCount());
        }
        intent.putExtra(Config.QUESTION_TEXT_KEY, question.getText());
        intent.putExtra(Config.QUESTION_HINT_KEY, question.getHint());
        currentContext.startActivity(intent);
    }

    private void switchToNextStep(Survey currentSurvey, Step currentStep, InternalStorage internalStorage,
                                  AlarmSender alarmSender, Calendar calendar) {
        // Set step timer if the current step was an ongoing step
        setStepTimer(currentStep, alarmSender);
        // Switching to next step
        Step nextStep = currentSurvey.getStepById(currentStep.getNextStepId());
        // There is a next step
        if (nextStep != null) {
            currentStepId = nextStep.getId();
            // Moving on to next step after a little checkup
            checkWaitingRequestAndNavigateToStep(currentSurvey, nextStep);
        }
        // There is no next step and the survey gets finished
        else {
            Log.e("ExperimentController", "EVENT_SURVEY_FINISHED");
            alarmSender.cancelAllAlarms();
            updateProgress(internalStorage);
            exitApp(currentSurvey, internalStorage, alarmSender, calendar);
        }
    }
    private void setStepTimer(Step step, AlarmSender alarmSender) {
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
        if (step.getType().equals(StepType.INSTRUCTION)) {
            Instruction instructionStep = (Instruction) step;
            if (instructionStep.getDurationInMin() != 0) {
                Log.e("ExperimentController:", "StepTimer set");
                alarmSender.setStepTimer(instructionStep.getId(), instructionStep.getDurationInMin());
            }
        }
    }
    private void checkWaitingRequestAndNavigateToStep(Survey currentSurvey, Step step) {
        // Here we're checking if the next step waits for another step,
        // like in the "cotton-bud"-example in "setStepTimer()" explained.
        if (step.getWaitForStep() != 0) {
            Log.e("ExperimentController:", "Next step has to wait");
            Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(step.getWaitForStep());
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
                navigateToStep(step);
            }
        }
        else {
            navigateToStep(step);
        }
    }

    private void updateProgress(InternalStorage internalStorage) {
        String progress = internalStorage.getFileContent(Config.FILE_NAME_PROGRESS);
        int prog = Integer.parseInt(progress) + 1;
        internalStorage.saveFileContent(Config.FILE_NAME_PROGRESS, Integer.toString(prog));
    }

    private void exitApp(Survey currentSurvey, InternalStorage internalStorage, AlarmSender alarmSender, Calendar calendar) {
        prepareNextSurvey(currentSurvey, internalStorage, alarmSender, calendar.getTimeInMillis());
        internalStorage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
        currentContext.stopService(new Intent(currentContext, AppKillCallbackService.class));
        Intent intent = new Intent(currentContext, LoginActivity.class);
        intent.putExtra(Config.EXIT_APP_KEY, true);
        currentContext.startActivity(intent);
    }

    private void saveCsvInternalStorage(InternalStorage internalStorage) {
        String csv = csvCreator.getCsvString();
        internalStorage.saveFileContent(Config.FILE_NAME_CSV, csv);
    }
}
