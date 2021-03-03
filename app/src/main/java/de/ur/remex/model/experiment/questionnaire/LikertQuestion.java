package de.ur.remex.model.experiment.questionnaire;

public class LikertQuestion extends Question {

    private int nextQuestionId;
    private String scaleMinimumLabel;
    private String scaleMaximumLabel;
    private int initialValue;
    private int itemCount;

    public LikertQuestion() {
        type = QuestionType.LIKERT;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(int nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
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
