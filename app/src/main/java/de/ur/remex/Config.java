package de.ur.remex;

// Class for shared values
public class Config {

    // General app settings
    public static final String EXIT_APP_KEY = "exitApp";
    public static final int ADMIN_TIMEOUT_MIN = 3;

    // Event types
    public static final String EVENT_NEXT_STEP = "nextStep";
    public static final String EVENT_SURVEY_STARTED = "surveyStarted";

    // Alarm purposes
    public static final String ALARM_PURPOSE_KEY = "purpose";
    public static final String PURPOSE_SURVEY_NOTIFY = "surveyNotification";
    public static final String PURPOSE_ADMIN_TIMEOUT = "adminTimeout";

    // Notification settings
    public static final String NOTIFICATION_HEADER = "Neue Befragung";
    public static final String NOTIFICATION_TEXT = "Es ist Zeit für deine Befragung. Schön, dass du dabei bist.";
    public static final String NOTIFICATION_CHANNEL_NAME = "Befragungswecker";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Dieser Channel aktiviert den Wecker der RemEx-App. Dadurch werden die Teilnehmer an ihre Befragungen erinnert.";

    // InstructionActivity
    public static final String INSTRUCTION_HEADER_KEY = "instructionHeader";
    public static final String INSTRUCTION_TEXT_KEY = "instructionText";
}
