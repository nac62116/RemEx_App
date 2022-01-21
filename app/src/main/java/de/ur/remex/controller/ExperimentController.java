package de.ur.remex.controller;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import de.ur.remex.admin.AdminActivity;
import de.ur.remex.admin.LoginActivity;
import de.ur.remex.model.experiment.ExperimentGroup;
import de.ur.remex.model.experiment.Instruction;
import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.model.experiment.breathingExercise.BreathingExercise;
import de.ur.remex.model.experiment.questionnaire.ChoiceType;
import de.ur.remex.model.experiment.questionnaire.LikertQuestion;
import de.ur.remex.model.experiment.questionnaire.PointOfTimeQuestion;
import de.ur.remex.model.experiment.questionnaire.Question;
import de.ur.remex.model.experiment.questionnaire.QuestionType;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;
import de.ur.remex.model.experiment.questionnaire.ChoiceQuestion;
import de.ur.remex.model.experiment.questionnaire.TextQuestion;
import de.ur.remex.model.experiment.questionnaire.TimeIntervalQuestion;
import de.ur.remex.model.storage.InternalStorage;
import de.ur.remex.utilities.AlarmReceiver;
import de.ur.remex.utilities.AppKillCallbackService;
import de.ur.remex.utilities.CsvCreator;
import de.ur.remex.utilities.Event;
import de.ur.remex.Config;
import de.ur.remex.utilities.AlarmScheduler;
import de.ur.remex.utilities.NotificationHandler;
import de.ur.remex.view.BreathingExerciseActivity;
import de.ur.remex.view.LikertQuestionActivity;
import de.ur.remex.view.PointOfTimeQuestionActivity;
import de.ur.remex.view.InstructionActivity;
import de.ur.remex.view.ChoiceQuestionActivity;
import de.ur.remex.view.SurveyEntranceActivity;
import de.ur.remex.view.TextQuestionActivity;
import de.ur.remex.view.TimeIntervalQuestionActivity;
import de.ur.remex.view.WaitingRoomActivity;

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

public class ExperimentController implements Observer {

    // Current model
    private ExperimentGroup currentExperimentGroup;
    // Current state
    private int currentSurveyId;
    private int currentStepId;
    private int currentQuestionId;
    private boolean userIsWaiting;
    // Current activity context (View)
    private Context currentContext;
    // Utilities
    private CsvCreator csvCreator;

    public ExperimentController(Context context) {
        // Initial state
        currentContext = context;
        userIsWaiting = false;
        // Registering as observer within the observables
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        InstructionActivity instructionActivity = new InstructionActivity();
        BreathingExerciseActivity breathingExerciseActivity = new BreathingExerciseActivity();
        WaitingRoomActivity waitingRoomActivity = new WaitingRoomActivity();
        SurveyEntranceActivity surveyEntranceActivity = new SurveyEntranceActivity();
        AdminActivity adminActivity = new AdminActivity();
        ChoiceQuestionActivity choiceQuestionActivity = new ChoiceQuestionActivity();
        TextQuestionActivity textQuestionActivity = new TextQuestionActivity();
        PointOfTimeQuestionActivity pointOfTimeQuestionActivity = new PointOfTimeQuestionActivity();
        TimeIntervalQuestionActivity timeIntervalQuestionActivity = new TimeIntervalQuestionActivity();
        LikertQuestionActivity likertQuestionActivity = new LikertQuestionActivity();
        AppKillCallbackService service = new AppKillCallbackService();
        instructionActivity.addObserver(this);
        breathingExerciseActivity.addObserver(this);
        surveyEntranceActivity.addObserver(this);
        alarmReceiver.addObserver(this);
        waitingRoomActivity.addObserver(this);
        adminActivity.addObserver(this);
        choiceQuestionActivity.addObserver(this);
        textQuestionActivity.addObserver(this);
        pointOfTimeQuestionActivity.addObserver(this);
        timeIntervalQuestionActivity.addObserver(this);
        likertQuestionActivity.addObserver(this);
        service.addObserver(this);
    }

