package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.ur.remex.model.experiment.breathingExercise.BreathingExercise;
import de.ur.remex.model.experiment.questionnaire.Questionnaire;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Instruction.class, name = "INSTRUCTION"),
        @JsonSubTypes.Type(value = BreathingExercise.class, name = "BREATHING_EXERCISE"),
        @JsonSubTypes.Type(value = Questionnaire.class, name = "QUESTIONNAIRE")
})
public abstract class Step {

    protected int id;
    protected StepType type;
    protected int waitForStep;
    protected int nextStepId;
    protected int previousStepId;

    public int getNextStepId() {
        return nextStepId;
    }
        
    public int getPreviousStepId() {
        return previousStepId;
    }

    public StepType getType() {
        return type;
    }

    public int getWaitForStep() {
        return waitForStep;
    }

    public int getId() {
        return id;
    }
}
