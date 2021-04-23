package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class PointOfTimeQuestion extends Question {

    // Has to contain at least one of PointOfTimeType.DATE or PointOfTimeType.DAYTIME
    private final ArrayList<PointOfTimeType> pointOfTimeTypes;

    @JsonCreator
    public PointOfTimeQuestion(@JsonProperty("id") int id,
                               @JsonProperty("name") String name,
                               @JsonProperty("text") String text,
                               @JsonProperty("hint") String hint,
                               @JsonProperty("nextQuestionId") int nextQuestionId,
                               @JsonProperty("pointOfTimeTypeNames") ArrayList<String> pointOfTimeTypeNames) {
        this.id = id;
        this.type = QuestionType.POINT_OF_TIME;
        this.name = name;
        this.text = text;
        this.hint = hint;
        this.nextQuestionId = nextQuestionId;
        pointOfTimeTypes = new ArrayList<>();
        if (pointOfTimeTypeNames.contains(PointOfTimeType.DATE.name())) {
            pointOfTimeTypes.add(PointOfTimeType.DATE);
        }
        if (pointOfTimeTypeNames.contains(PointOfTimeType.DAYTIME.name())) {
            pointOfTimeTypes.add(PointOfTimeType.DAYTIME);
        }
    }

    public ArrayList<String> getPointOfTimeTypeNames() {
        ArrayList<String> pointOfTimeTypeNames = new ArrayList<>();
        for (PointOfTimeType pointOfTimeType: pointOfTimeTypes) {
            pointOfTimeTypeNames.add(pointOfTimeType.name());
        }
        return pointOfTimeTypeNames;
    }
}
