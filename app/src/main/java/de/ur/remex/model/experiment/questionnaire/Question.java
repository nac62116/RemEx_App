package de.ur.remex.model.experiment.questionnaire;

public abstract class Question {

    protected QuestionType type;
    protected String name;
    protected String text;
    protected String hint;
    // Only needed if back button gets implemented.
    protected Question previousQuestion;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
