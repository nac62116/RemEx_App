package de.ur.remex;

public abstract class Step {

    protected String name;
    protected StepType type;
    protected Step previousStep;
    protected Step nextStep;

    public void setPreviousStep(Step previousStep) {
        this.previousStep = previousStep;
    }

    public void setNextStep(Step nextStep) {
        this.nextStep = nextStep;
    }

    public Step getPreviousStep() {
        return previousStep;
    }

    public Step getNextStep() {
        return nextStep;
    }
}
