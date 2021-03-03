package de.ur.remex.model.experiment;

import java.util.ArrayList;

public class Survey {

    // Id must have an unique value excluding 0 -> RemEx Interface
    private int id;
    // Has to be unique
    private String name;
    // Calculated relative to a given time (f.e. last survey finish, experimentStart, etc..)
    private int relativeStartTimeInMin;
    private boolean isRelative;
    private int absoluteStartAtHour;
    private int absoluteStartAtMinute;
    // Offset relative to experiment start day
    private int absoluteStartDaysOffset;
    // Calculate min maxDuration
    private int maxDurationInMin;
    private int notificationDurationInMin;
    private int nextSurveyId;
    private ArrayList<Step> steps;

    public Survey(String name, int relativeStartTimeInMin, int maxDurationInMin, int notificationDurationInMin) {
        this.name = name;
        this.relativeStartTimeInMin = relativeStartTimeInMin;
        this.notificationDurationInMin = notificationDurationInMin;
        isRelative = true;
        this.maxDurationInMin = maxDurationInMin;
        steps = new ArrayList<>();
    }

    public Survey(String name, int absoluteStartAtHour, int absoluteStartAtMinute,
                  int absoluteStartDaysOffset, int maxDurationInMin, int notificationDurationInMin) {
        this.name = name;
        this.absoluteStartAtHour = absoluteStartAtHour;
        this.absoluteStartAtMinute = absoluteStartAtMinute;
        this.absoluteStartDaysOffset = absoluteStartDaysOffset;
        this.notificationDurationInMin = notificationDurationInMin;
        isRelative = false;
        this.maxDurationInMin = maxDurationInMin;
        steps = new ArrayList<>();
    }

    public void setNextSurveyId(int nextSurveyId) {
        this.nextSurveyId = nextSurveyId;
    }

    public int getNextSurveyId() {
        return nextSurveyId;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public int getRelativeStartTimeInMin() {
        return relativeStartTimeInMin;
    }

    public int getAbsoluteStartAtHour() {
        return absoluteStartAtHour;
    }

    public int getAbsoluteStartAtMinute() {
        return absoluteStartAtMinute;
    }

    public int getAbsoluteStartDaysOffset() {
        return absoluteStartDaysOffset;
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public String getName() {
        return name;
    }

    public Step getFirstStep() {
        if (!steps.isEmpty()) {
            return steps.get(0);
        }
        return null;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getMaxDurationInMin() {
        return maxDurationInMin;
    }

    public Step getStepById(int id) {
        for (Step step: steps) {
            if (step.getId() == id) {
                return step;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotificationDurationInMin() {
        return notificationDurationInMin;
    }
}
