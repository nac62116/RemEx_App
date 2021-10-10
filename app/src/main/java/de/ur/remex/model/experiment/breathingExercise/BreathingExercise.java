package de.ur.remex.model.experiment.breathingExercise;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;

public class BreathingExercise extends Step {

    // RemEx Interface: For below attributes create two hidden instructions, one before and one after the breathingExercise step.
    // instructionText
    // instructionHeader
    // dischargeText
    // dischargeHeader

    // int value! max duration = 60
    private final int durationInMin;
    // min 2, max 10
    private final int breathingFrequencyInSec;
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
