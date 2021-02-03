package de.ur.remex.model.experiment.questionnaire;

public class DaysQuestion extends Question {

    private Question nextQuestion;

    public DaysQuestion() {
        type = QuestionType.DAYS;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}
