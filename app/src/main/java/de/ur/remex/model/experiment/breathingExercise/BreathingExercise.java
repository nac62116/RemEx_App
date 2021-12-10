package de.ur.remex.model.experiment.breathingExercise;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;

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
public class BreathingExercise extends Step {

    @JsonProperty("durationInMin")
    private final int durationInMin;
    @JsonProperty("breathingFrequencyInSec")
    private final int breathingFrequencyInSec;
    @JsonProperty("mode")
    private final BreathingMode mode;

    @JsonCreator
    public BreathingExercise(@JsonProperty("id") int id,
                             @JsonProperty("waitForStep") int waitForStep,
                             @JsonProperty("nextStepId") int nextStepId,
                             @JsonProperty("previousStepId") int previousStepId,
                             @JsonProperty("durationInMin") int durationInMin,
                             @JsonProperty("breathingFrequencyInSec") int breathingFrequencyInSec,
                             @JsonProperty("mode") String mode) {
        this.id = id;
        this.type = StepType.BREATHING_EXERCISE;
        this.waitForStep = waitForStep;
        this.nextStepId = nextStepId;
        this.previousStepId = previousStepId;
        this.durationInMin = durationInMin;
        this.breathingFrequencyInSec = breathingFrequencyInSec;
        if (mode.equals(BreathingMode.MOVING_CIRCLE.name())) {
            this.mode = BreathingMode.MOVING_CIRCLE;
        }
        else {
            this.mode = BreathingMode.STATIC_CIRCLE;
        }
    }

    public int getDurationInMin() {
        return durationInMin;
    }

    public int getBreathingFrequencyInSec() {
        return breathingFrequencyInSec;
    }

    public BreathingMode getMode() {
        return mode;
    }
}
