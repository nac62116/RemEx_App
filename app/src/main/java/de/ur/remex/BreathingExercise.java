package de.ur.remex;

public class BreathingExercise extends Step {

    enum Mode {
        MOVING_CIRCLE,
        STATIC_CIRCLE
    }

    private String instructionText;
    private String inhaleText;
    private String exhaleText;
    private int durationInMin;
    private int inhaleDurationInSec;
    private int exhaleDurationInSec;
    private Mode mode;


    public BreathingExercise() {
        type = StepType.BREATHING_EXERCISE;
    }
}
