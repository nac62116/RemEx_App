package de.ur.remex.model.experiment.questionnaire;

public class HoursQuestion extends Question {

    private Question nextQuestion;

    public HoursQuestion() {
        type = QuestionType.HOURS;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}