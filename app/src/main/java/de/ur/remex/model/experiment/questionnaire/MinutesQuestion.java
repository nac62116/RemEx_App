package de.ur.remex.model.experiment.questionnaire;

public class MinutesQuestion extends Question {

    private Question nextQuestion;

    public MinutesQuestion() {
        type = QuestionType.MINUTES;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}