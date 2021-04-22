package de.ur.remex.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import de.ur.remex.model.experiment.questionnaire.ChoiceType;
import de.ur.remex.model.experiment.questionnaire.LikertQuestion;
import de.ur.remex.model.experiment.questionnaire.PointOfTimeQuestion;
import de.ur.remex.model.experiment.questionnaire.PointOfTimeType;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;
import de.ur.remex.model.experiment.questionnaire.ChoiceQuestion;
import de.ur.remex.model.experiment.questionnaire.TextQuestion;
import de.ur.remex.model.experiment.questionnaire.TimeIntervallQuestion;
import de.ur.remex.model.experiment.questionnaire.TimeIntervallType;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.AlarmSender;
import de.ur.remex.utilities.Observable;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private Button currentVPButton;
    private Button createVPButton;
    private Button testRunButton;
    private Button loadExperimentButton;
    private Button logoutButton;

    private Experiment experiment;

    private static final Observable OBSERVABLE = new Observable();

    // Request codes for external storage operations.
    private static final int CREATE_CSV_FILE = 1;
    private static final int GET_EXPERIMENT_JSON_FILE = 2;
    private static final int GET_TEST_EXPERIMENT_JSON_FILE = 3;
    // TODO: Remove this
    private static final int CREATE_JSON_FILE = 4;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        // Get current experiment
        getExperimentFromInternalStorage();

        // Check for create CSV request
        boolean createCsv = this.getIntent().getBooleanExtra(Config.CREATE_CSV_KEY, false);
        if (createCsv) {
            createCsv();
        }
        // Check for start experiment request
        boolean startExperiment = this.getIntent().getBooleanExtra(Config.START_EXPERIMENT_KEY, false);
        if (startExperiment) {
            if (experiment != null) {
                InternalStorage storage = new InternalStorage(this);
                String vpGroup = storage.getFileContent(Config.FILE_NAME_GROUP);
                ExperimentGroup group = experiment.getExperimentGroupByName(vpGroup);
                long startTimeInMs = this.getIntent().getLongExtra(Config.START_TIME_MS_KEY, 0);
                startExperiment(group, startTimeInMs);
            }
        }
        initAdminScreen();
    }

    private void startExperiment(ExperimentGroup group, long startTimeInMs) {
        // Create new ExperimentController and start experiment
        ExperimentController experimentController = new ExperimentController(this);
        experimentController.startExperiment(group, startTimeInMs);
    }

    private void createCsv() {
        Event event = new Event(null, Config.EVENT_CSV_REQUEST, null);
        OBSERVABLE.notifyExperimentController(event);
        saveCsvInExternalStorage();
    }

    private void saveCsvInExternalStorage() {
        InternalStorage storage = new InternalStorage(this);
        String vpId = storage.getFileContent(Config.FILE_NAME_ID);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, vpId + ".csv");
        startActivityForResult(intent, CREATE_CSV_FILE);
    }

    private void loadExperimentJSONFromExternalStorage(boolean isTestExperiment) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (isTestExperiment) {
            startActivityForResult(intent, GET_TEST_EXPERIMENT_JSON_FILE);
        }
        else {
            startActivityForResult(intent, GET_EXPERIMENT_JSON_FILE);
        }
    }

    // TODO: Remove this
    private void saveTestExperimentJSON() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "Test_Experiment.json");
        startActivityForResult(intent, CREATE_JSON_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        InternalStorage storage = new InternalStorage(this);
        if (requestCode == CREATE_CSV_FILE && resultCode == Activity.RESULT_OK) {
            String csv = storage.getFileContent(Config.FILE_NAME_CSV);
            csv = csv.replace("*","\n");
            // The resultData contains a URI for the document or directory that
            // the user selected.
            if (resultData != null) {
                boolean success = false;
                Uri uri = resultData.getData();
                if (uri != null) {
                    // Writing csv in selected uri
                    success = writeFile(uri, csv);
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
        else if ((requestCode == GET_EXPERIMENT_JSON_FILE || requestCode == GET_TEST_EXPERIMENT_JSON_FILE) && resultCode == Activity.RESULT_OK) {
            // The resultData contains a URI for the document or directory that
            // the user selected.
            if (resultData != null) {
                String experimentJSON = null;
                Uri uri = resultData.getData();
                if (uri != null) {
                    // Reading Experiment JSON from selected uri
                    experimentJSON = readFile(uri);
                }
                if (experimentJSON != null) {
                    if (requestCode == GET_EXPERIMENT_JSON_FILE) {
                        storage.saveFileContent(Config.FILE_NAME_EXPERIMENT_JSON, experimentJSON);
                        Toast toast = Toast.makeText(this, Config.EXPERIMENT_LOADED_TOAST, Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else {
                        // TODO: Prove that this JSON file is correct
                        Log.e("JSON", experimentJSON);
                        // Convert JSON and start test experiment
                        Experiment testExperiment = null;
                        // JSON-String to Experiment testExperiment
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            testExperiment = mapper.readValue(experimentJSON, Experiment.class);
                        }
                        catch (JsonProcessingException e) {
                            Log.e("JSON Exception", e.getMessage());
                            // AlertDialog: Systemfehler
                            new AlertDialog.Builder(this)
                                    .setTitle(Config.JSON_PARSE_ALERT_TITLE)
                                    .setMessage(Config.JSON_PARSE_ALERT_MESSAGE)
                                    .setPositiveButton(Config.OK, null)
                                    .show();
                        }
                        if (testExperiment != null) {
                            startExperiment(testExperiment.getExperimentGroups().get(0), 0);
                        }
                    }
                }
            }
            else {
                // AlertDialog: resultData == null
                new AlertDialog.Builder(this)
                        .setTitle(Config.EXTERNAL_READ_ALERT_TITLE)
                        .setMessage(Config.EXTERNAL_READ_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
        /* TODO: Remove this
        else if (requestCode == CREATE_JSON_FILE && resultCode == Activity.RESULT_OK) {
            ObjectMapper mapper = new ObjectMapper();
            String experimentJSON = "";
            try {
                experimentJSON = mapper.writeValueAsString(createExperiment());
            } catch (JsonProcessingException e) {
                // AlertDialog: Systemfehler
                new AlertDialog.Builder(this)
                        .setTitle(this.getResources().getString(R.string.exception_alert_title))
                        .setMessage(this.getResources().getString(R.string.exception_alert_text))
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
            // The resultData contains a URI for the document or directory that
            // the user selected.
            if (resultData != null) {
                boolean success = false;
                Uri uri = resultData.getData();
                if (uri != null) {
                    success = writeFile(uri, experimentJSON);
                }
                if (success) {
                    Toast toast = Toast.makeText(this, "Test Experiment Saved", Toast.LENGTH_LONG);
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
        }*/
    }

    private boolean writeFile(@NonNull Uri uri, @NonNull String text) {
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

    // TODO: Reader does not read the JSON file correctly
    private String readFile(@NonNull Uri uri) {
        InputStream inputStream;
        StringBuilder result = new StringBuilder();
        try {
            inputStream = getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            int characterCode = br.read();
            while (characterCode != -1) {
                result.append((char) characterCode);
                characterCode = br.read();
            }
            br.close();
        }
        catch (IOException e) {
            // AlertDialog: I/O Exception
            new AlertDialog.Builder(this)
                    .setTitle(Config.EXTERNAL_READ_ALERT_TITLE)
                    .setMessage(Config.EXTERNAL_READ_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
            return null;
        }
        return result.toString();
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
            //loadExperimentJSONFromExternalStorage(false);
            saveTestExperimentJSON();
        }
        else if (testRunButton.equals(v)) {
            loadExperimentJSONFromExternalStorage(true);

        }
    }
    // TODO: Get experiment JSON from internal storage
    private void getExperimentFromInternalStorage() {
        InternalStorage storage = new InternalStorage(this);
        String experimentJSON = storage.getFileContent(Config.FILE_NAME_EXPERIMENT_JSON);
        if (experimentJSON != null) {
            // JSON-String to Experiment experiment
            ObjectMapper mapper = new ObjectMapper();
            try {
                experiment = mapper.readValue(experimentJSON, Experiment.class);
            }
            catch (JsonProcessingException e) {
                // AlertDialog: Systemfehler
                new AlertDialog.Builder(this)
                        .setTitle(Config.JSON_PARSE_ALERT_TITLE)
                        .setMessage(Config.JSON_PARSE_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
                experiment = null;
            }
        }
        else {
            experiment = null;
        }
    }

    private void restartAutoExitTimer() {
        AlarmSender alarmManager = new AlarmSender(this);
        alarmManager.setAdminTimeoutAlarm();
    }

    private void cancelAutoExitTimer() {
        AlarmSender alarmManager = new AlarmSender(this);
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




    /* TODO: The experiment object will be created by the RemEx Interface in the future.
    private Experiment createExperiment() {
        Experiment experiment = new Experiment("Test Experiment");
        ExperimentGroup experimentGroup = new ExperimentGroup("Test Group");

        Survey survey1 = new Survey("Test Survey", 0, 15, 5);
        survey1.setId(1);

        // Building instruction steps
        ArrayList<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Instruction instruction = new Instruction();
            instruction.setId(i + 1);
            if (i == 0) {
                instruction.setHeader("Herzlich Willkommen zum Test Experiment");
                instruction.setText("In den folgenden Schritten wird dir die Funktionalität dieser App anhand eines Test Experiments vorgestellt. Dabei werden dir die verschiedenen Fragentypen und Übungen vorgestellt, damit du bei dem echten Experiment möglichst wenig Schwierigkeiten bekommst. Falls noch Fragen aufkommen brauchst du nicht zu zögern und kannst dich gerne an den Versuchsleiter wenden.");
                instruction.setDurationInMin(2);
                instruction.setWaitingText("Die Watterolle ist noch nicht 2 Minuten lang in deinem Mund gewesen. Bitte warte noch einen Moment. Du wirst dann automatisch zum nächsten Schritt weitergeleitet.");
            }
            if (i == 1) {
                instruction.setHeader("Watterolle");
                instruction.setImageFileName("salivette1");
                instruction.setText("Nimm nun bitte die Watterolle aus dem Behälter und behalte sie solange im Mund bis du darauf hingewiesen wirst sie heraus zu nehmen.");
            }
            if (i == 2) {
                instruction.setHeader("Video");
                instruction.setVideoFileName("test_video");
                instruction.setText("Die Watterolle muss einige Zeit in deinem Mund bleiben. Damit du dich nicht langweilst kannst du dir das Video ansehen und danach zum nächsten Schritt über den \"Weiter\" Button fortfahren.");
            }
            if (i == 3) {
                instruction.setHeader("Watterolle herausnehmen");
                instruction.setImageFileName("salivette2");
                instruction.setText("Sehr gut, du hast die Watterolle jetzt lange genug im Mund behalten. Nimm sie bitte heraus, stecke sie in den Behälter und beschrifte diesen mit \"Test Experiment\". Danach kannst du auf weiter drücken um die Atemübung kennen zu lernen.");
                instruction.setWaitForStep(1);
            }
            instructions.add(instruction);
        }
        // Building breathing exercises
        Instruction breathingInstruction = new Instruction();
        breathingInstruction.setId(5);
        breathingInstruction.setHeader("Erklärung der Atemübung");
        breathingInstruction.setText("Wie bereits angekündigt folgt jetzt eine Atemübung. Setze dich entspannt hin und versuche deinen Atem den Bewegungen und Tönen des Kreises folgen zu lassen. Das ganze wird eine Minute dauern und wenn du möchtest, kannst du dabei auch deine Augen schließen. Wenn du bereit bist drücke auf \"Weiter\". Der Kreis wird auftauchen und du wirst sehen in welchem Rythmus dein Atem ihm folgen muss.");
        BreathingExercise breathingExercise = new BreathingExercise();
        breathingExercise.setId(6);
        breathingExercise.setMode(BreathingMode.MOVING_CIRCLE);
        breathingExercise.setDurationInMin(1);
        breathingExercise.setBreathingFrequencyInSec(5);
        Instruction breathingDischarge = new Instruction();
        breathingDischarge.setId(7);
        breathingDischarge.setHeader("Atemübung geschafft");
        breathingDischarge.setText("Sehr gut. Du hast die Atemübung erfolgreich absolviert. Zu guter Letzt werden dir jetzt die verschiedenen Fragentypen vorgestellt damit du dich mit deren Funktionalität vertraut machen kannst.");

        // Building questionnaire
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(8);
        // Building questions
        // Text
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setId(1);
        textQuestion.setName("Text Question");
        textQuestion.setText("Wie fühlst du dich vor deinem Experiment?");
        textQuestion.setHint("Das hier ist eine Text Frage. Gebe deine Antwort einfach in das untere Feld ein und drücke anschließend auf \"Weiter\".");
        // Single choice
        ChoiceQuestion singleChoiceQuestion = new ChoiceQuestion();
        singleChoiceQuestion.setId(2);
        singleChoiceQuestion.setChoiceType(ChoiceType.SINGLE_CHOICE);
        singleChoiceQuestion.setName("Single Choice Question");
        singleChoiceQuestion.setText("Wie gespannt bist du auf die Fragen in deinem Experiment?");
        singleChoiceQuestion.setHint("Das hier ist eine Einzelauswahl Frage. Das bedeutet, dass du nur eine der Antworten in der unteren Liste auswählen kannst. Nachdem du sie ausgwählt hast kannst du dich natürlich trotzdem nochmal umentscheiden.");
        // Multiple choice
        ChoiceQuestion multipleChoiceQuestion = new ChoiceQuestion();
        multipleChoiceQuestion.setId(3);
        multipleChoiceQuestion.setChoiceType(ChoiceType.MULTIPLE_CHOICE);
        multipleChoiceQuestion.setName("Multiple Choice Question");
        multipleChoiceQuestion.setText("Wofür benutzt du normalerweise dein Smartphone?");
        multipleChoiceQuestion.setHint("Das hier ist eine Mehrfachauswahl Frage. Das bedeutet, dass du mehrere Antworten in der unteren Liste auswählen kannst. Durch einen weiteres Drücken auf eine bereits ausgwählte Antwort kannst du sie auch wieder abwählen.");
        // Daytime
        PointOfTimeQuestion daytimeQuestion = new PointOfTimeQuestion();
        daytimeQuestion.setId(4);
        daytimeQuestion.addPointOfTimeType(PointOfTimeType.DAYTIME);
        daytimeQuestion.setName("Daytime Question");
        daytimeQuestion.setText("Um wie viel Uhr bist du gestern ungefähr ins Bett gegangen?");
        daytimeQuestion.setHint("Das hier ist eine Frage nach der Uhrzeit. Wenn du auf das untere Feld drückst erscheint eine Uhr bei der du Stunden und Minuten auf einem Kreis auswählen kannst. Falls du dich korrigieren möchtest, drücke enifach auf die entsprechende Zahl über der Uhr.");
        // Date
        PointOfTimeQuestion dateQuestion = new PointOfTimeQuestion();
        dateQuestion.setId(5);
        dateQuestion.addPointOfTimeType(PointOfTimeType.DATE);
        dateQuestion.setName("Date Question");
        dateQuestion.setText("Wann hast du Geburtstag?");
        dateQuestion.setHint("Das hier ist eine Frage nach einem Datum. Wenn du auf das untere Feld drückst erscheint ein Kalender in dem du dein Geburtsdatum auswählen kannst. Drücke einfach auf die Jahreszahl, den Monat oder den Tag den du verändern möchtest.");
        // Time Intervall (Hours, Minutes)
        TimeIntervallQuestion hoursMinutesQuestion = new TimeIntervallQuestion();
        hoursMinutesQuestion.setId(6);
        hoursMinutesQuestion.addTimeIntervallType(TimeIntervallType.HOURS);
        hoursMinutesQuestion.addTimeIntervallType(TimeIntervallType.MINUTES);
        hoursMinutesQuestion.setName("Time Intervall Hours/Minutes Question");
        hoursMinutesQuestion.setText("Wie lange hast du Freitags Schule?");
        hoursMinutesQuestion.setHint("Das hier ist eine Frage über eine Zeitspanne. Gebe einfach die entsprechenden Zeiten in die unteren Felder ein.");
        // Time Intervall (Years, Months, Days)
        TimeIntervallQuestion yearsMonthsDaysQuestion = new TimeIntervallQuestion();
        yearsMonthsDaysQuestion.setId(7);
        yearsMonthsDaysQuestion.addTimeIntervallType(TimeIntervallType.YEARS);
        yearsMonthsDaysQuestion.addTimeIntervallType(TimeIntervallType.MONTHS);
        yearsMonthsDaysQuestion.addTimeIntervallType(TimeIntervallType.DAYS);
        yearsMonthsDaysQuestion.setName("Time Intervall Years/Months/Days Question");
        yearsMonthsDaysQuestion.setText("Wie alt bist du?");
        yearsMonthsDaysQuestion.setHint("Nochmal eine Frage über ein Zeitintervall. Falls du nicht genau weist wie viel Monate oder Tage du alt bist, kannst du gerne einfach eine \"0\" eintragen.");
        // Time Intervall (Minutes, Seconds)
        TimeIntervallQuestion minutesSecondsQuestion = new TimeIntervallQuestion();
        minutesSecondsQuestion.setId(8);
        minutesSecondsQuestion.addTimeIntervallType(TimeIntervallType.MINUTES);
        minutesSecondsQuestion.addTimeIntervallType(TimeIntervallType.SECONDS);
        minutesSecondsQuestion.setName("Time Intervall Minutes/Seconds Question");
        minutesSecondsQuestion.setText("Wie lange kannst du die Luft anhalten?");
        minutesSecondsQuestion.setHint("Wieder eine Frage über eine Zeitspanne. Auch hier gilt, falls du nicht genau weißt wie viele Minuten oder Sekunden du die Luft anhalten kannst, trage einfach eine Schätzung ein.");
        // Likert
        LikertQuestion likertQuestion = new LikertQuestion();
        likertQuestion.setId(9);
        likertQuestion.setName("Likert Question");
        likertQuestion.setText("Wie verständlich war die Bedienung der App?");
        likertQuestion.setScaleMinimumLabel("Sehr verständlich");
        likertQuestion.setScaleMaximumLabel("Gar nicht verständlich");
        likertQuestion.setItemCount(9);
        likertQuestion.setInitialValue(5);
        // Building answers
        // Single choice
        Answer answerS1 = new Answer();
        answerS1.setText("Sehr gespannt");
        answerS1.setCode("1");
        answerS1.setNextQuestionId(multipleChoiceQuestion.getId());
        singleChoiceQuestion.addAnswer(answerS1);
        Answer answerS2 = new Answer();
        answerS2.setText("Ein wenig gespannt");
        answerS2.setCode("2");
        answerS2.setNextQuestionId(multipleChoiceQuestion.getId());
        singleChoiceQuestion.addAnswer(answerS2);
        Answer answerS3 = new Answer();
        answerS3.setText("Ist mir egal");
        answerS3.setCode("3");
        answerS3.setNextQuestionId(multipleChoiceQuestion.getId());
        singleChoiceQuestion.addAnswer(answerS3);
        Answer answerS4 = new Answer();
        answerS4.setText("Eher nicht gespannt");
        answerS4.setCode("4");
        answerS4.setNextQuestionId(multipleChoiceQuestion.getId());
        singleChoiceQuestion.addAnswer(answerS4);
        Answer answerS5 = new Answer();
        answerS5.setText("Gar nicht gespannt");
        answerS5.setCode("5");
        answerS5.setNextQuestionId(multipleChoiceQuestion.getId());
        singleChoiceQuestion.addAnswer(answerS5);
        // Multiple choice
        Answer answerM1 = new Answer();
        answerM1.setText("Videos anschauen");
        answerM1.setCode("1");
        multipleChoiceQuestion.addAnswer(answerM1);
        Answer answerM2 = new Answer();
        answerM2.setText("Nachrichten schreiben");
        answerM2.setCode("2");
        multipleChoiceQuestion.addAnswer(answerM2);
        Answer answerM3 = new Answer();
        answerM3.setText("Telefonieren");
        answerM3.setCode("3");
        multipleChoiceQuestion.addAnswer(answerM3);
        Answer answerM4 = new Answer();
        answerM4.setText("Spiele spielen");
        answerM4.setCode("4");
        multipleChoiceQuestion.addAnswer(answerM4);
        Answer answerM5 = new Answer();
        answerM5.setText("Fotos machen");
        answerM5.setCode("5");
        multipleChoiceQuestion.addAnswer(answerM5);
        Answer answerM6 = new Answer();
        answerM6.setText("Notizen verfassen");
        answerM6.setCode("6");
        multipleChoiceQuestion.addAnswer(answerM6);
        // Connecting questions together
        textQuestion.setNextQuestionId(singleChoiceQuestion.getId());
        multipleChoiceQuestion.setNextQuestionId(daytimeQuestion.getId());
        daytimeQuestion.setNextQuestionId(dateQuestion.getId());
        dateQuestion.setNextQuestionId(hoursMinutesQuestion.getId());
        hoursMinutesQuestion.setNextQuestionId(yearsMonthsDaysQuestion.getId());
        yearsMonthsDaysQuestion.setNextQuestionId(minutesSecondsQuestion.getId());
        minutesSecondsQuestion.setNextQuestionId(likertQuestion.getId());
        likertQuestion.setNextQuestionId(0);
        // Adding questions to questionnaire
        questionnaire.addQuestion(textQuestion);
        questionnaire.addQuestion(singleChoiceQuestion);
        questionnaire.addQuestion(multipleChoiceQuestion);
        questionnaire.addQuestion(daytimeQuestion);
        questionnaire.addQuestion(dateQuestion);
        questionnaire.addQuestion(hoursMinutesQuestion);
        questionnaire.addQuestion(yearsMonthsDaysQuestion);
        questionnaire.addQuestion(minutesSecondsQuestion);
        questionnaire.addQuestion(likertQuestion);

        // Filling surveys with steps
        // Adding first instructions
        for (int i = 0; i < instructions.size(); i++) {
            Instruction currInstruction = instructions.get(i);
            Instruction nextInstruction;
            if (i == instructions.size() - 1) {
                currInstruction.setNextStepId(breathingInstruction.getId());
            }
            else {
                nextInstruction = instructions.get(i + 1);
                currInstruction.setNextStepId(nextInstruction.getId());
            }
            survey1.addStep(currInstruction);
        }
        // Adding Breathing exercise and questionnaire
        breathingInstruction.setNextStepId(breathingExercise.getId());
        survey1.addStep(breathingInstruction);
        breathingExercise.setNextStepId(breathingDischarge.getId());
        survey1.addStep(breathingExercise);
        breathingDischarge.setNextStepId(questionnaire.getId());
        survey1.addStep(breathingDischarge);
        questionnaire.setNextStepId(0);
        survey1.addStep(questionnaire);

        survey1.setNextSurveyId(0);

        experimentGroup.addSurvey(survey1);

        experiment.addExperimentGroup(experimentGroup);

        return experiment;
    }*/
}