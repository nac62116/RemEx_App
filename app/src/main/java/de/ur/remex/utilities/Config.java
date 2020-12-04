package de.ur.remex.utilities;

// Class for shared values
public class Config {

    // Event types
    public static final String EVENT_NEXT_STEP = "nextStep";
    public static final String EVENT_SURVEY_STARTED = "surveyStarted";

    // Alarm purposes
    public static final String ALARM_PURPOSE_KEY = "purpose";
    public static final String PURPOSE_SURVEY_NOTIFY = "surveyNotification";

    // Notification settings
    public static final String NOTIFICATION_HEADER = "Neue Befragung";
    public static final String NOTIFICATION_TEXT = "Es ist Zeit für deine Befragung. Schön, dass du dabei bist.";
    public static final String NOTIFICATION_CHANNEL_NAME = "Befragungswecker";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Dieser Channel aktiviert den Wecker der RemEx-App. Dadurch werden die Teilnehmer an ihre Befragungen erinnert.";

    // InstructionActivity
    public static final String INSTRUCTION_HEADER_KEY = "instructionHeader";
    public static final String INSTRUCTION_TEXT_KEY = "instructionText";
}
