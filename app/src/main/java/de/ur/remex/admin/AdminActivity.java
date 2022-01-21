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

/*
                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   APPENDIX: How to apply the Apache License to your work.

      To apply the Apache License to your work, attach the following
      boilerplate notice, with the fields enclosed by brackets "[]"
      replaced with your own identifying information. (Don't include
      the brackets!)  The text should be enclosed in the appropriate
      comment syntax for the file format. We also recommend that a
      file or class name and description of purpose be included on the
      same "printed page" as the copyright notice for easier
      identification within third-party archives.

   Copyright 2022 Colin Nash

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

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