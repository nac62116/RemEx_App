package de.ur.remex.model.experiment;

import java.util.ArrayList;

public class ExperimentGroup {

    private String name;
    // Relevant to calculate absolute alarm times
    private long startTimeInMillis;
    private ArrayList<Survey> surveys;

    public ExperimentGroup(String name) {
        this.name = name;
        surveys = new ArrayList<>();
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void addSurvey(Survey survey) {
        surveys.add(survey);
    }

    public Survey getFirstSurvey() {
        if (!surveys.isEmpty()) {
            return surveys.get(0);
        }
        return null;
    }

    public ArrayList<Survey> getSurveys() {
        return surveys;
    }

    public String getName() {
        return name;
    }
}
