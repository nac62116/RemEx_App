package de.ur.remex.model.experiment.questionnaire;

public abstract class Question {

    protected QuestionType type;
    protected String questionText;
    protected String questionHint;
    protected Answer[] answers;
    protected Question previousQuestion;
}
