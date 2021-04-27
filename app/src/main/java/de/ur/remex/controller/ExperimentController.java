package de.ur.remex.controller;

import android.content.Context;
import android.content.Intent;
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
import de.ur.remex.model.experiment.questionnaire.TimeIntervalQuestion;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.AlarmReceiver;
import de.ur.remex.utilities.AppKillCallbackService;
import de.ur.remex.utilities.CsvCreator;
import de.ur.remex.utilities.Event;
import de.ur.remex.Config;
import de.ur.remex.utilities.AlarmScheduler;
import de.ur.remex.utilities.NotificationHandler;
import de.ur.remex.view.BreathingExerciseActivity;
import de.ur.remex.view.LikertQuestionActivity;
import de.ur.remex.view.PointOfTimeQuestionActivity;
import de.ur.remex.view.InstructionActivity;
import de.ur.remex.view.ChoiceQuestionActivity;
import de.ur.remex.view.SurveyEntranceActivity;
import de.ur.remex.view.TextQuestionActivity;
import de.ur.remex.view.TimeIntervalQuestionActivity;
import de.ur.remex.view.WaitingRoomActivity;

public class ExperimentController implements Observer {

    // Current model
    private ExperimentGroup currentExperimentGroup;
    // Current state
    private int currentSurveyId;
    private int currentStepId;
    private int currentQuestionId;
    private boolean userIsWaiting;
    // Current activity context (View)
    private Context currentContext;
    // Utilities
    private CsvCreator csvCreator;

    public ExperimentController(Context context) {
        // Initial state
        currentContext = context;
        userIsWaiting = false;
        // Registering as observer within the observables
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        InstructionActivity instructionActivity = new InstructionActivity();
        BreathingExerciseActivity breathingExerciseActivity = new BreathingExerciseActivity();
        WaitingRoomActivity waitingRoomActivity = new WaitingRoomActivity();
        SurveyEntranceActivity surveyEntranceActivity = new SurveyEntranceActivity();
        AdminActivity adminActivity = new AdminActivity();
        ChoiceQuestionActivity choiceQuestionActivity = new ChoiceQuestionActivity();
        TextQuestionActivity textQuestionActivity = new TextQuestionActivity();
        PointOfTimeQuestionActivity pointOfTimeQuestionActivity = new PointOfTimeQuestionActivity();
        TimeIntervalQuestionActivity timeIntervalQuestionActivity = new TimeIntervalQuestionActivity();
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
        timeIntervalQuestionActivity.addObserver(this);
        likertQuestionActivity.addObserver(this);
        service.addObserver(this);
    }

    // Entry point from AdminActivity
    public void startExperiment(ExperimentGroup experimentGroup, long startTimeInMs) {
        InternalStorage storage = new InternalStorage(currentContext);
        // Start service to receive a callback, when the user kills the app by swiping it in the recent apps list.
        currentContext.startService(new Intent(currentContext, AppKillCallbackService.class));
        // Cancel possible ongoing alarms
        AlarmScheduler alarmScheduler = new AlarmScheduler(currentContext);
        alarmScheduler.cancelAllAlarms();
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
        Survey currentSurvey = currentExperimentGroup.getFirstSurvey();
        currentSurveyId = currentSurvey.getId();
        setSurveyAlarm(currentSurvey, alarmScheduler, startTimeInMs);
        // Inform user
        Toast toast = Toast.makeText(currentContext, Config.EXPERIMENT_STARTED_TOAST, Toast.LENGTH_LONG);
        toast.show();
    }

