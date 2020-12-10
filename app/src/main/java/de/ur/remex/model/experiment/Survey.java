package de.ur.remex.model.experiment;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Survey {

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
    private Survey previousSurvey;
    private Survey nextSurvey;
    private ArrayList<Step> steps;

    public Survey(int id, String name, int relativeStartTimeInMillis, int maxDurationInMin) {
        this.id = id;
        this.name = name;
        this.relativeStartTimeInMillis = relativeStartTimeInMillis;
        isRelative = true;
        this.maxDurationInMin = maxDurationInMin;
        steps = new ArrayList<>();
    }

    public Survey(int id, String name, int absoluteStartAtHour, int absoluteStartAtMinute,
                  int absoluteStartDaysOffset, int maxDurationInMin) {
        this.id = id;
        this.name = name;
        this.absoluteStartAtHour = absoluteStartAtHour;
        this.absoluteStartAtMinute = absoluteStartAtMinute;
        this.absoluteStartDaysOffset = absoluteStartDaysOffset;
        isRelative = false;
        this.maxDurationInMin = maxDurationInMin;
        steps = new ArrayList<>();
    }

    public void setPreviousSurvey(Survey previousSurvey) {
        this.previousSurvey = previousSurvey;
    }

    public Survey getPreviousSurvey() {
        return previousSurvey;
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

    public int getId() {
        return id;
    }

    public Step getFirstStep() {
        return steps.get(0);
    }

    public int getMaxDurationInMin() {
        return maxDurationInMin;
    }

    public Step getStepById(int stepId) {
        for (Step step: steps) {
            if (step.getId() == stepId) {
                return step;
            }
        }
        return null;
    }
}
