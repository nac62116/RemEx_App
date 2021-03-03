package de.ur.remex.model.experiment.questionnaire;

public class TextQuestion extends Question {

    private int nextQuestionId;

    public TextQuestion() {
        type = QuestionType.TEXT;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(int nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }
}