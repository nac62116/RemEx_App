package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Survey {

    // Id must have an unique value excluding 0 -> RemEx Interface
    private final int id;
    // Has to be unique
    private final String name;
    // Calculated relative to a given time (f.e. last survey finish, experimentStart, etc..)
    private final int relativeStartTimeInMin;
    private final boolean isRelative;
    private final int absoluteStartAtHour;
    private final int absoluteStartAtMinute;
    // Offset relative to experiment start day
    private final int absoluteStartDaysOffset;
    // Calculate min maxDuration
    private final int maxDurationInMin;
    private final int notificationDurationInMin;
    private final int nextSurveyId;
    private final ArrayList<Step> steps;

    @JsonCreator
    public Survey(@JsonProperty("id") int id,
                  @JsonProperty("name") String name,
                  @JsonProperty("relativeStartTimeInMin") int relativeStartTimeInMin,
                  @JsonProperty("absoluteStartAtHour") int absoluteStartAtHour,
                  @JsonProperty("absoluteStartAtMinute") int absoluteStartAtMinute,
                  @JsonProperty("absoluteStartDaysOffset") int absoluteStartDaysOffset,
                  @JsonProperty("maxDurationInMin") int maxDurationInMin,
                  @JsonProperty("notificationDurationInMin") int notificationDurationInMin,
                  @JsonProperty("isRelative") boolean isRelative,
                  @JsonProperty("nextSurveyId") int nextSurveyId,
                  @JsonProperty("previousSurveyId") int previousSurveyId,
                  @JsonProperty("steps") ArrayList<Step> steps) {
        this.id = id;
        this.name = name;
        this.relativeStartTimeInMin = relativeStartTimeInMin;
        this.absoluteStartAtHour = absoluteStartAtHour;
        this.absoluteStartAtMinute = absoluteStartAtMinute;
        this.absoluteStartDaysOffset = absoluteStartDaysOffset;
        this.maxDurationInMin = maxDurationInMin;
        this.notificationDurationInMin = notificationDurationInMin;
        this.isRelative = isRelative;
        this.nextSurveyId = nextSurveyId;
        this.previousSurveyId = previousSurveyId;
        this.steps = steps;
    }

    public int getNextSurveyId() {
        return nextSurveyId;
    }
    
    public int getPreviousSurveyId() {
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

    public String getName() {
        return name;
    }

    public Step getFirstStep() {
        for (Step step: steps) {
            if (step.getPreviousStepId() == null) {
                return step;
            }
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

    public int getNotificationDurationInMin() {
        return notificationDurationInMin;
    }
}
