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

public class CurrentVPActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView currentVpId;
    private TextView currentVpGroup;
    private TextView currentVpProgress;
    private TextView currentVpStartDate;
    private TextView currentVpStartTime;
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
        currentVpStartTime = findViewById(R.id.currentStartTime);
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
        currentVpId.setText(storage.getFileContent(Config.FILE_NAME_ID));
        currentVpGroup.setText(storage.getFileContent(Config.FILE_NAME_GROUP));
        String progress = storage.getFileContent(Config.FILE_NAME_PROGRESS) + "/" + this.getIntent().getIntExtra(Config.PROGRESS_MAXIMUM_KEY, 0);
        currentVpProgress.setText(progress);
        currentVpStartTime.setText(storage.getFileContent(Config.FILE_NAME_START_TIME));
        currentVpStartDate.setText(storage.getFileContent(Config.FILE_NAME_START_DATE));
        csvStatus.setText(storage.getFileContent(Config.FILE_NAME_CSV_STATUS));
    }

    @Override
    public void onBackPressed() {
        restartAutoExitTimer();
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }
}
