package de.ur.remex.model.experiment.questionnaire;

import java.util.ArrayList;

public class ChoiceQuestion extends Question {

    private int nextQuestionId;
    private ArrayList<Answer> answers;
    // Has to contain either ChoiceType.SINGLE or ChoiceType.MULTIPLE
    private ChoiceType choiceType;

    public ChoiceQuestion() {
        type = QuestionType.CHOICE;
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

    public int getNextQuestionIdByAnswerText(String answerText) {
        for (Answer answer: answers) {
            if (answer.getText().equals(answerText)) {
                return answer.getNextQuestionId();
            }
        }
        return 0;
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(int nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }

    public ChoiceType getChoiceType() {
        return choiceType;
    }

    public void setChoiceType(ChoiceType choiceType) {
        this.choiceType = choiceType;
    }
}