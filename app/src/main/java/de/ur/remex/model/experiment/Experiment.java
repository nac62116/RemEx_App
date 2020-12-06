package de.ur.remex.model.experiment;

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

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void addSurvey(Survey survey) {
        surveys.add(survey);
    }

    public Survey getSurveyById(int id) {
        for (Survey survey: surveys) {
            if (survey.getId() == id) {
                return survey;
            }
        }
        return null;
    }

    public Survey getFirstSurvey() {
        for (Survey survey: surveys) {
            if (survey.getPreviousSurvey() == null) {
                return survey;
            }
        }
        return null;
    }

    public int getNotificationDurationInMin() {
        return notificationDurationInMin;
    }

    public void setNotificationDurationInMin(int notificationDurationInMin) {
        this.notificationDurationInMin = notificationDurationInMin;
    }
}