    private void setSurveyAlarm(Survey survey, AlarmScheduler alarmScheduler, long referenceTime) {
        if (survey.isRelative()) {
            long relativeStartTimeInMillis = survey.getRelativeStartTimeInMin() * 60 * 1000;
            alarmScheduler.setRelativeSurveyAlarm(survey.getId(),
                    referenceTime,
                    relativeStartTimeInMillis);
        }
        else {
            alarmScheduler.setAbsoluteSurveyAlarm(survey.getId(),
                    currentExperimentGroup.getStartTimeInMillis(),
                    survey.getAbsoluteStartAtHour(),
                    survey.getAbsoluteStartAtMinute(),
                    survey.getAbsoluteStartDaysOffset());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Event event = (Event) arg;
        if (event.getContext() != null) {
            currentContext = event.getContext();
        }
        // Preparing utilities
        AlarmScheduler alarmScheduler = new AlarmScheduler(currentContext);
        NotificationHandler notificationHandler = new NotificationHandler(currentContext);
        InternalStorage storage = new InternalStorage(currentContext);
        Calendar calendar = Calendar.getInstance();
        // Preparing current state
        Survey currentSurvey = getCurrentSurvey();
        Step currentStep;
        // Checking event type
        switch (event.getType()) {

            case Config.EVENT_SURVEY_ALARM:
                // Making survey accessible via AdminActivity (App Launcher)
                storage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_OPENED);
                // Creating notification
                notificationHandler.sendNotification();
                // Setting timer to close the AdminActivity (App Launcher) entrance after the notification expired
                alarmScheduler.setNotificationTimeoutAlarm(currentSurvey.getNotificationDurationInMin());
                break;

            case Config.EVENT_NOTIFICATION_TIMEOUT:
                // Cancel Notification
                notificationHandler.cancelNotification();
                // Closing the AdminActivity (App Launcher) entrance
                storage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
                // Prepare next survey
                long referenceTime = calendar.getTimeInMillis() - currentSurvey.getNotificationDurationInMin() * 60 * 1000;
                prepareNextSurvey(currentSurvey, storage, alarmScheduler, referenceTime);
                break;

            case Config.EVENT_SURVEY_STARTED:
                // As the survey got started the notification timeout is not relevant anymore
                alarmScheduler.cancelNotificationTimeoutAlarm();
                // Each survey has a maximum duration. The timeout alarm is set here
                alarmScheduler.setSurveyTimeoutAlarm(currentSurvey.getId(), currentSurvey.getMaxDurationInMin());
                // Beginning with the first step of the survey
                currentStep = currentSurvey.getFirstStep();
                currentStepId = currentStep.getId();
                navigateToStep(currentStep);
                break;

            case Config.EVENT_NEXT_STEP:
                currentStep = getCurrentStep();
                switchToNextStep(currentSurvey, currentStep, storage, alarmScheduler, calendar);
                break;

            case Config.EVENT_NEXT_QUESTION:
                currentStep = getCurrentStep();
                Question nextQuestion = getNextQuestion(currentSurvey, currentStep, event, calendar);
                if (nextQuestion != null) {
                    currentQuestionId = nextQuestion.getId();
                    navigateToQuestion(nextQuestion);
                }
                else {
                    switchToNextStep(currentSurvey, currentStep, storage, alarmScheduler, calendar);
                }
                break;

            case Config.EVENT_STEP_TIMER:
                // The ongoing instruction is finished (see example in setStepTimer())
                int stepId = (int) event.getData();
                Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(stepId);
                // Boolean value that gets checked when the user reaches the point in the survey,
                // that has to wait for this ongoing instruction
                instructionToWaitFor.setFinished(true);
                // If the user already reached the above explained point, he/she gets redirected automatically
                if (userIsWaiting) {
                    userIsWaiting = false;
                    currentStep = getCurrentStep();
                    navigateToStep(currentStep);
                }
                break;

            case Config.EVENT_SURVEY_TIMEOUT:
                Toast toast = Toast.makeText(currentContext, Config.SURVEY_TIMEOUT_TOAST, Toast.LENGTH_LONG);
                toast.show();
                finishSurvey(currentSurvey, storage, alarmScheduler, calendar);
                break;

            case Config.EVENT_CSV_REQUEST:
                // When the csv is requested the CsvCreator class converts its whole Hashmap into a csvString
                // which is then saved in the internal storage.
                saveCsvInInternalStorage(storage);
                break;

            case Config.EVENT_APP_KILLED:
                // This event is triggered when the user swipes the app away in the recent apps list.
                // As a result the currentSurvey gets cancelled, the next survey is scheduled and the app exits.
                if (currentStepId != 0) {
                    finishSurvey(currentSurvey, storage, alarmScheduler, calendar);
                }
                break;

            default:
                break;
        }
    }

    private Survey getCurrentSurvey() {
        return currentExperimentGroup.getSurveyById(currentSurveyId);
    }

    private void prepareNextSurvey(Survey currentSurvey, InternalStorage storage,
                                   AlarmScheduler alarmScheduler, long referenceTime) {
        Survey nextSurvey = currentExperimentGroup.getSurveyById(currentSurvey.getNextSurveyId());
        if (nextSurvey != null) {
            currentSurveyId = nextSurvey.getId();
            setSurveyAlarm(nextSurvey, alarmScheduler, referenceTime);
        }
        else {
            finishExperiment(storage);
        }
    }

    private void finishExperiment(InternalStorage storage) {
        currentContext.stopService(new Intent(currentContext, AppKillCallbackService.class));
        currentSurveyId = 0;
        storage.saveFileContent(Config.FILE_NAME_EXPERIMENT_STATUS, Config.EXPERIMENT_FINISHED);
    }

