package de.ur.remex.model;

import java.util.ArrayList;

public class Experiment {

    private String name;
    private String group;
    private long startTimeInMillis;
    private int notificationDurationInMin;
    private ArrayList<Survey> surveys;

    public Experiment(String name, String group, int notificationDurationInMin) {
        this.name = name;
        this.group = group;
        this.notificationDurationInMin = notificationDurationInMin;
        surveys = new ArrayList<>();
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public void addSurvey(Survey survey) {
        surveys.add(survey);
    }

    public Survey getSurveyByName(String name) {
        for (Survey survey: surveys) {
            if (survey.getName().equals(name)) {
                return survey;
            }
        }
        return null;
    }
}
