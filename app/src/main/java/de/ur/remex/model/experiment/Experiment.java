package de.ur.remex.model.experiment;

import java.util.ArrayList;

public class Experiment {

    private String name;
    private long startTimeInMillis;
    private int notificationDurationInMin;
    private ArrayList<Survey> surveys;

    public Experiment(String name, int notificationDurationInMin) {
        this.name = name;
        this.notificationDurationInMin = notificationDurationInMin;
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

    public int getNotificationDurationInMin() {
        return notificationDurationInMin;
    }

    public void setNotificationDurationInMin(int notificationDurationInMin) {
        this.notificationDurationInMin = notificationDurationInMin;
    }
}
