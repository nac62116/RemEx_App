package de.ur.remex.model.experiment.questionnaire;

import java.util.ArrayList;

public class TimeIntervallQuestion extends Question {

    private int nextQuestionId;
    // Has to contain at least one of TimeIntervallType.YEARS/MONTHS/DAYS/HOURS/MINUTES/SECONDS
    private ArrayList<TimeIntervallType> timeIntervallTypes;

    public TimeIntervallQuestion() {
        type = QuestionType.TIME_INTERVALL;
        timeIntervallTypes = new ArrayList<>();
    }

    public ArrayList<String> getTimeIntervallTypeNames() {
        ArrayList<String> timeIntervallTypeNames = new ArrayList<>();
        for (TimeIntervallType timeIntervallType: timeIntervallTypes) {
            timeIntervallTypeNames.add(timeIntervallType.name());
        }
        return timeIntervallTypeNames;
    }

    public void addTimeIntervallType(TimeIntervallType timeIntervallType) {
        if (!timeIntervallTypes.contains(timeIntervallType)) {
            timeIntervallTypes.add(timeIntervallType);
        }
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(int nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }
}
