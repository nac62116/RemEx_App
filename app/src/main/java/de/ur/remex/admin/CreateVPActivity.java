package de.ur.remex.admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import de.ur.remex.Config;
import de.ur.remex.R;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.ExperimentAlarmManager;

public class CreateVPActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText vpIdEditText;
    private Button vpGroupButton;
    private EditText startDateEditText;
    private EditText startTimeEditText;
    private Button createVPButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vp);
        init();
    }

    private void init() {
        restartAutoExitTimer();
        vpIdEditText = findViewById(R.id.inputVPid);
        vpGroupButton = findViewById(R.id.inputVPGroup);
        startDateEditText = findViewById(R.id.startDate);
        startTimeEditText = findViewById(R.id.startTime);
        createVPButton = findViewById(R.id.createVP);
        vpGroupButton.setOnClickListener(this);
        startDateEditText.setOnClickListener(this);
        startTimeEditText.setOnClickListener(this);
        createVPButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        restartAutoExitTimer();
        if (vpGroupButton.equals(v)) {
            // TODO: Convert vpGroupButton to dropdown menu with group data from current experiment
        }
        if (startDateEditText.equals(v)) {
            DatePickerDialog datePickerDialog = createDatePickerDialog();
            datePickerDialog.show();
        }
        if (startTimeEditText.equals(v)) {
            TimePickerDialog timePickerDialog = createTimePickerDialog();
            timePickerDialog.show();
        }
        if (createVPButton.equals(v)) {
            if (inputIsValid()) {
                // Get experiment start time in ms
                long startTimeInMs = getStartTimeInMs();
                // Send start request to AdminActivity
                sendStartRequest(startTimeInMs);
            }
            else {
                // AlertDialog: Nicht alle Felder wurden angegeben
                new AlertDialog.Builder(this)
                        .setTitle(Config.INPUT_INVALID_ALERT_TITLE)
                        .setMessage(Config.INPUT_INVALID_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
    }

    private long getStartTimeInMs() {
        Calendar calendar = Calendar.getInstance();
        // TODO: Move line "long startTimeInMs..." under "calender.set(..." and remove "+ 30 * 1000"
        long startTimeInMs = calendar.getTimeInMillis() + 30 * 1000;
        // Format: dd.mm.yyyy
        String startDate = startDateEditText.getText().toString();
        // Format: hh:mm Uhr
        String startTime = startTimeEditText.getText().toString();
        int year = Integer.parseInt(startDate.substring(6));
        int month = Integer.parseInt(startDate.substring(3,5));
        int date = Integer.parseInt(startDate.substring(0,2));
        int hour = Integer.parseInt(startTime.substring(0,2));
        int minute = Integer.parseInt(startTime.substring(3,5));
        calendar.set(year, month - 1, date, hour, minute);
        return startTimeInMs;
    }

    private void sendStartRequest(long startTimeInMs) {
        InternalStorage storage = new InternalStorage(this);
        String experimentStatus = storage.getFileContent(Config.FILE_NAME_EXPERIMENT_STATUS);
        if (experimentStatus == null) {
            experimentStatus = Config.EXPERIMENT_FINISHED;
            storage.saveFileContent(Config.FILE_NAME_EXPERIMENT_STATUS, experimentStatus);
        }
        if (experimentStatus.equals(Config.EXPERIMENT_FINISHED)) {
            saveVpInternalStorage();
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra(Config.START_EXPERIMENT_KEY, true);
            intent.putExtra(Config.START_TIME_MS_KEY, startTimeInMs);
            startActivity(intent);
        }
        else {
            // AlertDialog: Experiment wurde noch nicht beendet
            new AlertDialog.Builder(this)
                    .setTitle(Config.EXPERIMENT_NOT_FINISHED_ALERT_TITLE)
                    .setMessage(Config.EXPERIMENT_NOT_FINISHED_ALERT_MESSAGE)
                    .setPositiveButton(Config.JA, (dialog, which) -> {
                        saveVpInternalStorage();
                        Intent intent = new Intent(CreateVPActivity.this, AdminActivity.class);
                        intent.putExtra(Config.START_EXPERIMENT_KEY, true);
                        intent.putExtra(Config.START_TIME_MS_KEY, startTimeInMs);
                        startActivity(intent);
                    })
                    .setNegativeButton(Config.NEIN, null)
                    .show();
        }
    }

    private DatePickerDialog createDatePickerDialog() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        final int currentYear = c.get(Calendar.YEAR);
        final int currentMonth = c.get(Calendar.MONTH);
        final int currentDay = c.get(Calendar.DAY_OF_MONTH);
        final long currentTimeInMillis = c.getTimeInMillis();
        DatePickerDialog datePickerDialog;

        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            c.set(year, month, dayOfMonth);
            long userSettedTimeInMillis = c.getTimeInMillis();
            if (currentTimeInMillis >= userSettedTimeInMillis) {
                // AlertDialog: Ausgwähltes Datum liegt in der Vergangenheit
                new AlertDialog.Builder(CreateVPActivity.this)
                        .setTitle(Config.DATE_INVALID_ALERT_TITLE)
                        .setMessage(Config.DATE_INVALID_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
            else {
                month += 1;
                String dayString, monthString;
                if (month < 10) {
                    monthString = "0" + month;
                }
                else {
                    monthString = "" + month;
                }
                if (dayOfMonth < 10) {
                    dayString = "0" + dayOfMonth;
                }
                else {
                    dayString = "" + dayOfMonth;
                }
                String dateString = dayString + "." + monthString + "." + year;
                startDateEditText.setText(dateString);
            }
        }, currentYear, currentMonth, currentDay);
        return datePickerDialog;
    }

    private TimePickerDialog createTimePickerDialog() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;

        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String hour, min;
            if (hourOfDay < 10) {
                hour = "0" + hourOfDay;
            }
            else {
                hour = "" + hourOfDay;
            }
            if (minute < 10) {
                min = "0" + minute;
            }
            else {
                min = "" + minute;
            }
            String timeString = hour + ":" + min + " Uhr";
            startTimeEditText.setText(timeString);
        }, currentHour, currentMinute, true);
        return timePickerDialog;
    }

    private boolean inputIsValid() {
        boolean isValid = false;
        if (vpIdEditText.getText().length() != 0 &&
                startDateEditText.getText().length() != 0 &&
                startTimeEditText.getText().length() != 0) {
            isValid = true;
        }
        return isValid;
    }

    private void saveVpInternalStorage() {
        String vpId = vpIdEditText.getText().toString();
        String group = vpGroupButton.getText().toString();
        String progress = "0";
        String startDate = startDateEditText.getText().toString();
        String startTime = startTimeEditText.getText().toString();

        InternalStorage storage = new InternalStorage(this);
        storage.saveFileContent(Config.FILE_NAME_ID, vpId);
        storage.saveFileContent(Config.FILE_NAME_GROUP, group);
        storage.saveFileContent(Config.FILE_NAME_START_DATE, startDate);
        storage.saveFileContent(Config.FILE_NAME_START_TIME, startTime);
        storage.saveFileContent(Config.FILE_NAME_PROGRESS, progress);
    }

    private void restartAutoExitTimer() {
        ExperimentAlarmManager alarmManager = new ExperimentAlarmManager(this);
        alarmManager.setAdminTimeoutAlarm();
    }

    @Override
    public void onBackPressed() {
        restartAutoExitTimer();
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }
}
