package de.ur.remex.model.experiment.questionnaire;

import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;

public class Questionnaire extends Step {

    private String instructionText;
    private Question[] questions;

    public Questionnaire() {
        type = StepType.QUESTIONNAIRE;
    }
}
