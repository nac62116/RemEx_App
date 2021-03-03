package de.ur.remex.model.experiment.questionnaire;

public class DateQuestion extends Question {

    private int nextQuestionId;

    public DateQuestion() {
        type = QuestionType.DATE;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(int nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }
}
