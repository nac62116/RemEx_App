package de.ur.remex.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.AlarmScheduler;

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

public class CurrentVPActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView currentVpId;
    private TextView currentVpGroup;
    private TextView currentVpProgress;
    private TextView currentVpStartDate;
    private TextView csvStatus;
    private Button createCsvButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_vp);
        init();
        setCurrentVpInfo();
    }

    private void init() {
        restartAutoExitTimer();
        currentVpId = findViewById(R.id.currentVPid);
        currentVpGroup = findViewById(R.id.currentGroup);
        currentVpProgress = findViewById(R.id.currentProgress);
        currentVpStartDate = findViewById(R.id.currentStartDate);
        csvStatus = findViewById(R.id.csvStatus);
        createCsvButton = findViewById(R.id.createCsvButton);
        createCsvButton.setOnClickListener(this);
    }

    private void restartAutoExitTimer() {
        AlarmScheduler alarmScheduler = new AlarmScheduler(this);
        alarmScheduler.setAdminTimeoutAlarm();
    }

    @Override
    public void onClick(View v) {
        if (createCsvButton.equals(v)) {
            restartAutoExitTimer();
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra(Config.CREATE_CSV_KEY, true);
            startActivity(intent);
        }
    }

    private void setCurrentVpInfo() {
        InternalStorage storage = new InternalStorage(this);
        currentVpId.setText(storage.getFileContentString(Config.FILE_NAME_ID));
        currentVpGroup.setText(storage.getFileContentString(Config.FILE_NAME_GROUP));
        String progress = storage.getFileContentString(Config.FILE_NAME_PROGRESS) + "/" + this.getIntent().getIntExtra(Config.PROGRESS_MAXIMUM_KEY, 0);
        currentVpProgress.setText(progress);
        currentVpStartDate.setText(storage.getFileContentString(Config.FILE_NAME_START_DATE));
        csvStatus.setText(storage.getFileContentString(Config.FILE_NAME_CSV_STATUS));
    }

    @Override
    public void onBackPressed() {
        restartAutoExitTimer();
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }
}
