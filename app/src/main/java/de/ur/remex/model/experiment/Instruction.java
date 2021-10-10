package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Instruction extends Step {

    // Max characters: 50
    private final String header;
    // Max characters: 350 (with image), 500 (without image)
    private final String text;
    // Either image or video in one Instruction
    private final String imageFileName;
    private final String videoFileName;
    private final int durationInMin;
    // Max characters: 500
    private final String waitingText;
    private boolean isFinished;

    @JsonCreator
    public Instruction(@JsonProperty("id") int id,
                       @JsonProperty("waitForStep") int waitForStep,
                       @JsonProperty("nextStepId") int nextStepId,
                       @JsonProperty("previousStepId") int previousStepId,
                       @JsonProperty("header") String header,
                       @JsonProperty("text") String text,
                       @JsonProperty("imageFileName") String imageFileName,
                       @JsonProperty("videoFileName") String videoFileName,
                       @JsonProperty("durationInMin") int durationInMin,
                       @JsonProperty("waitingText") String waitingText,
                       @JsonProperty("isFinished") boolean isFinished) {
        this.id = id;
        this.type = StepType.INSTRUCTION;
        this.waitForStep = waitForStep;
        this.nextStepId = nextStepId;
        this.previousStepId = previousStepId;
        this.header = header;
        this.text = text;
        this.imageFileName = imageFileName;
        this.videoFileName = videoFileName;
        this.durationInMin = durationInMin;
        this.waitingText = waitingText;
        this.isFinished = isFinished;
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

    public int getDurationInMin() {
        return durationInMin;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public String getWaitingText() {
        return waitingText;
    }

    public String getVideoFileName() {
        return videoFileName;
    }
}
