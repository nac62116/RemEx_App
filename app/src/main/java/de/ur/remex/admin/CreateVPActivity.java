package de.ur.remex.admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

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

public class CreateVPActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText vpIdEditText;
    private EditText startDateEditText;
    private Button createVPButton;
    private String selectedGroup;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vp);
        init();
    }

    private void init() {
        restartAutoExitTimer();
        vpIdEditText = findViewById(R.id.inputVPid);
        startDateEditText = findViewById(R.id.startDate);
        createVPButton = findViewById(R.id.createVP);
        Spinner vpGroupSpinner = findViewById(R.id.inputVPGroup);
        String[] groups = this.getIntent().getStringArrayExtra(Config.GROUP_NAMES_KEY);
        if (groups != null) {
            selectedGroup = groups[0];
        }
        else {
            groups = new String[0];
            selectedGroup = "";
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vpGroupSpinner.setAdapter(adapter);
        vpGroupSpinner.setOnItemSelectedListener(this);
        startDateEditText.setOnClickListener(this);
        createVPButton.setOnClickListener(this);
    }

    private void restartAutoExitTimer() {
        AlarmScheduler alarmScheduler = new AlarmScheduler(this);
        alarmScheduler.setAdminTimeoutAlarm();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedGroup = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        restartAutoExitTimer();
        if (startDateEditText.equals(v)) {
            DatePickerDialog datePickerDialog = createDatePickerDialog();
            datePickerDialog.show();
        }
        if (createVPButton.equals(v)) {
            if (inputIsValid()) {
                long startTimeInMs = getStartTimeInMs();
                // Send start request to AdminActivity
                sendStartRequest(startTimeInMs);
            }
            else {
                // AlertDialog: Input invalid
                new AlertDialog.Builder(this)
                        .setTitle(Config.INPUT_INVALID_ALERT_TITLE)
                        .setMessage(Config.INPUT_INVALID_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
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
            if (currentTimeInMillis > userSettedTimeInMillis) {
                // AlertDialog: Picked date lays in the past
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

    private boolean inputIsValid() {
        boolean isValid = false;
        if (vpIdEditText.getText().length() != 0 &&
                startDateEditText.getText().length() != 0) {
            isValid = true;
        }
        return isValid;
    }

    private long getStartTimeInMs() {
        Calendar calendar = Calendar.getInstance();
        // Format: dd.mm.yyyy
        String startDate = startDateEditText.getText().toString();
        int year = Integer.parseInt(startDate.substring(6));
        int month = Integer.parseInt(startDate.substring(3,5));
        int date = Integer.parseInt(startDate.substring(0,2));
        calendar.set(year, month - 1, date, 0, 0);
        return calendar.getTimeInMillis();
    }

    private void sendStartRequest(long startTimeInMs) {
        InternalStorage storage = new InternalStorage(this);
        String experimentStatus = storage.getFileContentString(Config.FILE_NAME_EXPERIMENT_STATUS);
        if (experimentStatus.equals(Config.EXPERIMENT_FINISHED)) {
            saveVpInInternalStorage(storage);
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra(Config.START_EXPERIMENT_KEY, true);
            intent.putExtra(Config.START_TIME_MS_KEY, startTimeInMs);
            startActivity(intent);
        }
        else {
            // AlertDialog: Experiment not finished
            new AlertDialog.Builder(this)
                    .setTitle(Config.EXPERIMENT_NOT_FINISHED_ALERT_TITLE)
                    .setMessage(Config.EXPERIMENT_NOT_FINISHED_ALERT_MESSAGE)
                    .setPositiveButton(Config.JA, (dialog, which) -> {
                        saveVpInInternalStorage(storage);
                        Intent intent = new Intent(CreateVPActivity.this, AdminActivity.class);
                        intent.putExtra(Config.START_EXPERIMENT_KEY, true);
                        intent.putExtra(Config.START_TIME_MS_KEY, startTimeInMs);
                        startActivity(intent);
                    })
                    .setNegativeButton(Config.NEIN, null)
                    .show();
        }
    }

    private void saveVpInInternalStorage(InternalStorage storage) {
        String vpId = vpIdEditText.getText().toString();
        String group = selectedGroup;
        String progress = "0";
        String startDate = startDateEditText.getText().toString();

        storage.saveFileContentString(Config.FILE_NAME_ID, vpId);
        storage.saveFileContentString(Config.FILE_NAME_GROUP, group);
        storage.saveFileContentString(Config.FILE_NAME_START_DATE, startDate);
        storage.saveFileContentString(Config.FILE_NAME_PROGRESS, progress);
    }

    @Override
    public void onBackPressed() {
        restartAutoExitTimer();
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }
}
