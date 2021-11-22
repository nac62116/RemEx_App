package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Survey {

    @JsonProperty("id")
    private final int id;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("absoluteStartAtHour")
    private final int absoluteStartAtHour;
    @JsonProperty("absoluteStartAtMinute")
    private final int absoluteStartAtMinute;
    // Offset relative to experiment start day
    @JsonProperty("absoluteStartDaysOffset")
    private final int absoluteStartDaysOffset;
    @JsonProperty("maxDurationInMin")
    private final int maxDurationInMin;
    @JsonProperty("notificationDurationInMin")
    private final int notificationDurationInMin;
    @JsonProperty("nextSurveyId")
    private final int nextSurveyId;
    @JsonProperty("previousSurveyId")
    private final int previousSurveyId;
    @JsonProperty("steps")
    private final ArrayList<Step> steps;

    @JsonCreator
    public Survey(@JsonProperty("id") int id,
                  @JsonProperty("name") String name,
                  @JsonProperty("absoluteStartAtHour") int absoluteStartAtHour,
                  @JsonProperty("absoluteStartAtMinute") int absoluteStartAtMinute,
                  @JsonProperty("absoluteStartDaysOffset") int absoluteStartDaysOffset,
                  @JsonProperty("maxDurationInMin") int maxDurationInMin,
                  @JsonProperty("notificationDurationInMin") int notificationDurationInMin,
                  @JsonProperty("nextSurveyId") int nextSurveyId,
                  @JsonProperty("previousSurveyId") int previousSurveyId,
                  @JsonProperty("steps") ArrayList<Step> steps) {
        this.id = id;
        this.name = name;
        this.absoluteStartAtHour = absoluteStartAtHour;
        this.absoluteStartAtMinute = absoluteStartAtMinute;
        this.absoluteStartDaysOffset = absoluteStartDaysOffset;
        this.maxDurationInMin = maxDurationInMin;
        this.notificationDurationInMin = notificationDurationInMin;
        this.nextSurveyId = nextSurveyId;
        this.previousSurveyId = previousSurveyId;
        this.steps = steps;
    }

    public int getNextSurveyId() {
        return nextSurveyId;
    }
    
    public int getPreviousSurveyId() {
        return previousSurveyId;
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
            if (step.getPreviousStepId() == 0) {
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
