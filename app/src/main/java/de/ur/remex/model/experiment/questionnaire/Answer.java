package de.ur.remex.model.experiment.questionnaire;

public class Answer {

    private String text;
    // Add RemEx Interface functionality to create a list of the answer codes
    private String code;
    // Multiple choice answers must either have the same destination or all combinations must be defined
    private Question nextQuestion;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}
