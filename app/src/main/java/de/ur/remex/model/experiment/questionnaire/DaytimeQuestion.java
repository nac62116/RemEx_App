package de.ur.remex.model.experiment.questionnaire;

public class DaytimeQuestion extends Question {

    private Question nextQuestion;

    public DaytimeQuestion() {
        type = QuestionType.DAYTIME;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}