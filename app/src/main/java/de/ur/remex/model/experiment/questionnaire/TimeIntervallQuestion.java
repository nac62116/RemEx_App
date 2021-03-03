package de.ur.remex.model.experiment.questionnaire;

public class TimeIntervallQuestion extends Question {

    private int nextQuestionId;

    public TimeIntervallQuestion() {
        type = QuestionType.TIME_INTERVALL;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(int nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }
}
