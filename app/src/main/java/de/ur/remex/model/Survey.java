package de.ur.remex.model;

import java.util.ArrayList;

public class Survey {

    private String name;
    // All start parameters are calculated relative to Experiment.startTimeInMillis
    private int relativeStartTimeInMin;
    private int absoluteStartAtHour;
    private int absoluteStartAtMinute;
    private int absoluteStartDaysOffset;
    private int maxDurationInMin;
    private ArrayList<Step> steps;

    public Survey(String name, int relativeStartTimeInMin, int maxDurationInMin) {
        this.name = name;
        this.relativeStartTimeInMin = relativeStartTimeInMin;
        this.maxDurationInMin = maxDurationInMin;
        steps = new ArrayList<>();
    }

    public Survey(String name, int absoluteStartAtHour, int absoluteStartAtMinute,
                  int absoluteStartDaysOffset, int maxDurationInMin) {
        this.name = name;
        this.absoluteStartAtHour = absoluteStartAtHour;
        this.absoluteStartAtMinute = absoluteStartAtMinute;
        this.absoluteStartDaysOffset = absoluteStartDaysOffset;
        this.maxDurationInMin = maxDurationInMin;
        steps = new ArrayList<>();
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
}
