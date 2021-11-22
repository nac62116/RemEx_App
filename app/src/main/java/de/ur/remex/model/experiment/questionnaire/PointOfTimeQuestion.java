package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PointOfTimeQuestion extends Question {

    // Has to contain at least one of PointOfTimeType.DATE or PointOfTimeType.DAYTIME
    @JsonProperty("pointOfTimeTypes")
    private final ArrayList<PointOfTimeType> pointOfTimeTypes;

    @JsonCreator
    public PointOfTimeQuestion(@JsonProperty("id") int id,
                               @JsonProperty("name") String name,
                               @JsonProperty("text") String text,
                               @JsonProperty("hint") String hint,
                               @JsonProperty("nextQuestionId") int nextQuestionId,
                               @JsonProperty("previousQuestionId") int previousQuestionId,
                               @JsonProperty("pointOfTimeTypes") ArrayList<String> pointOfTimeTypes) {
        this.id = id;
        this.type = QuestionType.POINT_OF_TIME;
        this.name = name;
        this.text = text;
        this.hint = hint;
        this.nextQuestionId = nextQuestionId;
        this.previousQuestionId = previousQuestionId;
        this.pointOfTimeTypes = new ArrayList<>();
        if (pointOfTimeTypes.contains(PointOfTimeType.DATE.name())) {
            this.pointOfTimeTypes.add(PointOfTimeType.DATE);
        }
        if (pointOfTimeTypes.contains(PointOfTimeType.DAYTIME.name())) {
            this.pointOfTimeTypes.add(PointOfTimeType.DAYTIME);
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
