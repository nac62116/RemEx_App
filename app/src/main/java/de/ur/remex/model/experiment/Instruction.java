package de.ur.remex.model.experiment;

public class Instruction extends Step {

    private String header;
    // Max characters: 350 (with image), 500 (without image)
    private String text;
    // Either image or video in one Instruction
    private String imageFileName;
    private String videoFileName;
    private int durationInMin;
    // Max characters: 500
    private String waitingText;
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

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getDurationInMin() {
        return durationInMin;
    }

    public void setDurationInMin(int durationInMin) {
        this.durationInMin = durationInMin;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public String getWaitingText() {
        return waitingText;
    }

    public void setWaitingText(String waitingText) {
        this.waitingText = waitingText;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }
}
