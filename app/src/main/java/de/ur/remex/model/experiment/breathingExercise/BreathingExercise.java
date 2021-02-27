package de.ur.remex.model.experiment.breathingExercise;

import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;

public class BreathingExercise extends Step {

    // RemEx Interface: For below attributes create two hidden instructions, one before and one after the breathingExercise step.
    // instructionText
    // instructionHeader
    // dischargeText
    // dischargeHeader

    // int value! max duration = 60
    private int durationInMin;
    // min 2, max 10
    private int breathingFrequencyInSec;
    private BreathingMode mode;


    public BreathingExercise() {
        type = StepType.BREATHING_EXERCISE;
    }

    public int getDurationInMin() {
        return durationInMin;
    }

    public void setDurationInMin(int durationInMin) {
        this.durationInMin = durationInMin;
    }

    public int getBreathingFrequencyInSec() {
        return breathingFrequencyInSec;
    }

    public void setBreathingFrequencyInSec(int breathingFrequencyInSec) {
        this.breathingFrequencyInSec = breathingFrequencyInSec;
    }

    public BreathingMode getMode() {
        return mode;
    }

    public void setMode(BreathingMode mode) {
        this.mode = mode;
    }
}
