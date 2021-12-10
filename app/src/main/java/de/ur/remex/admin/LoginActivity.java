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
import de.ur.remex.utilities.NotificationHandler;
import de.ur.remex.view.SurveyEntranceActivity;

/*
MIT License

Copyright (c) 2021 Colin Nash

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText loginInput;
    private Button loginButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Checking for exit app attribute
        boolean exitApp = this.getIntent().getBooleanExtra(Config.EXIT_APP_KEY, false);
        if (exitApp) {
            exitApp();
        }
        initLoginScreen();
    }

    @Override
    protected void onStart () {
        super.onStart();
        // Checking for active experiment
        InternalStorage storage = new InternalStorage(this);
        String experimentActive = storage.getFileContentString(Config.FILE_NAME_SURVEY_ENTRANCE);
        if (experimentActive.equals(Config.SURVEY_ENTRANCE_OPENED)) {
            NotificationHandler notificationHandler = new NotificationHandler(this);
            notificationHandler.cancelNotification();
            Intent intent = new Intent(this, SurveyEntranceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void exitApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initLoginScreen() {
        loginButton = findViewById(R.id.loginButton);
        loginInput = findViewById(R.id.loginInput);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (loginButton.equals(v)) {
            if (loginInput.getText().toString().equals(Config.INITIAL_PASSWORD)) {
                Intent intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
            }
            else {
                // AlertDialog: Entered wrong password
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