package de.ur.remex.model.experiment.questionnaire;

public class DateQuestion extends Question {

    private Question nextQuestion;

    public DateQuestion() {
        type = QuestionType.DATE;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}
