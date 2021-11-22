package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Answer {

    @JsonProperty("text")
    private final String text;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("nextQuestionId")
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
