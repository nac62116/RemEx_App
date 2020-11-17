package de.ur.remex.model.questionnaire;

import de.ur.remex.model.Step;
import de.ur.remex.model.StepType;

public class Questionnaire extends Step {

    private String instructionText;
    private Question[] questions;

    public Questionnaire() {
        type = StepType.QUESTIONNAIRE;
    }
}
