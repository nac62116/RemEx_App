package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
MIT License

Copyright (c) 2021 Colin Nash

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

@JsonIgnoreProperties(ignoreUnknown = true)
public class Instruction extends Step {

    @JsonProperty("header")
    private final String header;
    @JsonProperty("text")
    private final String text;
    @JsonProperty("imageFileName")
    private final String imageFileName;
    @JsonProperty("videoFileName")
    private final String videoFileName;
    @JsonProperty("durationInMin")
    private final int durationInMin;
    @JsonProperty("waitingText")
    private final String waitingText;
    @JsonProperty("isFinished")
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
