package de.ur.remex.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Observer;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.controller.ExperimentController;
import de.ur.remex.model.experiment.Experiment;
import de.ur.remex.model.experiment.ExperimentGroup;
import de.ur.remex.model.experiment.Instruction;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.model.experiment.breathingExercise.BreathingExercise;
import de.ur.remex.model.experiment.breathingExercise.BreathingMode;
import de.ur.remex.model.experiment.questionnaire.Answer;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;
import de.ur.remex.model.experiment.questionnaire.SingleChoiceQuestion;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.ExperimentAlarmManager;
import de.ur.remex.utilities.Observable;

// TODO: Make password changeable
// TODO: Implement Activity Lifecycle functionality (Home Button, etc...)

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private Button currentVPButton;
    private Button createVPButton;
    private Button testRunButton;
    private Button loadExperimentButton;
    private Button logoutButton;

    private Experiment experiment;

    private static final Observable OBSERVABLE = new Observable();

    // Request code for creating a CSV document.
    private static final int CREATE_CSV_FILE = 1;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        // Get current experiment
        experiment = getExperiment();
        // Check for create CSV request
        boolean createCsv = this.getIntent().getBooleanExtra(Config.CREATE_CSV_KEY, false);
        if (createCsv) {
            createCsv();
        }
        // Check for start experiment request
        boolean startExperiment = this.getIntent().getBooleanExtra(Config.START_EXPERIMENT_KEY, false);
        if (startExperiment) {
            startExperiment();
        }
        initAdminScreen();
    }

    private void startExperiment() {
        // Create new ExperimentController and start experiment
        InternalStorage storage = new InternalStorage(this);
        String vpGroup = storage.getFileContent(Config.FILE_NAME_GROUP);
        ExperimentGroup group = experiment.getExperimentGroupByName(vpGroup);
        long startTimeInMs = this.getIntent().getLongExtra(Config.START_TIME_MS_KEY, 0);
        ExperimentController experimentController = new ExperimentController(this);
        experimentController.startExperiment(group, startTimeInMs);
    }

    private void createCsv() {
        Event event = new Event(null, Config.EVENT_CSV_REQUEST, null);
        OBSERVABLE.notifyExperimentController(event);
        saveCsvExternalStorage();
    }

    public void saveCsvExternalStorage() {
        InternalStorage storage = new InternalStorage(this);
        String vpId = storage.getFileContent(Config.FILE_NAME_ID);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, vpId + ".csv");
        startActivityForResult(intent, CREATE_CSV_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == CREATE_CSV_FILE && resultCode == Activity.RESULT_OK) {
            InternalStorage storage = new InternalStorage(this);
            String csv = storage.getFileContent(Config.FILE_NAME_CSV);

            // The resultData contains a URI for the document or directory that
            // the user selected.
            if (resultData != null) {
                boolean success = false;
                Uri uri = resultData.getData();
                if (uri != null) {
                    // Writing csv in selected uri
                    success = writeInFile(uri, csv);
                }
                if (success) {
                    storage.saveFileContent(Config.FILE_NAME_CSV_STATUS, Config.CSV_SAVED);
                    Toast toast = Toast.makeText(this, Config.CSV_SAVED_TOAST, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            else {
                // AlertDialog: resultData == null
                new AlertDialog.Builder(this)
                        .setTitle(Config.EXTERNAL_WRITE_ALERT_TITLE)
                        .setMessage(Config.EXTERNAL_WRITE_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
    }

    private boolean writeInFile(@NonNull Uri uri, @NonNull String text) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        }
        catch (IOException e) {
            // AlertDialog: I/O Exception
            new AlertDialog.Builder(this)
                    .setTitle(Config.EXTERNAL_WRITE_ALERT_TITLE)
                    .setMessage(Config.EXTERNAL_WRITE_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
            return false;
        }
        return true;
    }

    private void initAdminScreen() {
        currentVPButton = findViewById(R.id.currentVPButton);
        createVPButton = findViewById(R.id.adminCreateVPButton);
        testRunButton = findViewById(R.id.testRunButton);
        loadExperimentButton = findViewById(R.id.loadExperimentButton);
        logoutButton = findViewById(R.id.logoutButton);
        currentVPButton.setOnClickListener(this);
        createVPButton.setOnClickListener(this);
        testRunButton.setOnClickListener(this);
        loadExperimentButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        restartAutoExitTimer();
    }

    @Override
    public void onClick(View v) {
        if (currentVPButton.equals(v)) {
            restartAutoExitTimer();
            InternalStorage storage = new InternalStorage(this);
            String vpGroup = storage.getFileContent(Config.FILE_NAME_GROUP);
            ExperimentGroup group = experiment.getExperimentGroupByName(vpGroup);
            Intent intent = new Intent(this, CurrentVPActivity.class);
            if (group != null) {
                intent.putExtra(Config.PROGRESS_MAXIMUM_KEY, group.getSurveys().size());
            }
            else {
                intent.putExtra(Config.PROGRESS_MAXIMUM_KEY, 1);
            }
            startActivity(intent);
        }
        else if (logoutButton.equals(v)) {
            cancelAutoExitTimer();
            // Switch to login activity and exit app
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(Config.EXIT_APP_KEY, true);
            startActivity(intent);
        }
        else if (createVPButton.equals(v)) {
            restartAutoExitTimer();
            ArrayList<ExperimentGroup> groups = experiment.getExperimentGroups();
            String[] groupNames = new String[groups.size()];
            for (int i = 0; i < groups.size(); i++) {
                groupNames[i] = groups.get(i).getName();
            }
            Intent intent = new Intent(this, CreateVPActivity.class);
            intent.putExtra(Config.GROUP_NAMES_KEY, groupNames);
            startActivity(intent);
        }
        else if (loadExperimentButton.equals(v)) {
            // TODO: Implement Loading Experiment as JSON into internal storage and update experiment variable
            // Save new experiment in internal storage
            // experiment = getExperiment
        }
        else if (testRunButton.equals(v)) {
            // TODO: Implement Test Experiment
        }
    }
    // TODO: Get experiment JSON from internal storage
    private Experiment getExperiment() {
        // Get experiment from internal storage
        return createExperiment();
    }

    private void restartAutoExitTimer() {
        ExperimentAlarmManager alarmManager = new ExperimentAlarmManager(this);
        alarmManager.setAdminTimeoutAlarm();
    }

    private void cancelAutoExitTimer() {
        ExperimentAlarmManager alarmManager = new ExperimentAlarmManager(this);
        alarmManager.cancelAdminTimeoutAlarm();
    }

    public void addObserver(Observer observer) {
        OBSERVABLE.deleteObservers();
        OBSERVABLE.addObserver(observer);
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }

    // TODO: The experiment object will be created by the RemEx Interface in the future.
    private Experiment createExperiment() {
        Experiment experiment = new Experiment("Test Experiment");
        ExperimentGroup experimentGroup = new ExperimentGroup("Experiment Group");
        ExperimentGroup controlGroup = new ExperimentGroup("Control Group");

        Survey survey1 = new Survey("Survey1 +1 Min", 0, 3, 5);
        survey1.setId(1);
        Survey survey2 = new Survey("Survey2 +2 Min", 60 * 1000, 3, 5);
        survey2.setId(2);

        // Building instruction steps
        ArrayList<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Instruction instruction = new Instruction();
            instruction.setId(i + 1);
            instruction.setHeader("header1_" + i);
            if (i == 1) {
                instruction.setText("Super, vielen Dank. Deine aktive Teilnahme wird vermerkt. Toll gemacht! Denke daran, dass die zwei Tage der Befragung am Smartphone nun vorbei sind und dein Besuch am Lehrstuhl für Kinder- und Jugendpsychiatrie und -psychotherapie bevorsteht. Super, vielen Dank. Deine aktive Teilnahme wird vermerkt. Toll gemacht! Jetzt gibt's noch ein paar weitere Fragen.");
            }
            else {
                instruction.setText("Super, vielen Dank. Deine aktive Teilnahme wird vermerkt. Toll gemacht! Denke daran, dass die zwei Tage der Befragung am Smartphone nun vorbei sind und dein Besuch am Lehrstuhl für Kinder- und Jugendpsychiatrie und -psychotherapie bevorsteht.");
            }
            if (i != 3 && i != 4) {
                instruction.setImageFileName("salivette1");
            }
            if (i == 4) {
                instruction.setVideoFileName("test_video");
            }

            /* Setting an ongoing instruction
            if (i == 0) {
                instruction.setDurationInMin(1);
                instruction.setWaitingText("Bitte warte noch wegen der ersten Instruktion.");
            }
            // Defining a step that has to wait for that instruction to finish
            if (i == 2) {
                instruction.setWaitForStep(1);
            }*/

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
        breathingExercise.setDurationInMin(0);
        breathingExercise.setBreathingFrequencyInSec(5);

        // Building questionnaire
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(7);
        questionnaire.setInstructionText("Instruktion des Fragebogens");
        // Building questions
        /* Text
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setName("textQuestion_0");
        textQuestion.setText("Wie hat sich das ganze angefühlt?");
        textQuestion.setHint("Warst du verägert, fröhlich, optimistisch, etc...");
        */
        // Single choice
        SingleChoiceQuestion singleChoiceQuestion = new SingleChoiceQuestion();
        singleChoiceQuestion.setName("singleChoiceQuestion_0");
        singleChoiceQuestion.setText("Wie hat sich das ganze angefühlt? Wie hat sich das ganze angefühlt? Wie hat sich das ganze angefühlt? Wie hat sich das ganze angefühlt?");
        singleChoiceQuestion.setHint("Warst du verägert, fröhlich, optimistisch, etc... Warst du verägert, fröhlich, optimistisc");
        /* Multiple choice
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
        multipleChoiceQuestion.setName("multipleChoiceQuestion_0");
        multipleChoiceQuestion.setText("Wie hat sich das ganze angefühlt?");
        multipleChoiceQuestion.setHint("Warst du verägert, fröhlich, optimistisch, etc...");
        // Minutes
        MinutesQuestion minutesQuestion = new MinutesQuestion();
        minutesQuestion.setName("minutesQuestion_0");
        minutesQuestion.setText("Wie lange hast du gebraucht?");
        minutesQuestion.setHint("Angabe in Minuten");
        // Likert
        LikertQuestion likertQuestion = new LikertQuestion();
        likertQuestion.setName("likertQuestion_0");
        likertQuestion.setText("Wie unangenehm war die Situation?");
        likertQuestion.setScaleMinimumLabel("Sehr unangenehm");
        likertQuestion.setScaleMaximumLabel("Gar nicht unangenehm");
        likertQuestion.setItemCount(5);
        likertQuestion.setInitialValue(1);
        // Hours
        HoursQuestion hoursQuestion = new HoursQuestion();
        hoursQuestion.setName("hoursQuestion_0");
        hoursQuestion.setText("Wie lange hast du gebraucht?");
        hoursQuestion.setHint("Angabe in Stunden");
        // Daytime
        DaytimeQuestion daytimeQuestion = new DaytimeQuestion();
        daytimeQuestion.setName("daytimeQuestion_0");
        daytimeQuestion.setText("Um wie viel Uhr bist du ins Bett gegangen?");
        // Days
        DaysQuestion daysQuestion = new DaysQuestion();
        daysQuestion.setName("daysQuestion_0");
        daysQuestion.setText("An wie vielen Tagen der letzten Woche hast du geraucht?");
        daysQuestion.setHint("Angabe in Tagen");
        // Date
        DateQuestion dateQuestion = new DateQuestion();
        dateQuestion.setName("dateQuestion_0");
        dateQuestion.setText("Wann hast du Geburtstag?");
        */
        // Building answers
        // Single choice
        Answer answerS1 = new Answer();
        answerS1.setText("Verärgert");
        answerS1.setCode("1");
        answerS1.setNextQuestion(null); //multipleChoiceQuestion
        singleChoiceQuestion.addAnswer(answerS1);
        Answer answerS2 = new Answer();
        answerS2.setText("Fröhlich");
        answerS2.setCode("2");
        answerS2.setNextQuestion(null); //dateQuestion
        singleChoiceQuestion.addAnswer(answerS2);
        Answer answerS3 = new Answer();
        answerS3.setText("Schlecht");
        answerS3.setCode("3");
        answerS3.setNextQuestion(null); //minuteQuestion
        singleChoiceQuestion.addAnswer(answerS3);
        Answer answerS4 = new Answer();
        answerS4.setText("Gut");
        answerS4.setCode("4");
        answerS4.setNextQuestion(null); //minuteQuestion
        singleChoiceQuestion.addAnswer(answerS4);
        Answer answerS5 = new Answer();
        answerS5.setText("Hervorragend");
        answerS5.setCode("5");
        answerS5.setNextQuestion(null); //minuteQuestion
        singleChoiceQuestion.addAnswer(answerS5);
        /* Multiple choice
        Answer answerM1 = new Answer();
        answerM1.setText("Verärgert");
        answerM1.setCode("1");
        answerM1.setNextQuestion(minutesQuestion);
        multipleChoiceQuestion.addAnswer(answerM1);
        Answer answerM2 = new Answer();
        answerM2.setText("Fröhlich");
        answerM2.setCode("2");
        answerM2.setNextQuestion(minutesQuestion);
        multipleChoiceQuestion.addAnswer(answerM2);
        // Connecting questions together
        textQuestion.setNextQuestion(singleChoiceQuestion);
        minutesQuestion.setNextQuestion(likertQuestion);
        likertQuestion.setNextQuestion(hoursQuestion);
        hoursQuestion.setNextQuestion(daytimeQuestion);
        daytimeQuestion.setNextQuestion(daysQuestion);
        daysQuestion.setNextQuestion(dateQuestion);
        dateQuestion.setNextQuestion(null);
        // Adding questions to questionnaire
        questionnaire.addQuestion(textQuestion);
        questionnaire.addQuestion(multipleChoiceQuestion);
        questionnaire.addQuestion(minutesQuestion);
        questionnaire.addQuestion(likertQuestion);
        questionnaire.addQuestion(hoursQuestion);
        questionnaire.addQuestion(daytimeQuestion);
        questionnaire.addQuestion(daysQuestion);
        questionnaire.addQuestion(dateQuestion);
         */
        questionnaire.addQuestion(singleChoiceQuestion);

        // Filling surveys with steps
        for (int i = 0; i < instructions.size(); i++) {
            Instruction currInstruction = instructions.get(i);
            Instruction nextInstruction;
            if (i == instructions.size() - 1) {
                currInstruction.setNextStep(breathingExercise);
                breathingExercise.setNextStep(questionnaire);
                questionnaire.setNextStep(null);
            }
            else {
                nextInstruction = instructions.get(i + 1);
                currInstruction.setNextStep(nextInstruction);
            }
            survey1.addStep(currInstruction);
            survey2.addStep(currInstruction);
            // Adding Breathing exercise and questionnaire
            if (i == instructions.size() - 1) {
                survey1.addStep(breathingExercise);
                survey1.addStep(questionnaire);
                survey2.addStep(breathingExercise);
                survey2.addStep(questionnaire);
            }
        }

        survey1.setNextSurvey(survey2);
        survey2.setNextSurvey(null);

        experimentGroup.addSurvey(survey1);
        experimentGroup.addSurvey(survey2);

        controlGroup.addSurvey(survey1);
        controlGroup.addSurvey(survey2);

        experiment.addExperimentGroup(experimentGroup);
        experiment.addExperimentGroup(controlGroup);

        return experiment;
    }
}