package de.ur.remex.model.experiment;

import androidx.annotation.NonNull;

public abstract class Step {

    protected int id;
    protected int waitForId;
    protected String name;
    protected StepType type;
    protected Step previousStep;
    protected Step nextStep;

    public void setPreviousStep(Step previousStep) {
        this.previousStep = previousStep;
    }

    public Step getPreviousStep() {
        return previousStep;
    }

    public void setNextStep(Step nextStep) {
        this.nextStep = nextStep;
    }

    public Step getNextStep() {
        return nextStep;
    }

    public StepType getType() {
        return type;
    }

    public void setId(int stepId) {
        this.id = stepId;
    }

    public int getId() {
        return id;
    }

    public void setWaitForId(int stepId) {
        this.waitForId = stepId;
    }

    public int getWaitForId() {
        return waitForId;
    }
}
