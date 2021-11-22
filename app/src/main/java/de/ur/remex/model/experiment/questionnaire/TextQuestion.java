package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextQuestion extends Question {

    @JsonCreator
    public TextQuestion(@JsonProperty("id") int id,
                        @JsonProperty("name") String name,
                        @JsonProperty("text") String text,
                        @JsonProperty("hint") String hint,
                        @JsonProperty("nextQuestionId") int nextQuestionId,
                        @JsonProperty("previousQuestionId") int previousQuestionId) {
        this.id = id;
        this.type = QuestionType.TEXT;
        this.name = name;
        this.text = text;
        this.hint = hint;
        this.nextQuestionId = nextQuestionId;
        this.previousQuestionId = previousQuestionId;
    }
}
