package de.ur.remex.model.experiment.questionnaire;

public class DaytimeQuestion extends Question {

    private int nextQuestionId;

    public DaytimeQuestion() {
        type = QuestionType.DAYTIME;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(int nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }
}