package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextQuestion extends Question {

    @JsonCreator
    public TextQuestion(@JsonProperty("id") int id,
                        @JsonProperty("name") String name,
                        @JsonProperty("text") String text,
                        @JsonProperty("hint") String hint,
                        @JsonProperty("nextQuestionId") int nextQuestionId) {
        this.id = id;
        this.type = QuestionType.TEXT;
        this.name = name;
        this.text = text;
        this.hint = hint;
        this.nextQuestionId = nextQuestionId;
    }
}