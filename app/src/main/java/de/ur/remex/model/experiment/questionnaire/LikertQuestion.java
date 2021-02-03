package de.ur.remex.model.experiment.questionnaire;

public class LikertQuestion extends Question {

    private Question nextQuestion;
    private String scaleMinimumLabel;
    private String scaleMaximumLabel;
    private int initialValue;
    private int itemCount;

    public LikertQuestion() {
        type = QuestionType.LIKERT;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

    public String getScaleMinimumLabel() {
        return scaleMinimumLabel;
    }

    public void setScaleMinimumLabel(String scaleMinimumLabel) {
        this.scaleMinimumLabel = scaleMinimumLabel;
    }

    public String getScaleMaximumLabel() {
        return scaleMaximumLabel;
    }

    public void setScaleMaximumLabel(String scaleMaximumLabel) {
        this.scaleMaximumLabel = scaleMaximumLabel;
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
