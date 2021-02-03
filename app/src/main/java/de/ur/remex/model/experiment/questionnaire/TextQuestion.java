package de.ur.remex.model.experiment.questionnaire;

public class TextQuestion extends Question {

    private Question nextQuestion;

    public TextQuestion() {
        type = QuestionType.TEXT;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }
}