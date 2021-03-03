package de.ur.remex.model.experiment.questionnaire;

public abstract class Question {

    protected QuestionType type;
    // Has to be unique inside one questionnaire
    protected int id;
    // Has to be unique inside one survey
    protected String name;
    // Max characters 130
    protected String text;
    // Max characters 90
    protected String hint;
    // Only needed if back button gets implemented.
    protected int previousQuestionId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuestionType getType() {
        return type;
    }

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
