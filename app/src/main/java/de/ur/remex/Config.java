package de.ur.remex;

// Class for shared values

// Can't be extended
public final class Config {

    private Config() {
        // Can't be instantiated
    }

    // General app settings
    public static final int ADMIN_TIMEOUT_MIN = 3;
    public static final String MESSAGE_SURVEY_TIMEOUT = "Zeit abgelaufen.\nBefragung beendet.";

    // Event types
    static final String EVENT_NEXT_STEP = "nextStep";
    public static final String EVENT_SURVEY_STARTED = "surveyStarted";
    public static final String EVENT_SURVEY_TIMEOUT = "surveyTimeoutEvent";
    public static final String EVENT_SURVEY_ALARM = "surveyAlarmEvent";
    public static final String EVENT_NOTIFICATION_TIMEOUT = "notificationTimeoutEvent";
    public static final String EVENT_STEP_TIMER = "stepTimeoutEvent";
    public static final String EVENT_WAITING_ROOM_ENTERED = "waitingRoomEntered";
    public static final String EVENT_NEXT_QUESTION = "eventNextQuestion";

    // Intent Extra Keys
    public static final String EXIT_APP_KEY = "exitApp";
    public static final String ALARM_PURPOSE_KEY = "purpose";
    public static final String STEP_ID_KEY = "stepId";
    public static final String INSTRUCTION_HEADER_KEY = "instructionHeader";
    public static final String INSTRUCTION_TEXT_KEY = "instructionText";
    public static final String INSTRUCTION_IMAGE_KEY = "instructionImage";
    public static final String WAITING_ROOM_TEXT_KEY = "waitingRoomText";
    public static final String BREATHING_MODE_KEY = "breathingMode";
    public static final String BREATHING_INSTRUCTION_HEADER_KEY = "breathingInstructionHeader";
    public static final String BREATHING_INSTRUCTION_TEXT_KEY = "breathingInstructionText";
    public static final String BREATHING_DISCHARGE_HEADER_KEY = "breathingDischargeHeader";
    public static final String BREATHING_DISCHARGE_TEXT_KEY = "breathingDischargeText";
    public static final String BREATHING_DURATION_KEY = "breathingDuration";
    public static final String BREATHING_FREQUENCY_KEY = "breathingFrequency";

    // Alarm purposes
    public static final String PURPOSE_SURVEY_ALARM = "surveyAlarm";
    public static final String PURPOSE_ADMIN_TIMEOUT = "adminTimeout";
    public static final String PURPOSE_SURVEY_TIMEOUT = "surveyTimeout";
    public static final String PURPOSE_NOTIFICATION_TIMEOUT = "notificationTimeout";
    public static final String PURPOSE_STEP_TIMER = "stepTimeout";

    // Notification settings
    public static final String NOTIFICATION_HEADER = "Neue Befragung";
    public static final String NOTIFICATION_TEXT = "Es ist Zeit für deine Befragung.";
    public static final String NOTIFICATION_CHANNEL_NAME = "Befragungswecker";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Dieser Channel aktiviert den Wecker der RemEx-App. Dadurch werden die Teilnehmer an ihre Befragungen erinnert.";

    // Breathing exercise settings
    public static final String BREATHING_INHALE_TEXT = "Einatmen...";
    public static final String BREATHING_EXHALE_TEXT = "...und ausatmen";
    public static final String BREATHING_NEUTRAL_TEXT = "Entspannen";
    public static final int BREATHING_GONG_LENGTH_SEC = 2;
    public static final int BREATHING_TIMER_FREQUENCY_MS = 50; // Value to set the fps for the moving circle (f.e. 50ms = 20fps)

    // File Names
    public static final String FILE_NAME_ID = "vpId";
    public static final String FILE_NAME_GROUP = "vpGroup";
    public static final String FILE_NAME_CSV = "csv";
    public static final String FILE_NAME_PROGRESS = "progress";
    public static final String FILE_NAME_SURVEY_ENTRANCE = "surveyEntrance";

    // File Values
    public static final String SURVEY_ENTRANCE_OPENED = "surveyEntranceOpened";
    public static final String SURVEY_ENTRANCE_CLOSED = "surveyEntranceClosed";
}
