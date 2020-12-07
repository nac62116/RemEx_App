package de.ur.remex.model.experiment;

public class Instruction extends Step {

    private String header;
    // Max characters: 360 (with image), 570 (without image)
    private String text;
    private String imageFileName;
    private String videoFileName;
    private int durationInMin;
    private boolean isFinished;

    public Instruction() {
        type = StepType.INSTRUCTION;
        isFinished = false;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}
