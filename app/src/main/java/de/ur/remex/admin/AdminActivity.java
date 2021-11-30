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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Observer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private Button currentVPButton;
    private Button createVPButton;
    private Button loadExperimentButton;
    private TextView currentExperimentNameView;
    private Button logoutButton;

    private static final Observable OBSERVABLE = new Observable();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        InternalStorage storage = new InternalStorage(this);
        // Initial storage files
        if (!storage.fileExists(Config.FILE_NAME_FIRST_START)) {
            createInitialStorageFiles(storage);
        }
        // Get currently loaded experiment
        Experiment currentExperiment = getExperimentFromInternalStorage(storage);
        // Check for create CSV request
        boolean createCsv = this.getIntent().getBooleanExtra(Config.CREATE_CSV_KEY, false);
        if (createCsv) {
            createCsv(storage);
        }
        // Check for load experiment request
        boolean loadExperiment = this.getIntent().getBooleanExtra(Config.LOAD_EXPERIMENT_KEY, false);
        if (loadExperiment) {
            loadExperimentFromExternalStorage();
        }
        // Check for start experiment request
        boolean startExperiment = this.getIntent().getBooleanExtra(Config.START_EXPERIMENT_KEY, false);
        if (startExperiment) {
            if (currentExperiment != null) {
                String vpGroup = storage.getFileContentString(Config.FILE_NAME_GROUP);
                ExperimentGroup group = currentExperiment.getExperimentGroupByName(vpGroup);
                long startTimeInMs = this.getIntent().getLongExtra(Config.START_TIME_MS_KEY, 0);
                startExperiment(group, startTimeInMs, storage);
            }
        }
        initAdminScreen(currentExperiment);
    }

    private void createInitialStorageFiles(InternalStorage storage) {
        String firstStart = "1";
        String vpId = "n/a";
        String group = "n/a";
        String progress = "0";
        String startDate = "n/a";
        String csv = Config.INITIAL_CSV_VALUE;
        String csvStatus = Config.CSV_SAVED;
        String surveyEntrance = Config.SURVEY_ENTRANCE_CLOSED;
        String experimentStatus = Config.EXPERIMENT_FINISHED;
        String nextSurveyAlarmTime = "0";

        storage.saveFileContentString(Config.FILE_NAME_FIRST_START, firstStart);
        storage.saveFileContentString(Config.FILE_NAME_ID, vpId);
        storage.saveFileContentString(Config.FILE_NAME_GROUP, group);
        storage.saveFileContentString(Config.FILE_NAME_START_DATE, startDate);
        storage.saveFileContentString(Config.FILE_NAME_PROGRESS, progress);
        storage.saveFileContentString(Config.FILE_NAME_CSV, csv);
        storage.saveFileContentString(Config.FILE_NAME_CSV_STATUS, csvStatus);
        storage.saveFileContentString(Config.FILE_NAME_SURVEY_ENTRANCE, surveyEntrance);
        storage.saveFileContentString(Config.FILE_NAME_EXPERIMENT_STATUS, experimentStatus);
        storage.saveFileContentString(Config.FILE_NAME_NEXT_SURVEY_ALARM, nextSurveyAlarmTime);
    }

    private Experiment getExperimentFromInternalStorage(InternalStorage storage) {
        Experiment experiment = null;
        if (storage.fileExists(Config.FILE_NAME_EXPERIMENT_JSON)) {
            String experimentJSON = storage.getFileContentString(Config.FILE_NAME_EXPERIMENT_JSON);
            JSONParser parser = new JSONParser(this);
            experiment = (Experiment) parser.parseJSONString(experimentJSON, Experiment.class);
        }
        return experiment;
    }

    private void createCsv(InternalStorage storage) {
        Event event = new Event(null, Config.EVENT_CSV_REQUEST, null);
        OBSERVABLE.notifyExperimentController(event);
        saveCsvInExternalStorage(storage);
    }

    private void saveCsvInExternalStorage(InternalStorage storage) {
        String vpId = storage.getFileContentString(Config.FILE_NAME_ID);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, vpId + ".csv");
        ActivityResultLauncher<Intent> saveCsvIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        getCsvFileFromActivityResult(result);
                    }
                });
        saveCsvIntent.launch(intent);
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

    private void loadExperimentFromExternalStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        ActivityResultLauncher<Intent> loadExperimentIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        getExperimentFromZip(result);
                    }
                });
        new AlertDialog.Builder(this)
                .setTitle(Config.LOADING_TIME_ALERT_TITLE)
                .setMessage(Config.LOADING_TIME_ALERT_MESSAGE)
                .setPositiveButton(Config.OK, (dialog, which) -> loadExperimentIntent.launch(intent))
                .show();
    }

    private void getCsvFileFromActivityResult(ActivityResult result) {
        InternalStorage storage = new InternalStorage(this);
        String csv = storage.getFileContentString(Config.FILE_NAME_CSV);
        // Line breaks get deleted in the internal storage and therefore they were replaced by stars(*)
        // -> Reversing this here
        csv = csv.replace("*","\n");
        // The resultData contains a URI for the document or directory that the user selected.
        if (result != null) {
            Intent intent = result.getData();
            if (intent != null) {
                Uri uri = result.getData().getData();
                boolean success = false;
                if (uri != null) {
                    // Writing csv in selected uri
                    success = writeFile(uri, csv);
                }
                if (success) {
                    storage.saveFileContentString(Config.FILE_NAME_CSV_STATUS, Config.CSV_SAVED);
                    Toast toast = Toast.makeText(this, Config.CSV_SAVED_TOAST, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(Config.EXTERNAL_WRITE_ALERT_TITLE)
                    .setMessage(Config.EXTERNAL_WRITE_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
        }
    }

    private void getExperimentFromZip(ActivityResult result) {
        InternalStorage storage = new InternalStorage(this);
        storage.clear();
        createInitialStorageFiles(storage);
        // The resultData contains a URI for the document or directory that the user selected.
        if (result != null) {
            Intent intent = result.getData();
            if (intent != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        ZipInputStream zis = new ZipInputStream(inputStream);
                        ZipEntry zipEntry = zis.getNextEntry();
                        while (zipEntry != null) {
                            if (!zipEntry.isDirectory()) {
                                // Experiment JSON entry
                                if (!(zipEntry.getName().contains("_Code_Tabelle"))
                                        && !(zipEntry.getName().contains("resources/"))) {
                                    StringBuilder experimentJSON = new StringBuilder();
                                    byte[] buffer = new byte[Config.JSON_UPLOAD_BUFFER_LENGTH];
                                    int length = zis.read(buffer);
                                    while (length > 0) {
                                        String line = new String(buffer, StandardCharsets.UTF_8);
                                        experimentJSON.append(line);
                                        length = zis.read(buffer);
                                    }
                                    storage.saveFileContentString(Config.FILE_NAME_EXPERIMENT_JSON, experimentJSON.toString());
                                    // User feedback
                                    JSONParser parser = new JSONParser(this);
                                    Experiment experiment = (Experiment) parser.parseJSONString(experimentJSON.toString(), Experiment.class);
                                    String experimentName = Config.EXPERIMENT_NAME_FIELD_SUFFIX + experiment.getName();
                                    currentExperimentNameView.setText(experimentName);
                                }
                                // Resource entries
                                if (zipEntry.getName().contains("resources/")) {
                                    storage.saveZipEntry(zipEntry.getName().replace("resources/", ""), zis);
                                }
                            }
                            zipEntry = zis.getNextEntry();
                        }
                        zis.closeEntry();
                        zis.close();
                        Toast toast = Toast.makeText(this, Config.EXPERIMENT_LOADED_TOAST, Toast.LENGTH_LONG);
                        toast.show();
                    }
                    catch (Exception e) {
                        new AlertDialog.Builder(this)
                                .setTitle(Config.ZIP_READING_ALERT_TITLE)
                                .setMessage(Config.ZIP_READING_ALERT_MESSAGE)
                                .setPositiveButton(Config.OK, null)
                                .show();
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

    private void startExperiment(ExperimentGroup group, long startTimeInMs, InternalStorage storage) {
        if (storage.getFileContentString(Config.FILE_NAME_CSV_STATUS).equals(Config.CSV_SAVED)) {
            ExperimentController experimentController = new ExperimentController(this);
            experimentController.startExperiment(group, startTimeInMs);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(Config.CSV_NOT_SAVED_ALERT_TITLE)
                    .setMessage(Config.CSV_NOT_SAVED_SAVE_EXPERIMENT_ALERT_MESSAGE)
                    .setPositiveButton(Config.JA, (dialog, which) -> {
                        ExperimentController experimentController = new ExperimentController(this);
                        experimentController.startExperiment(group, startTimeInMs);
                    })
                    .setNegativeButton(Config.NEIN, null)
                    .show();
        }
    }

    private void initAdminScreen(Experiment currentExperiment) {
        currentVPButton = findViewById(R.id.currentVPButton);
        createVPButton = findViewById(R.id.adminCreateVPButton);
        loadExperimentButton = findViewById(R.id.loadExperimentButton);
        logoutButton = findViewById(R.id.logoutButton);
        currentVPButton.setOnClickListener(this);
        createVPButton.setOnClickListener(this);
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
            Experiment currentExperiment = getExperimentFromInternalStorage(storage);
            String vpGroup = storage.getFileContentString(Config.FILE_NAME_GROUP);
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
            Experiment currentExperiment = getExperimentFromInternalStorage(storage);
            if (currentExperiment != null) {
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
            restartAutoExitTimer();
            InternalStorage storage = new InternalStorage(this);
            if (storage.getFileContentString(Config.FILE_NAME_CSV_STATUS).equals(Config.CSV_SAVED)) {
                Intent intent = new Intent(this, AdminActivity.class);
                intent.putExtra(Config.LOAD_EXPERIMENT_KEY, true);
                startActivity(intent);
            }
            else {
                new AlertDialog.Builder(this)
                        .setTitle(Config.CSV_NOT_SAVED_ALERT_TITLE)
                        .setMessage(Config.CSV_NOT_SAVED_LOAD_EXPERIMENT_ALERT_MESSAGE)
                        .setPositiveButton(Config.JA, (dialog, which) -> {
                            Intent intent = new Intent(this, AdminActivity.class);
                            intent.putExtra(Config.LOAD_EXPERIMENT_KEY, true);
                            startActivity(intent);
                        })
                        .setNegativeButton(Config.NEIN, null)
                        .show();
            }
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