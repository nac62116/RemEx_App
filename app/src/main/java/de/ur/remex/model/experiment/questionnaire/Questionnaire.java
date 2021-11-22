package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Questionnaire extends Step {

    @JsonProperty("questions")
    private final ArrayList<Question> questions;

    @JsonCreator
    public Questionnaire(@JsonProperty("id") int id,
                         @JsonProperty("waitForStep") int waitForStep,
                         @JsonProperty("nextStepId") int nextStepId,
                         @JsonProperty("previousStepId") int previousStepId,
                         @JsonProperty("questions") ArrayList<Question> questions) {
        this.id = id;
        this.type = StepType.QUESTIONNAIRE;
        this.waitForStep = waitForStep;
        this.nextStepId = nextStepId;
        this.previousStepId = previousStepId;
        this.questions = questions;
    }

    public Question getFirstQuestion() {
        for (Question question: questions) {
            if (question.getPreviousQuestionId() == 0) {
                return question;
            }
        }
        return null;
    }

    public Question getQuestionById(int questionId) {
        for (Question question: questions) {
            if (questionId == question.getId()) {
                return question;
            }
        }
        return null;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
