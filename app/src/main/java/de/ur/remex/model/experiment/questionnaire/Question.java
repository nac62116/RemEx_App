package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChoiceQuestion.class, name = "CHOICE"),
        @JsonSubTypes.Type(value = LikertQuestion.class, name = "LIKERT"),
        @JsonSubTypes.Type(value = PointOfTimeQuestion.class, name = "POINT_OF_TIME"),
        @JsonSubTypes.Type(value = TextQuestion.class, name = "TEXT"),
        @JsonSubTypes.Type(value = TimeIntervalQuestion.class, name = "TIME_INTERVAL")
})
public abstract class Question {

    // Has to be unique inside one questionnaire
    protected int id;
    protected QuestionType type;
    // Has to be unique inside one survey
    protected String name;
    // Max characters 130
    protected String text;
    // Max characters 90
    protected String hint;
    protected int nextQuestionId;

    public int getId() {
        return id;
    }

    public QuestionType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getHint() {
        return hint;
    }

    public String getName() {
        return name;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }
}
