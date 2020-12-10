package de.ur.remex.model.experiment.breathingExercise;

import de.ur.remex.model.experiment.Step;
import de.ur.remex.model.experiment.StepType;

public class BreathingExercise extends Step {

    private String instructionHeader;
    // Max characters: 500
    private String instructionText;
    private String dischargeHeader;
    // Max characters: 500
    private String dischargeText;
    // int value! max duration = 60
    private int durationInMin;
    // min 2, max 10
    private int breathingFrequencyInSec;
    private BreathingMode mode;


    public BreathingExercise() {
        type = StepType.BREATHING_EXERCISE;
    }


    public String getInstructionHeader() {
        return instructionHeader;
    }

    public void setInstructionHeader(String instructionHeader) {
        this.instructionHeader = instructionHeader;
    }

    public String getInstructionText() {
        return instructionText;
    }

    public void setInstructionText(String instructionText) {
        this.instructionText = instructionText;
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

    public String getDischargeHeader() {
        return dischargeHeader;
    }

    public void setDischargeHeader(String dischargeHeader) {
        this.dischargeHeader = dischargeHeader;
    }

    public String getDischargeText() {
        return dischargeText;
    }

    public void setDischargeText(String dischargeText) {
        this.dischargeText = dischargeText;
    }
}
