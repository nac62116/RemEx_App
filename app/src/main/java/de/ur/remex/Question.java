package de.ur.remex;

public class Question {

    enum Type {
        LIKERT,
        SINGLE_CHOICE,
        MULTIPLE_CHOICE,
        DATE,
        DAYTIME,
        MINUTES,
        HOURS,
        DAYS
    }

    private String questionText;
    private Answer[] answers;
    private Question previousQuestion;
}
