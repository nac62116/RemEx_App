package de.ur.remex.model.experiment.questionnaire;

public class TimeIntervallQuestion extends Question {

    private Question nextQuestion;

    public TimeIntervallQuestion() {
        type = QuestionType.TIME_INTERVALL;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}
