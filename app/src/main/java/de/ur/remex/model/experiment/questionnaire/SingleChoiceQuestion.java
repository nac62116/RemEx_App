package de.ur.remex.model.experiment.questionnaire;

import java.util.ArrayList;

public class SingleChoiceQuestion extends Question {

    private ArrayList<Answer> answers;

    public SingleChoiceQuestion() {
        type = QuestionType.SINGLE_CHOICE;
        answers = new ArrayList<>();
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }
}
