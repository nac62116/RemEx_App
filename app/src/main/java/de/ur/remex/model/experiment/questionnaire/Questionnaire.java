package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;

public class Questionnaire extends Step {

    // RemEx Interface: For below attributes create two hidden instructions, one before and one after the questionnaire step.
    // instructionText
    // instructionHeader
    // dischargeText
    // dischargeHeader

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
            if (question.getPreviousQuestionId() == null) {
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
