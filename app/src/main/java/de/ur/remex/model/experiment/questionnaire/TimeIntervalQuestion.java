package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeIntervalQuestion extends Question {

    // Has to contain at least one of TimeIntervalType.YEARS/MONTHS/DAYS/HOURS/MINUTES/SECONDS
    @JsonProperty("timeIntervalTypes")
    private final ArrayList<TimeIntervalType> timeIntervalTypes;

    @JsonCreator
    public TimeIntervalQuestion(@JsonProperty("id") int id,
                                @JsonProperty("name") String name,
                                @JsonProperty("text") String text,
                                @JsonProperty("hint") String hint,
                                @JsonProperty("nextQuestionId") int nextQuestionId,
                                @JsonProperty("previousQuestionId") int previousQuestionId,
                                @JsonProperty("timeIntervalTypes") ArrayList<String> timeIntervalTypes) {
        this.id = id;
        this.type = QuestionType.TIME_INTERVAL;
        this.name = name;
        this.text = text;
        this.hint = hint;
        this.nextQuestionId = nextQuestionId;
        this.previousQuestionId = previousQuestionId;
        this.timeIntervalTypes = new ArrayList<>();
        if (timeIntervalTypes.contains(TimeIntervalType.YEARS.name())) {
            this.timeIntervalTypes.add(TimeIntervalType.YEARS);
        }
        if (timeIntervalTypes.contains(TimeIntervalType.MONTHS.name())) {
            this.timeIntervalTypes.add(TimeIntervalType.MONTHS);
        }
        if (timeIntervalTypes.contains(TimeIntervalType.DAYS.name())) {
            this.timeIntervalTypes.add(TimeIntervalType.DAYS);
        }
        if (timeIntervalTypes.contains(TimeIntervalType.HOURS.name())) {
            this.timeIntervalTypes.add(TimeIntervalType.HOURS);
        }
        if (timeIntervalTypes.contains(TimeIntervalType.MINUTES.name())) {
            this.timeIntervalTypes.add(TimeIntervalType.MINUTES);
        }
        if (timeIntervalTypes.contains(TimeIntervalType.SECONDS.name())) {
            this.timeIntervalTypes.add(TimeIntervalType.SECONDS);
        }
    }

    public ArrayList<String> getTimeIntervalTypeNames() {
        ArrayList<String> timeIntervalTypeNames = new ArrayList<>();
        for (TimeIntervalType timeIntervalType : timeIntervalTypes) {
            timeIntervalTypeNames.add(timeIntervalType.name());
        }
        return timeIntervalTypeNames;
    }
}
