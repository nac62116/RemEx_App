package de.ur.remex.model.experiment.questionnaire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ChoiceQuestion extends Question {

    private final ArrayList<Answer> answers;
    // Has to contain either ChoiceType.SINGLE or ChoiceType.MULTIPLE
    private final ChoiceType choiceType;

    @JsonCreator
    public ChoiceQuestion(@JsonProperty("id") int id,
                          @JsonProperty("name") String name,
                          @JsonProperty("text") String text,
                          @JsonProperty("hint") String hint,
                          @JsonProperty("nextQuestionId") int nextQuestionId,
                          @JsonProperty("answers") ArrayList<Answer> answers,
                          @JsonProperty("choiceType") String choiceType) {
        this.id = id;
        this.type = QuestionType.CHOICE;
        this.name = name;
        this.text = text;
        this.hint = hint;
        this.nextQuestionId = nextQuestionId;
        this.answers = answers;
        if (choiceType.equals(ChoiceType.SINGLE_CHOICE.name())) {
            this.choiceType = ChoiceType.SINGLE_CHOICE;
        }
        else {
            this.choiceType = ChoiceType.MULTIPLE_CHOICE;
        }
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

    public int getNextQuestionIdByAnswerText(String answerText) {
        for (Answer answer: answers) {
            if (answer.getText().equals(answerText)) {
                return answer.getNextQuestionId();
            }
        }
        return 0;
    }

    public ChoiceType getChoiceType() {
        return choiceType;
    }
}
