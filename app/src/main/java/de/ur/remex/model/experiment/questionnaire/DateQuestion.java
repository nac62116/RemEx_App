package de.ur.remex.model.experiment.questionnaire;

// TODO: Combine Date and Daytime Question to PointOfTimeQuestion with QuestionType.POINT_OF_TIME
//  and PointOfTimeType.<DATE,DAYTIME> (both should be possible at the same time)

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
