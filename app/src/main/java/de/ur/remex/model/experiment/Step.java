package de.ur.remex.model.experiment;

public abstract class Step {

    // Id must have an unique value excluding 0 -> RemEx Interface
    protected int id;
    private String name;
    protected StepType type;
    protected int waitForStep;
    // Only needed if back button gets implemented.
    protected Step previousStep;
    protected Step nextStep;

    public void setNextStep(Step nextStep) {
        this.nextStep = nextStep;
    }

    public Step getNextStep() {
        return nextStep;
    }

    public StepType getType() {
        return type;
    }

    public void setWaitForStep(int stepId) {
        this.waitForStep = stepId;
    }

    public int getWaitForStep() {
        return waitForStep;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
