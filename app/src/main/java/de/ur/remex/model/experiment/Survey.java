package de.ur.remex.model.experiment;

import java.util.ArrayList;

public class Survey {

    // Id must have an unique value excluding 0 -> RemEx Interface
    private int id;
    private String name;
    // Calculated relative to a given time (f.e. last survey finish, experimentStart, etc..)
    private long relativeStartTimeInMillis;
    private boolean isRelative;
    private int absoluteStartAtHour;
    private int absoluteStartAtMinute;
    // Offset relative to experiment start day
    private int absoluteStartDaysOffset;
    // Calculate min maxDuration
    private int maxDurationInMin;
    private int notificationDurationInMin;
    private Survey nextSurvey;
    private ArrayList<Step> steps;

    public Survey(String name, int relativeStartTimeInMillis, int maxDurationInMin, int notificationDurationInMin) {
        this.name = name;
        this.relativeStartTimeInMillis = relativeStartTimeInMillis;
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

    public void setNextSurvey(Survey nextSurvey) {
        this.nextSurvey = nextSurvey;
    }

    public Survey getNextSurvey() {
        return nextSurvey;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public long getRelativeStartTimeInMillis() {
        return relativeStartTimeInMillis;
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
        return steps.get(0);
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
