package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class TimeIntervalQuestion extends Question {

    // Has to contain at least one of TimeIntervalType.YEARS/MONTHS/DAYS/HOURS/MINUTES/SECONDS
    private final ArrayList<TimeIntervalType> timeIntervalTypes;

    @JsonCreator
    public TimeIntervalQuestion(@JsonProperty("id") int id,
                                @JsonProperty("name") String name,
                                @JsonProperty("text") String text,
                                @JsonProperty("hint") String hint,
                                @JsonProperty("nextQuestionId") int nextQuestionId,
                                @JsonProperty("timeIntervalTypeNames") ArrayList<String> timeIntervalTypeNames) {
        this.id = id;
        this.type = QuestionType.TIME_INTERVAL;
        this.name = name;
        this.text = text;
        this.hint = hint;
        this.nextQuestionId = nextQuestionId;
        timeIntervalTypes = new ArrayList<>();
        if (timeIntervalTypeNames.contains(TimeIntervalType.YEARS.name())) {
            timeIntervalTypes.add(TimeIntervalType.YEARS);
        }
        if (timeIntervalTypeNames.contains(TimeIntervalType.MONTHS.name())) {
            timeIntervalTypes.add(TimeIntervalType.MONTHS);
        }
        if (timeIntervalTypeNames.contains(TimeIntervalType.DAYS.name())) {
            timeIntervalTypes.add(TimeIntervalType.DAYS);
        }
        if (timeIntervalTypeNames.contains(TimeIntervalType.HOURS.name())) {
            timeIntervalTypes.add(TimeIntervalType.HOURS);
        }
        if (timeIntervalTypeNames.contains(TimeIntervalType.MINUTES.name())) {
            timeIntervalTypes.add(TimeIntervalType.MINUTES);
        }
        if (timeIntervalTypeNames.contains(TimeIntervalType.SECONDS.name())) {
            timeIntervalTypes.add(TimeIntervalType.SECONDS);
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
