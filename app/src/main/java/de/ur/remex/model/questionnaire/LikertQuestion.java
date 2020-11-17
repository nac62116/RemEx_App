package de.ur.remex.model.questionnaire;

public class LikertQuestion extends Question {

    private String scaleMinimumLabel;
    private String scaleMaximumLabel;
    private int initialValue;

    public LikertQuestion() {
        type = QuestionType.LIKERT;
    }
}