    // Entry point from AdminActivity
    public void startExperiment(ExperimentGroup experimentGroup, long startTimeInMs) {
        InternalStorage storage = new InternalStorage(currentContext);
        // Start service to receive a callback, when the user kills the app by swiping it in the recent apps list.
        currentContext.startService(new Intent(currentContext, AppKillCallbackService.class));
        // Cancel possible ongoing alarms
        AlarmScheduler alarmScheduler = new AlarmScheduler(currentContext);
        alarmScheduler.cancelAllAlarms();
        // Set internal storage values
        storage.saveFileContentString(Config.FILE_NAME_EXPERIMENT_STATUS, Config.EXPERIMENT_RUNNING);
        storage.saveFileContentString(Config.FILE_NAME_CSV_STATUS, Config.CSV_NOT_SAVED);
        // Init CSV
        String vpId = storage.getFileContentString(Config.FILE_NAME_ID);
        String vpGroup = storage.getFileContentString(Config.FILE_NAME_GROUP);
        csvCreator = new CsvCreator();
        csvCreator.initCsvMap(experimentGroup.getSurveys(), vpId, vpGroup);
        storage.saveFileContentString(Config.FILE_NAME_CSV, Config.INITIAL_CSV_VALUE);
        // Init current state
        currentExperimentGroup = experimentGroup;
        currentExperimentGroup.setStartTimeInMillis(startTimeInMs);
        Survey currentSurvey = currentExperimentGroup.getFirstSurvey();
        currentSurveyId = currentSurvey.getId();
        setSurveyAlarm(currentSurvey, alarmScheduler);
        // Inform user
        Toast toast = Toast.makeText(currentContext, Config.EXPERIMENT_STARTED_TOAST, Toast.LENGTH_LONG);
        toast.show();
    }

    private void setSurveyAlarm(Survey survey, AlarmScheduler alarmScheduler) {
        alarmScheduler.setAbsoluteSurveyAlarm(survey.getId(),
                currentExperimentGroup.getStartTimeInMillis(),
                survey.getAbsoluteStartAtHour(),
                survey.getAbsoluteStartAtMinute(),
                survey.getAbsoluteStartDaysOffset());
    }

