package de.ur.remex.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.NotificationSender;
import de.ur.remex.view.SurveyEntranceActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText loginInput;
    private Button loginButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initial VP
        InternalStorage storage = new InternalStorage(this);
        String currentVpId = storage.getFileContent(Config.FILE_NAME_ID);
        if (currentVpId == null) {
            createInitialVP(storage);
        }
        // Checking for exit app attribute
        boolean exitApp = this.getIntent().getBooleanExtra(Config.EXIT_APP_KEY, false);
        if (exitApp) {
            exitApp();
        }
        initLoginScreen();
    }

    private void createInitialVP(InternalStorage storage) {
        String vpId = "Initial-VP";
        String group = "VP-GROUP";
        String progress = "0";
        String startDate = "dd.mm.jjjj";
        String startTime = "hh:mm";
        String csv = Config.INITIAL_CSV_VALUE;

        storage.saveFileContent(Config.FILE_NAME_ID, vpId);
        storage.saveFileContent(Config.FILE_NAME_GROUP, group);
        storage.saveFileContent(Config.FILE_NAME_START_DATE, startDate);
        storage.saveFileContent(Config.FILE_NAME_START_TIME, startTime);
        storage.saveFileContent(Config.FILE_NAME_PROGRESS, progress);
        storage.saveFileContent(Config.FILE_NAME_CSV, csv);
    }

    private void initLoginScreen() {
        loginButton = findViewById(R.id.loginButton);
        loginInput = findViewById(R.id.loginInput);
        loginButton.setOnClickListener(this);
    }

    private void exitApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart () {
        super.onStart();
        // Checking for active experiment
        InternalStorage storage = new InternalStorage(this);

        // TODO: Remove this line
        storage.saveFileContent(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);


        String experimentActive = storage.getFileContent(Config.FILE_NAME_SURVEY_ENTRANCE);
        if (experimentActive.equals(Config.SURVEY_ENTRANCE_OPENED)) {
            NotificationSender notificationManager = new NotificationSender(this);
            notificationManager.cancelNotification();
            Intent intent = new Intent(this, SurveyEntranceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if (loginButton.equals(v)) {
            InternalStorage storage = new InternalStorage(this);
            String password = storage.getFileContent(Config.FILE_NAME_PASSWORD);
            if (password == null) {
                storage.saveFileContent(Config.FILE_NAME_PASSWORD, Config.INITIAL_PASSWORD);
                password = Config.INITIAL_PASSWORD;
            }
            if (loginInput.getText().toString().equals(password)) {
                Intent intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
            }
            else {
                // AlertDialog: Falsches Passwort eingegeben
                new AlertDialog.Builder(this)
                        .setTitle(Config.PASSWORD_ALERT_TITLE)
                        .setMessage(Config.PASSWORD_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
    }

    // Disabling the OS-Back Button
    @Override
    public void onBackPressed() {
        //
    }
}