    private void navigateToStep(Step step) {
        if (step.getType().equals(StepType.INSTRUCTION)) {
            Instruction instruction = (Instruction) step;
            Intent intent = new Intent(currentContext, InstructionActivity.class);
            intent.putExtra(Config.INSTRUCTION_HEADER_KEY, instruction.getHeader());
            intent.putExtra(Config.INSTRUCTION_TEXT_KEY, instruction.getText());
            intent.putExtra(Config.INSTRUCTION_IMAGE_KEY, instruction.getImageFileName());
            intent.putExtra(Config.INSTRUCTION_VIDEO_KEY, instruction.getVideoFileName());
            currentContext.startActivity(intent);
        }
        else if (step.getType().equals(StepType.BREATHING_EXERCISE)) {
            BreathingExercise breathingExercise = (BreathingExercise) step;
            Intent intent = new Intent(currentContext, BreathingExerciseActivity.class);
            intent.putExtra(Config.BREATHING_MODE_KEY, breathingExercise.getMode());
            intent.putExtra(Config.BREATHING_DURATION_KEY, breathingExercise.getDurationInMin());
            intent.putExtra(Config.BREATHING_FREQUENCY_KEY, breathingExercise.getBreathingFrequencyInSec());
            currentContext.startActivity(intent);
        }
        else if (step.getType().equals(StepType.QUESTIONNAIRE)) {
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
        else if (question.getType().equals(QuestionType.TIME_INTERVAL)) {
            TimeIntervalQuestion timeIntervalQuestion = (TimeIntervalQuestion) question;
            intent = new Intent(currentContext, TimeIntervalQuestionActivity.class);
            intent.putExtra(Config.TIME_INTERVALL_TYPES_KEY, timeIntervalQuestion.getTimeIntervalTypeNames());
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

    private Step getCurrentStep() {
        return currentExperimentGroup.getSurveyById(currentSurveyId).getStepById(currentStepId);
    }

    private void switchToNextStep(Survey currentSurvey, Step currentStep, InternalStorage storage,
                                  AlarmScheduler alarmScheduler, Calendar calendar) {
        // Set step timer if the current step was an ongoing step (see setStepTimer() for an example)
        setStepTimer(currentStep, alarmScheduler);
        // Switching to next step
        Step nextStep = currentSurvey.getStepById(currentStep.getNextStepId());
        // There is a next step
        if (nextStep != null) {
            currentStepId = nextStep.getId();
            // Moving on to next step after a little checkup (Explanation inside the method)
            checkWaitingRequestAndNavigateToStep(currentSurvey, nextStep);
        }
        // There is no next step and the survey gets finished
        else {
            updateProgress(storage);
            finishSurvey(currentSurvey, storage, alarmScheduler, calendar);
        }
    }

    private void setStepTimer(Step step, AlarmScheduler alarmScheduler) {
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
                alarmScheduler.setStepTimer(instructionStep.getId(), instructionStep.getDurationInMin());
            }
        }
    }

    private void checkWaitingRequestAndNavigateToStep(Survey currentSurvey, Step step) {
        // Here we're checking if the next step waits for another step,
        // like in the "cotton-bud"-example in "setStepTimer()" explained.
        if (step.getWaitForStep() != 0) {
            Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(step.getWaitForStep());
            // The next step waits for the instruction "instructionToWaitFor".
            // If its not finished yet (in our "cotton-bud"-example 2 minutes haven't passed)
            // the user gets directed to the WaitingRoomActivity
            if (!instructionToWaitFor.isFinished()) {
                userIsWaiting = true;
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

    private void updateProgress(InternalStorage storage) {
        String progressString = storage.getFileContent(Config.FILE_NAME_PROGRESS);
        int progress = Integer.parseInt(progressString) + 1;
        storage.saveFileContent(Config.FILE_NAME_PROGRESS, Integer.toString(progress));
    }

    private void finishSurvey(Survey currentSurvey, InternalStorage storage,
                              AlarmScheduler alarmScheduler, Calendar calendar) {
        currentStepId = 0;
        alarmScheduler.cancelAllAlarms();
        prepareNextSurvey(currentSurvey, storage, alarmScheduler, calendar.getTimeInMillis());
        storage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
        Intent intent = new Intent(currentContext, LoginActivity.class);
        intent.putExtra(Config.EXIT_APP_KEY, true);
        currentContext.startActivity(intent);
    }

    private Question getNextQuestion(Survey currentSurvey, Step currentStep,
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
        else if (currentQuestion.getType().equals(QuestionType.TIME_INTERVAL)) {
            TimeIntervalQuestion timeIntervalQuestion = (TimeIntervalQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(timeIntervalQuestion.getNextQuestionId());
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

    private void saveCsvInInternalStorage(InternalStorage storage) {
        String csv = csvCreator.getCsvString();
        storage.saveFileContent(Config.FILE_NAME_CSV, csv);
    }
}