    @Override
    public void update(Observable o, Object arg) {
        Event event = (Event) arg;
        if (event.getContext() != null) {
            currentContext = event.getContext();
        }
        // Preparing utilities
        AlarmScheduler alarmScheduler = new AlarmScheduler(currentContext);
        NotificationHandler notificationHandler = new NotificationHandler(currentContext);
        InternalStorage storage = new InternalStorage(currentContext);
        Calendar calendar = Calendar.getInstance();
        // Preparing current state
        Survey currentSurvey = getCurrentSurvey();
        Step currentStep;
        // Checking event type
        switch (event.getType()) {

            case Config.EVENT_SURVEY_ALARM:
                // Making survey accessible via AdminActivity (App Launcher)
                storage.saveFileContentString(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_OPENED);
                // Creating notification
                notificationHandler.sendNotification();
                // Setting timer to close the AdminActivity (App Launcher) entrance after the notification expired
                alarmScheduler.setNotificationTimeoutAlarm(currentSurvey.getNotificationDurationInMin());
                break;

            case Config.EVENT_NOTIFICATION_TIMEOUT:
                // Cancel Notification
                notificationHandler.cancelNotification();
                // Closing the AdminActivity (App Launcher) entrance
                storage.saveFileContentString(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
                // Prepare next survey
                prepareNextSurvey(currentSurvey, storage, alarmScheduler);
                break;

            case Config.EVENT_SURVEY_STARTED:
                // As the survey got started the notification timeout is not relevant anymore
                alarmScheduler.cancelNotificationTimeoutAlarm();
                // Each survey has a maximum duration. The timeout alarm is set here
                alarmScheduler.setSurveyTimeoutAlarm(currentSurvey.getId(), currentSurvey.getMaxDurationInMin());
                // Beginning with the first step of the survey
                currentStep = currentSurvey.getFirstStep();
                currentStepId = currentStep.getId();
                navigateToStep(currentStep);
                break;

            case Config.EVENT_NEXT_STEP:
                currentStep = getCurrentStep();
                switchToNextStep(currentSurvey, currentStep, storage, alarmScheduler);
                break;

            case Config.EVENT_NEXT_QUESTION:
                currentStep = getCurrentStep();
                Question nextQuestion = getNextQuestion(currentSurvey, currentStep, event, calendar);
                if (nextQuestion != null) {
                    currentQuestionId = nextQuestion.getId();
                    navigateToQuestion(nextQuestion);
                }
                else {
                    switchToNextStep(currentSurvey, currentStep, storage, alarmScheduler);
                }
                break;

            case Config.EVENT_STEP_TIMER:
                // The ongoing instruction is finished (see example in setStepTimer())
                int stepId = (int) event.getData();
                Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(stepId);
                // Boolean value that gets checked when the user reaches the point in the survey,
                // that has to wait for this ongoing instruction
                instructionToWaitFor.setFinished(true);
                // If the user already reached the above explained point, he/she gets redirected automatically
                if (userIsWaiting) {
                    userIsWaiting = false;
                    currentStep = getCurrentStep();
                    navigateToStep(currentStep);
                }
                break;

            case Config.EVENT_SURVEY_TIMEOUT:
                Toast toast = Toast.makeText(currentContext, Config.SURVEY_TIMEOUT_TOAST, Toast.LENGTH_LONG);
                toast.show();
                finishSurvey(currentSurvey, storage, alarmScheduler);
                break;

            case Config.EVENT_CSV_REQUEST:
                // When the csv is requested the CsvCreator class converts its whole Hashmap into a csvString
                // which is then saved in the internal storage.
                saveCsvInInternalStorage(storage);
                break;

            case Config.EVENT_APP_KILLED:
                // This event is triggered when the user swipes the app away in the recent apps list.
                // As a result the currentSurvey gets cancelled, the next survey is scheduled and the app exits.
                if (currentStepId != 0) {
                    finishSurvey(currentSurvey, storage, alarmScheduler);
                }
                break;

            default:
                break;
        }
    }

    private Survey getCurrentSurvey() {
        return currentExperimentGroup.getSurveyById(currentSurveyId);
    }

    private void prepareNextSurvey(Survey currentSurvey, InternalStorage storage,
                                   AlarmScheduler alarmScheduler) {
        Survey nextSurvey = currentExperimentGroup.getSurveyById(currentSurvey.getNextSurveyId());
        if (nextSurvey != null) {
            currentSurveyId = nextSurvey.getId();
            setSurveyAlarm(nextSurvey, alarmScheduler);
        }
        else {
            finishExperiment(storage);
        }
    }

    private void finishExperiment(InternalStorage storage) {
        currentContext.stopService(new Intent(currentContext, AppKillCallbackService.class));
        currentSurveyId = 0;
        storage.saveFileContentString(Config.FILE_NAME_EXPERIMENT_STATUS, Config.EXPERIMENT_FINISHED);
    }

    private void navigateToStep(Step step) {
        if (step.getType().equals(StepType.INSTRUCTION)) {
            Instruction instruction = (Instruction) step;
            Intent intent = new Intent(currentContext, InstructionActivity.class);
            intent.putExtra(Config.INSTRUCTION_HEADER_KEY, instruction.getHeader());
            intent.putExtra(Config.INSTRUCTION_TEXT_KEY, instruction.getText());
            intent.putExtra(Config.INSTRUCTION_IMAGE_KEY, instruction.getImageFileName());
            intent.putExtra(Config.INSTRUCTION_VIDEO_KEY, instruction.getVideoFileName());
            currentContext.startActivity(intent);
        }
        else if (step.getType().equals(StepType.BREATHING_EXERCISE)) {
            BreathingExercise breathingExercise = (BreathingExercise) step;
            Intent intent = new Intent(currentContext, BreathingExerciseActivity.class);
            intent.putExtra(Config.BREATHING_MODE_KEY, breathingExercise.getMode());
            intent.putExtra(Config.BREATHING_DURATION_KEY, breathingExercise.getDurationInMin());
            intent.putExtra(Config.BREATHING_FREQUENCY_KEY, breathingExercise.getBreathingFrequencyInSec());
            currentContext.startActivity(intent);
        }
        else if (step.getType().equals(StepType.QUESTIONNAIRE)) {
            Questionnaire questionnaire = (Questionnaire) step;
            Question currentQuestion = questionnaire.getFirstQuestion();
            currentQuestionId = currentQuestion.getId();
            navigateToQuestion(currentQuestion);
        }
    }

    private void navigateToQuestion(Question question) {
        Intent intent = new Intent();
        if (question.getType().equals(QuestionType.CHOICE)) {
            ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
            intent = new Intent(currentContext, ChoiceQuestionActivity.class);
            intent.putExtra(Config.ANSWER_TEXTS_KEY, choiceQuestion.getAnswerTexts());
            intent.putExtra(Config.CHOICE_TYPE_KEY, choiceQuestion.getChoiceType());
        }
        else if (question.getType().equals(QuestionType.TEXT)) {
            intent = new Intent(currentContext, TextQuestionActivity.class);
        }
        else if (question.getType().equals(QuestionType.POINT_OF_TIME)) {
            PointOfTimeQuestion pointOfTimeQuestion = (PointOfTimeQuestion) question;
            intent = new Intent(currentContext, PointOfTimeQuestionActivity.class);
            intent.putExtra(Config.POINT_OF_TIME_TYPES_KEY, pointOfTimeQuestion.getPointOfTimeTypeNames());
        }
        else if (question.getType().equals(QuestionType.TIME_INTERVAL)) {
            TimeIntervalQuestion timeIntervalQuestion = (TimeIntervalQuestion) question;
            intent = new Intent(currentContext, TimeIntervalQuestionActivity.class);
            intent.putExtra(Config.TIME_INTERVALL_TYPES_KEY, timeIntervalQuestion.getTimeIntervalTypeNames());
        }
        else if (question.getType().equals(QuestionType.LIKERT)) {
            LikertQuestion likertQuestion = (LikertQuestion) question;
            intent = new Intent(currentContext, LikertQuestionActivity.class);
            intent.putExtra(Config.SCALE_LABEL_MIN_KEY, likertQuestion.getScaleMinimumLabel());
            intent.putExtra(Config.SCALE_LABEL_MAX_KEY, likertQuestion.getScaleMaximumLabel());
            intent.putExtra(Config.INITIAL_SCALE_VALUE_KEY, likertQuestion.getInitialValue());
            intent.putExtra(Config.SCALE_ITEM_COUNT_KEY, likertQuestion.getItemCount());
        }
        intent.putExtra(Config.QUESTION_TEXT_KEY, question.getText());
        intent.putExtra(Config.QUESTION_HINT_KEY, question.getHint());
        currentContext.startActivity(intent);
    }

    private Step getCurrentStep() {
        return currentExperimentGroup.getSurveyById(currentSurveyId).getStepById(currentStepId);
    }

    private void switchToNextStep(Survey currentSurvey, Step currentStep, InternalStorage storage,
                                  AlarmScheduler alarmScheduler) {
        // Set step timer if the current step was an ongoing step (see setStepTimer() for an example)
        setStepTimer(currentStep, alarmScheduler);
        // Switching to next step
        Step nextStep = currentSurvey.getStepById(currentStep.getNextStepId());
        // There is a next step
        if (nextStep != null) {
            currentStepId = nextStep.getId();
            // Moving on to next step after a little checkup (Explanation inside the method)
            checkWaitingRequestAndNavigateToStep(currentSurvey, nextStep);
        }
        // There is no next step and the survey gets finished
        else {
            updateProgress(storage);
            finishSurvey(currentSurvey, storage, alarmScheduler);
        }
    }

    private void setStepTimer(Step step, AlarmScheduler alarmScheduler) {
        /* Description:
                Checking if the step that was finished just now is an ongoing step.
                That means that another step waits for the completion of this step.
                f.e.:
                - Instruction ("Please take the cotton bud into your mouth and continue answering our questions.")
                    -> Note: The cotton bud in this example has to be inside the mouth for at least 2 minutes to get good results
                - Several other Steps (Questionnaires, Breathing Exercises, Instructions, ...)
                - Instruction ("Please take the cotton bud out of your mouth and put it in the fridge")
                    -> Here we have to check if the "cotton bud"-Instruction is already finished (2 minutes passed)
                */
        // If the step is ongoing we set a step timer and pass the step name to it
        if (step.getType().equals(StepType.INSTRUCTION)) {
            Instruction instructionStep = (Instruction) step;
            if (instructionStep.getDurationInMin() != 0) {
                alarmScheduler.setStepTimer(instructionStep.getId(), instructionStep.getDurationInMin());
            }
        }
    }

    private void checkWaitingRequestAndNavigateToStep(Survey currentSurvey, Step step) {
        // Here we're checking if the next step waits for another step,
        // like in the "cotton-bud"-example in "setStepTimer()" explained.
        if (step.getWaitForStep() != 0) {
            Instruction instructionToWaitFor = (Instruction) currentSurvey.getStepById(step.getWaitForStep());
            // The next step waits for the instruction "instructionToWaitFor".
            // If its not finished yet (in our "cotton-bud"-example 2 minutes haven't passed)
            // the user gets directed to the WaitingRoomActivity
            if (!instructionToWaitFor.isFinished()) {
                userIsWaiting = true;
                Intent intent = new Intent(currentContext, WaitingRoomActivity.class);
                intent.putExtra(Config.WAITING_ROOM_TEXT_KEY, instructionToWaitFor.getWaitingText());
                currentContext.startActivity(intent);
            }
            else {
                navigateToStep(step);
            }
        }
        else {
            navigateToStep(step);
        }
    }

    private void updateProgress(InternalStorage storage) {
        String progressString = storage.getFileContentString(Config.FILE_NAME_PROGRESS);
        int progress = Integer.parseInt(progressString) + 1;
        storage.saveFileContentString(Config.FILE_NAME_PROGRESS, Integer.toString(progress));
    }

    private void finishSurvey(Survey currentSurvey, InternalStorage storage,
                              AlarmScheduler alarmScheduler) {
        currentStepId = 0;
        alarmScheduler.cancelAllAlarms();
        prepareNextSurvey(currentSurvey, storage, alarmScheduler);
        storage.saveFileContentString(Config.FILE_NAME_SURVEY_ENTRANCE, Config.SURVEY_ENTRANCE_CLOSED);
        Intent intent = new Intent(currentContext, LoginActivity.class);
        intent.putExtra(Config.EXIT_APP_KEY, true);
        currentContext.startActivity(intent);
    }

    private Question getNextQuestion(Survey currentSurvey, Step currentStep,
                                     Event event, Calendar calendar) {
        Questionnaire currentQuestionnaire = (Questionnaire) currentStep;
        Question currentQuestion = currentQuestionnaire.getQuestionById(currentQuestionId);
        Question nextQuestion = null;
        if (currentQuestion.getType().equals(QuestionType.CHOICE)) {
            ChoiceQuestion choiceQuestion = (ChoiceQuestion) currentQuestion;
            if (choiceQuestion.getChoiceType().equals(ChoiceType.SINGLE_CHOICE)) {
                String answerText = (String) event.getData();
                String answerCode = choiceQuestion.getCodeByAnswerText(answerText);
                csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                        answerCode, calendar.getTime().toString());
                nextQuestion = currentQuestionnaire.getQuestionById(choiceQuestion.getNextQuestionIdByAnswerText(answerText));
            }
            else {
                @SuppressWarnings("unchecked")
                ArrayList<String> answerTexts = (ArrayList<String>) event.getData();
                StringBuilder answerCode = new StringBuilder();
                for (String answerText: answerTexts) {
                    answerCode.append(choiceQuestion.getCodeByAnswerText(answerText));
                }
                csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                        answerCode.toString(), calendar.getTime().toString());
                nextQuestion = currentQuestionnaire.getQuestionById(choiceQuestion.getNextQuestionId());
            }
        }
        else if (currentQuestion.getType().equals(QuestionType.TEXT)) {
            TextQuestion textQuestion = (TextQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(textQuestion.getNextQuestionId());
        }
        else if (currentQuestion.getType().equals(QuestionType.POINT_OF_TIME)) {
            PointOfTimeQuestion pointOfTimeQuestion = (PointOfTimeQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(pointOfTimeQuestion.getNextQuestionId());
        }
        else if (currentQuestion.getType().equals(QuestionType.TIME_INTERVAL)) {
            TimeIntervalQuestion timeIntervalQuestion = (TimeIntervalQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(timeIntervalQuestion.getNextQuestionId());
        }
        else if (currentQuestion.getType().equals(QuestionType.LIKERT)) {
            LikertQuestion likertQuestion = (LikertQuestion) currentQuestion;
            String answerText = (String) event.getData();
            csvCreator.updateCsvMap(currentSurvey.getName(), currentQuestion.getName(),
                    answerText, calendar.getTime().toString());
            nextQuestion = currentQuestionnaire.getQuestionById(likertQuestion.getNextQuestionId());
        }
        return nextQuestion;
    }

    private void saveCsvInInternalStorage(InternalStorage storage) {
        String csv = csvCreator.getCsvString();
        storage.saveFileContentString(Config.FILE_NAME_CSV, csv);
    }
}
