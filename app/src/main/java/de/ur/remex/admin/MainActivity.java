package de.ur.remex.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import de.ur.remex.R;
import de.ur.remex.controller.ExperimentController;
import de.ur.remex.model.experiment.breathingExercise.BreathingExercise;
import de.ur.remex.model.experiment.Experiment;
import de.ur.remex.model.experiment.Instruction;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.model.experiment.breathingExercise.BreathingMode;
import de.ur.remex.model.experiment.questionnaire.Answer;
import de.ur.remex.model.experiment.questionnaire.DateQuestion;
import de.ur.remex.model.experiment.questionnaire.DaysQuestion;
import de.ur.remex.model.experiment.questionnaire.DaytimeQuestion;
import de.ur.remex.model.experiment.questionnaire.HoursQuestion;
import de.ur.remex.model.experiment.questionnaire.LikertQuestion;
import de.ur.remex.model.experiment.questionnaire.MinutesQuestion;
import de.ur.remex.model.experiment.questionnaire.MultipleChoiceQuestion;
import de.ur.remex.model.experiment.questionnaire.Question;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;
import de.ur.remex.model.experiment.questionnaire.SingleChoiceQuestion;
import de.ur.remex.model.experiment.questionnaire.TextQuestion;

// General TODOS
// TODO: Disable Back Buttons

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button startExperimentButton;
    private Experiment experiment;
    private ExperimentController experimentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createExperiment();
        initViews();
    }

    private void initViews() {
        startExperimentButton = findViewById(R.id.startExperimentButton);
        startExperimentButton.setOnClickListener(this);
    }

    // TODO: The experiment object will be created by the RemEx Interface in the future
    private void createExperiment() {
        experiment = new Experiment("Test Experiment", 1);

        Survey survey1 = new Survey("Survey1 +1 Min", 0, 3);
        survey1.setId(1);
        Survey survey2 = new Survey("Survey2 +2 Min", 60 * 1000, 3);
        survey2.setId(2);

        // Building instruction steps
        ArrayList<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Instruction instruction = new Instruction();
            instruction.setId(i + 1);
            instruction.setHeader("header1_" + i);
            if (i == 1) {
                instruction.setText("Super, vielen Dank. Deine aktive Teilnahme wird vermerkt. Toll gemacht! Denke daran, dass die zwei Tage der Befragung am Smartphone nun vorbei sind und dein Besuch am Lehrstuhl für Kinder- und Jugendpsychiatrie und -psychotherapie bevorsteht. Super, vielen Dank. Deine aktive Teilnahme wird vermerkt. Toll gemacht! Jetzt gibt's noch ein paar weitere Fragen.");
            }
            else {
                instruction.setText("Super, vielen Dank. Deine aktive Teilnahme wird vermerkt. Toll gemacht! Denke daran, dass die zwei Tage der Befragung am Smartphone nun vorbei sind und dein Besuch am Lehrstuhl für Kinder- und Jugendpsychiatrie und -psychotherapie bevorsteht.");
            }
            if (i != 3) {
                instruction.setImageFileName("salivette1");
            }

            // Setting an ongoing instruction
            if (i == 0) {
                instruction.setDurationInMin(1);
                instruction.setWaitingText("Bitte warte noch wegen der ersten Instruktion.");
            }
            // Defining a step that has to wait for that instruction to finish
            if (i == 2) {
                instruction.setWaitForStep(1);
            }

            instructions.add(instruction);
        }
        // Building breathing exercises
        BreathingExercise breathingExercise = new BreathingExercise();
        breathingExercise.setId(6);
        breathingExercise.setMode(BreathingMode.MOVING_CIRCLE);
        breathingExercise.setInstructionHeader("Atemübung");
        breathingExercise.setInstructionText("Erklärung der Atemübung.");
        breathingExercise.setDischargeHeader("Atemübung");
        breathingExercise.setDischargeText("Verabschiedung der Atemübung.");
        breathingExercise.setDurationInMin(1);
        breathingExercise.setBreathingFrequencyInSec(5);

        // Building questionnaire
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(7);
        questionnaire.setInstructionText("Instruktion des Fragebogens");
        // Building questions
        // Text
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setName("textQuestion_0");
        textQuestion.setText("Wie hat sich das ganze angefühlt?");
        textQuestion.setHint("Warst du verägert, fröhlich, optimistisch, etc...");
        // Single choice
        SingleChoiceQuestion singleChoiceQuestion = new SingleChoiceQuestion();
        singleChoiceQuestion.setName("singleChoiceQuestion_0");
        singleChoiceQuestion.setText("Wie hat sich das ganze angefühlt?");
        singleChoiceQuestion.setHint("Warst du verägert, fröhlich, optimistisch, etc...");
        // Multiple choice
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
        multipleChoiceQuestion.setName("multipleChoiceQuestion_0");
        multipleChoiceQuestion.setText("Wie hat sich das ganze angefühlt?");
        multipleChoiceQuestion.setHint("Warst du verägert, fröhlich, optimistisch, etc...");
        // Minutes
        MinutesQuestion minutesQuestion = new MinutesQuestion();
        minutesQuestion.setName("minutesQuestion_0");
        minutesQuestion.setText("Wie lange hast du gebraucht?");
        minutesQuestion.setHint("Angabe in Minuten");
        // Likert
        LikertQuestion likertQuestion = new LikertQuestion();
        likertQuestion.setName("likertQuestion_0");
        likertQuestion.setText("Wie unangenehm war die Situation?");
        likertQuestion.setScaleMinimumLabel("Sehr unangenehm");
        likertQuestion.setScaleMaximumLabel("Gar nicht unangenehm");
        likertQuestion.setItemCount(5);
        likertQuestion.setInitialValue(1);
        // Hours
        HoursQuestion hoursQuestion = new HoursQuestion();
        hoursQuestion.setName("hoursQuestion_0");
        hoursQuestion.setText("Wie lange hast du gebraucht?");
        hoursQuestion.setHint("Angabe in Stunden");
        // Daytime
        DaytimeQuestion daytimeQuestion = new DaytimeQuestion();
        daytimeQuestion.setName("daytimeQuestion_0");
        daytimeQuestion.setText("Um wie viel Uhr bist du ins Bett gegangen?");
        // Days
        DaysQuestion daysQuestion = new DaysQuestion();
        daysQuestion.setName("daysQuestion_0");
        daysQuestion.setText("An wie vielen Tagen der letzten Woche hast du geraucht?");
        daysQuestion.setHint("Angabe in Tagen");
        // Date
        DateQuestion dateQuestion = new DateQuestion();
        dateQuestion.setName("dateQuestion_0");
        dateQuestion.setText("Wann hast du Geburtstag?");
        // Building answers
        // Single choice
        Answer answerS1 = new Answer();
        answerS1.setText("Verärgert");
        answerS1.setCode("1");
        answerS1.setNextQuestion(multipleChoiceQuestion);
        singleChoiceQuestion.addAnswer(answerS1);
        Answer answerS2 = new Answer();
        answerS2.setText("Fröhlich");
        answerS2.setCode("2");
        answerS2.setNextQuestion(dateQuestion);
        singleChoiceQuestion.addAnswer(answerS2);
        // Multiple choice
        Answer answerM1 = new Answer();
        answerM1.setText("Verärgert");
        answerM1.setCode("1");
        answerM1.setNextQuestion(minutesQuestion);
        multipleChoiceQuestion.addAnswer(answerM1);
        Answer answerM2 = new Answer();
        answerM2.setText("Fröhlich");
        answerM2.setCode("2");
        answerM2.setNextQuestion(minutesQuestion);
        multipleChoiceQuestion.addAnswer(answerM2);
        // Connecting questions together
        textQuestion.setNextQuestion(singleChoiceQuestion);
        minutesQuestion.setNextQuestion(likertQuestion);
        likertQuestion.setNextQuestion(hoursQuestion);
        hoursQuestion.setNextQuestion(daytimeQuestion);
        daytimeQuestion.setNextQuestion(daysQuestion);
        daysQuestion.setNextQuestion(dateQuestion);
        dateQuestion.setNextQuestion(null);
        // Adding questions to questionnaire
        questionnaire.addQuestion(textQuestion);
        questionnaire.addQuestion(singleChoiceQuestion);
        questionnaire.addQuestion(multipleChoiceQuestion);
        questionnaire.addQuestion(minutesQuestion);
        questionnaire.addQuestion(likertQuestion);
        questionnaire.addQuestion(hoursQuestion);
        questionnaire.addQuestion(daytimeQuestion);
        questionnaire.addQuestion(daysQuestion);
        questionnaire.addQuestion(dateQuestion);

        // Filling surveys with steps
        for (int i = 0; i < instructions.size(); i++) {
            Instruction currInstruction = instructions.get(i);
            Instruction nextInstruction;
            if (i == instructions.size() - 1) {
                currInstruction.setNextStep(breathingExercise);
                breathingExercise.setNextStep(questionnaire);
                questionnaire.setNextStep(null);
            }
            else {
                nextInstruction = instructions.get(i + 1);
                currInstruction.setNextStep(nextInstruction);
            }
            survey1.addStep(currInstruction);
            survey2.addStep(currInstruction);
            // Adding Breathing exercise and questionnaire
            if (i == instructions.size() - 1) {
                survey1.addStep(breathingExercise);
                survey1.addStep(questionnaire);
                survey2.addStep(breathingExercise);
                survey2.addStep(questionnaire);
            }
        }

        survey1.setNextSurvey(survey2);
        survey2.setNextSurvey(null);

        experiment.addSurvey(survey1);
        experiment.addSurvey(survey2);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(startExperimentButton)) {
            if (experimentController != null) {
                experimentController.stopExperiment();
            }
            experimentController = new ExperimentController(this);
            experimentController.startExperiment(experiment);
        }
    }
}