package de.ur.remex;

public class Questionnaire extends Step {

    private String instructionText;
    private Question[] questions;

    public Questionnaire() {
        type = StepType.QUESTIONNAIRE;
    }
}
