package de.ur.remex.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.Event;
import de.ur.remex.utilities.AlarmScheduler;
import de.ur.remex.utilities.JSONParser;
import de.ur.remex.utilities.Observable;

// TODO: Text alignment -> (center, justify, left?)

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private Button currentVPButton;
    private Button createVPButton;
    private Button testRunButton;
    private Button loadExperimentButton;
    private TextView currentExperimentNameView;
    private Button logoutButton;

    private Experiment currentExperiment;

    private static final Observable OBSERVABLE = new Observable();

    // Request codes for external storage operations.
    private static final int CREATE_CSV_FILE = 1;
    private static final int GET_EXPERIMENT_JSON_FILE = 2;
    private static final int GET_TEST_EXPERIMENT_JSON_FILE = 3;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        InternalStorage storage = new InternalStorage(this);
        // Get currently loaded experiment
        currentExperiment = getExperimentFromInternalStorage(storage);
        // Check for create CSV request
        boolean createCsv = this.getIntent().getBooleanExtra(Config.CREATE_CSV_KEY, false);
        if (createCsv) {
            createCsv(storage);
        }
        // Check for start experiment request
        boolean startExperiment = this.getIntent().getBooleanExtra(Config.START_EXPERIMENT_KEY, false);
        if (startExperiment) {
            if (currentExperiment != null) {
                String vpGroup = storage.getFileContent(Config.FILE_NAME_GROUP);
                ExperimentGroup group = currentExperiment.getExperimentGroupByName(vpGroup);
                long startTimeInMs = this.getIntent().getLongExtra(Config.START_TIME_MS_KEY, 0);
                startExperiment(group, startTimeInMs, storage);
            }
        }
        initAdminScreen();
    }

    private Experiment getExperimentFromInternalStorage(InternalStorage storage) {
        Experiment experiment = null;
        if (storage.fileExists(Config.FILE_NAME_EXPERIMENT_JSON)) {
            String experimentJSON = storage.getFileContent(Config.FILE_NAME_EXPERIMENT_JSON);
            experiment = parseExperimentJSON(experimentJSON);
        }
        return experiment;
    }

    private Experiment parseExperimentJSON(String experimentJSON) {
        // JSON-String to Experiment object
        JSONParser parser = new JSONParser(this);
        return (Experiment) parser.parse(experimentJSON, Experiment.class);
    }

    private void createCsv(InternalStorage storage) {
        Event event = new Event(null, Config.EVENT_CSV_REQUEST, null);
        OBSERVABLE.notifyExperimentController(event);
        saveCsvInExternalStorage(storage);
    }

    private void saveCsvInExternalStorage(InternalStorage storage) {
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
        InternalStorage storage = new InternalStorage(this);
        if (requestCode == CREATE_CSV_FILE && resultCode == Activity.RESULT_OK) {
            String csv = storage.getFileContent(Config.FILE_NAME_CSV);
            // Line breaks get deleted in the internal storage and therefore they were replaced by stars(*)
            // -> Reversing this here
            csv = csv.replace("*","\n");
            // The resultData contains a URI for the document or directory that the user selected.
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
            // The resultData contains a URI for the document or directory that the user selected.
            if (resultData != null) {
                String experimentJSON = null;
                Uri uri = resultData.getData();
                if (uri != null) {
                    // Reading Experiment JSON File from selected uri
                    experimentJSON = readFile(uri);
                }
                if (experimentJSON != null) {
                    Experiment experiment = parseExperimentJSON(experimentJSON);
                    if (experiment != null) {
                        if (requestCode == GET_EXPERIMENT_JSON_FILE) {
                            storage.saveFileContent(Config.FILE_NAME_EXPERIMENT_JSON, experimentJSON);
                            currentExperiment = experiment;
                            String experimentName = Config.EXPERIMENT_NAME_FIELD_SUFFIX + currentExperiment.getName();
                            currentExperimentNameView.setText(experimentName);
                            Toast toast = Toast.makeText(this, Config.EXPERIMENT_LOADED_TOAST, Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else {
                            startExperiment(experiment.getExperimentGroups().get(0), 0, storage);
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
            new AlertDialog.Builder(this)
                    .setTitle(Config.EXTERNAL_WRITE_ALERT_TITLE)
                    .setMessage(Config.EXTERNAL_WRITE_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
            return false;
        }
        return true;
    }

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
            new AlertDialog.Builder(this)
                    .setTitle(Config.EXTERNAL_READ_ALERT_TITLE)
                    .setMessage(Config.EXTERNAL_READ_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
            return null;
        }
        return result.toString();
    }

    private void startExperiment(ExperimentGroup group, long startTimeInMs, InternalStorage storage) {
        if (storage.getFileContent(Config.FILE_NAME_CSV_STATUS).equals(Config.CSV_SAVED)) {
            ExperimentController experimentController = new ExperimentController(this);
            experimentController.startExperiment(group, startTimeInMs);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(Config.CSV_NOT_SAVED_ALERT_TITLE)
                    .setMessage(Config.CSV_NOT_SAVED_ALERT_MESSAGE)
                    .setPositiveButton(Config.JA, (dialog, which) -> {
                        ExperimentController experimentController = new ExperimentController(this);
                        experimentController.startExperiment(group, startTimeInMs);
                    })
                    .setNegativeButton(Config.NEIN, null)
                    .show();
        }
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
        currentExperimentNameView = findViewById(R.id.currentExperimentNameView);
        if (currentExperiment != null) {
            String experimentName = Config.EXPERIMENT_NAME_FIELD_SUFFIX + currentExperiment.getName();
            currentExperimentNameView.setText(experimentName);
        }
        else {
            currentExperimentNameView.setText(Config.EXPERIMENT_NAME_FIELD_NO_EXPERIMENT_LOADED);
        }
        restartAutoExitTimer();
    }

    @Override
    public void onClick(View v) {
        if (currentVPButton.equals(v)) {
            restartAutoExitTimer();
            InternalStorage storage = new InternalStorage(this);
            String vpGroup = storage.getFileContent(Config.FILE_NAME_GROUP);
            Intent intent = new Intent(this, CurrentVPActivity.class);
            if (currentExperiment != null) {
                ExperimentGroup group = currentExperiment.getExperimentGroupByName(vpGroup);
                if (group != null) {
                    intent.putExtra(Config.PROGRESS_MAXIMUM_KEY, group.getSurveys().size());
                }
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
            InternalStorage storage = new InternalStorage(this);
            if (storage.fileExists(Config.FILE_NAME_EXPERIMENT_JSON)) {
                ArrayList<ExperimentGroup> groups = currentExperiment.getExperimentGroups();
                String[] groupNames = new String[groups.size()];
                for (int i = 0; i < groups.size(); i++) {
                    groupNames[i] = groups.get(i).getName();
                }
                Intent intent = new Intent(this, CreateVPActivity.class);
                intent.putExtra(Config.GROUP_NAMES_KEY, groupNames);
                startActivity(intent);
            }
            else {
                new AlertDialog.Builder(this)
                        .setTitle(Config.EXPERIMENT_NOT_LOADED_ALERT_TITLE)
                        .setMessage(Config.EXPERIMENT_NOT_LOADED_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
        else if (loadExperimentButton.equals(v)) {
            loadExperimentJSONFromExternalStorage(false);
        }
        else if (testRunButton.equals(v)) {
            loadExperimentJSONFromExternalStorage(true);

        }
    }

    private void restartAutoExitTimer() {
        AlarmScheduler alarmScheduler = new AlarmScheduler(this);
        alarmScheduler.setAdminTimeoutAlarm();
    }

    private void cancelAutoExitTimer() {
        AlarmScheduler alarmScheduler = new AlarmScheduler(this);
        alarmScheduler.cancelAdminTimeoutAlarm();
    }

    private void loadExperimentJSONFromExternalStorage(boolean isTestExperiment) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (isTestExperiment) {
            startActivityForResult(intent, GET_TEST_EXPERIMENT_JSON_FILE);
        }
        else {
            startActivityForResult(intent, GET_EXPERIMENT_JSON_FILE);
        }
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
}