package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LikertQuestion extends Question {

    @JsonProperty("scaleMinimumLabel")
    private final String scaleMinimumLabel;
    @JsonProperty("scaleMaximumLabel")
    private final String scaleMaximumLabel;
    @JsonProperty("initialValue")
    private final int initialValue;
    @JsonProperty("itemCount")
    private final int itemCount;

    @JsonCreator
    public LikertQuestion(@JsonProperty("id") int id,
                          @JsonProperty("name") String name,
                          @JsonProperty("text") String text,
                          @JsonProperty("hint") String hint,
                          @JsonProperty("nextQuestionId") int nextQuestionId,
                          @JsonProperty("previousQuestionId") int previousQuestionId,
                          @JsonProperty("scaleMinimumLabel") String scaleMinimumLabel,
                          @JsonProperty("scaleMaximumLabel") String scaleMaximumLabel,
                          @JsonProperty("initialValue") int initialValue,
                          @JsonProperty("itemCount") int itemCount) {
        this.id = id;
        this.type = QuestionType.LIKERT;
        this.name = name;
        this.text = text;
        this.hint = hint;
        this.nextQuestionId = nextQuestionId;
        this.previousQuestionId = previousQuestionId;
        this.scaleMinimumLabel = scaleMinimumLabel;
        this.scaleMaximumLabel = scaleMaximumLabel;
        this.initialValue = initialValue;
        this.itemCount = itemCount;
    }

    public String getScaleMinimumLabel() {
        return scaleMinimumLabel;
    }

    public String getScaleMaximumLabel() {
        return scaleMaximumLabel;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public int getItemCount() {
        return itemCount;
    }
}
