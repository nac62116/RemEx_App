package de.ur.remex.model.experiment.questionnaire;

import java.util.ArrayList;

public class MultipleChoiceQuestion extends Question {

    private ArrayList<Answer> answers;

    public MultipleChoiceQuestion() {
        type = QuestionType.MULTIPLE_CHOICE;
        answers = new ArrayList<>();
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }
}
