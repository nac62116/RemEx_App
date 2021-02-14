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

    public String[] getAnswerTexts() {
        String[] answerTexts = new String[answers.size()];
        for (int i = 0; i < answerTexts.length; i++) {
            answerTexts[i] = answers.get(i).getText();
        }
        return answerTexts;
    }

    public String getCodeByAnswerText(String answerText) {
        for (Answer a: answers) {
            if (a.getText().equals(answerText)) {
                return a.getCode();
            }
        }
        return null;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    public Question getNextQuestionByAnswerText(String answerText) {
        for (Answer a: answers) {
            if (a.getText().equals(answerText)) {
                return a.getNextQuestion();
            }
        }
        return null;
    }
}
