package de.ur.remex.utilities;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import de.ur.remex.Config;
import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.model.experiment.questionnaire.Question;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;
import de.ur.remex.model.storage.InternalStorage;

public class CsvCreator {

    private HashMap<String, String> questionMap;
    private ArrayList<String> questionKeys;

    // Hash Structure: Key = questionName.replace(",", ""); Value = csvLine
    // Csv Structure: VP_ID, VP_GROUP, SURVEY_NAME, QUESTION_NAME, ANSWER_CODE, TIME_STAMP
    public void initCsv(ArrayList<Survey> surveys, String vpId, String vpGroup) {
        questionMap = new HashMap<>();
        questionKeys = new ArrayList<>();
        StringBuilder csvLine = new StringBuilder(vpId + "," + vpGroup + ",");

        for (Survey survey: surveys) {
            String surveyName = survey.getName().replace(",", "");
            ArrayList<Step> steps = survey.getSteps();
            for (Step step: steps) {
                if (step.getType().equals(StepType.QUESTIONNAIRE)) {
                    Questionnaire questionnaire = (Questionnaire) step;
                    ArrayList<Question> questions = questionnaire.getQuestions();
                    for (Question question: questions) {
                        String questionName = question.getName().replace(",", "");
                        csvLine.append(surveyName).append(",").append(questionName).append(",,\n");
                        questionMap.put(questionName, csvLine.toString());
                        questionKeys.add(questionName);
                    }
                }
            }
        }
    }

    public void updateCsv(String questionName, String answer, String timeStamp) {
        String key = questionName.replace(",", "");
        String csvLine = questionMap.get(key);
        csvLine = csvLine.substring(0, csvLine.length() - 3);
        csvLine = csvLine + answer.replace(",", "") + "," +
                timeStamp.replace(",", "") + "\n";
        questionMap.put(key, csvLine);
    }

    public String getCsv() {
        StringBuilder csv = new StringBuilder("VP_ID,VP_GROUP,SURVEY_NAME,QUESTION_NAME,ANSWER_CODE,TIME_STAMP\n");
        for (String key: questionKeys) {
            csv.append(questionMap.get(key));
        }
        return csv.toString();
    }
}
