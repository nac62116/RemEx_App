package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Answer {

    private final String text;
    // Add RemEx Interface functionality to create a list of the answer codes
    // Has to be unique inside one question
    private final String code;
    private final int nextQuestionId;

    @JsonCreator
    public Answer(@JsonProperty("text") String text,
                  @JsonProperty("code") String code,
                  @JsonProperty("nextQuestionId") int nextQuestionId) {
        this.text = text;
        this.code = code;
        this.nextQuestionId = nextQuestionId;
    }

    public String getText() {
        return text;
    }

    public String getCode() {
        return code;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }
}
