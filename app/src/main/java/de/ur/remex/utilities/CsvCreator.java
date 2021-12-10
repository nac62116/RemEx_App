package de.ur.remex.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import de.ur.remex.Config;
import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;
import de.ur.remex.model.experiment.Survey;
import de.ur.remex.model.experiment.questionnaire.Question;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;

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

public class CsvCreator {

    private HashMap<String, String> questionMap;
    private ArrayList<String> questionKeys;

    // All commas (,) and stars (*) inside csv entries get eliminated as they are relevant for the csv structure
    // The comma separates the values and the star is a placeholder for "\n" because the saving process
    // in the internal storage cuts out all "\n"'s
    // Hash Structure: Key = surveyName + questionName; Value = csvLine
    // Csv Structure: VP_ID, VP_GROUP, SURVEY_NAME, QUESTION_NAME, ANSWER_CODE, TIME_STAMP

    public void initCsvMap(ArrayList<Survey> surveys, String vpId, String vpGroup) {
        questionMap = new HashMap<>();
        questionKeys = new ArrayList<>();
        vpId = vpId.replace(",", "").replace("*", "");
        vpGroup = vpGroup.replace(",", "").replace("*", "");

        for (Survey survey: surveys) {
            String surveyName = survey.getName().replace(",", "").replace("*", "");
            ArrayList<Step> steps = survey.getSteps();
            for (Step step: steps) {
                if (step.getType().equals(StepType.QUESTIONNAIRE)) {
                    Questionnaire questionnaire = (Questionnaire) step;
                    ArrayList<Question> questions = questionnaire.getQuestions();
                    for (Question question: questions) {
                        StringBuilder csvLine = new StringBuilder(vpId + "," + vpGroup + ",");
                        String questionName = question.getName().replace(",", "").replace("*", "");
                        csvLine.append(surveyName).append(",").append(questionName).append(",,*");
                        questionMap.put(surveyName + questionName, csvLine.toString());
                        questionKeys.add(surveyName + questionName);
                    }
                }
            }
        }
    }

    public void updateCsvMap(String surveyName, String questionName, String answer, String timeStamp) {
        surveyName = surveyName.replace(",", "").replace("*", "");
        questionName = questionName.replace(",", "").replace("*", "");
        answer = answer.replace(",", "").replace("*", "");
        timeStamp = timeStamp.replace(",", "").replace("*", "");
        String key = surveyName + questionName;
        String csvLine = questionMap.get(key);
        if (csvLine != null) {
            csvLine = csvLine.substring(0, csvLine.length() - 2);
            csvLine = csvLine + answer + "," + timeStamp + "*";
            questionMap.put(key, csvLine);
        }
    }

    public String getCsvString() {
        StringBuilder csv = new StringBuilder(Config.INITIAL_CSV_VALUE);
        for (String key: questionKeys) {
            csv.append(questionMap.get(key));
        }
        return csv.toString();
    }
}
