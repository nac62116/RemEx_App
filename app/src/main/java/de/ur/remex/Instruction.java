package de.ur.remex;

public class Instruction extends Step {

    private String header;
    private String text;
    private String imageFileName;
    private String videoFileName;
    private int durationInMin;
    private boolean isFinished;

    public Instruction() {
        type = StepType.INSTRUCTION;
        isFinished = false;
    }
}
