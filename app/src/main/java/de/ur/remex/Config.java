package de.ur.remex;

// Class for shared values

// Can't be extended
public final class Config {


    private Config() {
        // Can't be instantiated
    }

    // General app settings
    public static final int ADMIN_TIMEOUT_MIN = 10;
    public static final String INITIAL_PASSWORD = "";
    public static final String PASSWORD_ALERT_TITLE = "Login fehlgeschlagen";
    public static final String PASSWORD_ALERT_MESSAGE = "Das eingegebene Passwort ist falsch.";
    public static final String EXPERIMENT_NOT_FINISHED_ALERT_TITLE = "Experiment läuft noch";
    public static final String EXPERIMENT_NOT_FINISHED_ALERT_MESSAGE = "Das Experiment der aktuellen Versuchsperson läuft noch. Möchten Sie trotzdem das Experiment mit einer neuen Versuchsperson starten? Dadurch wird das aktuelle Experiment gestoppt und die dazugehörige CSV-Datei geht verloren.";
    public static final String EXTERNAL_WRITE_ALERT_TITLE = "Speichern fehlgeschlagen";
    public static final String EXTERNAL_WRITE_ALERT_MESSAGE = "Die CSV-Datei konnte nicht gespeichert werden. Versuchen Sie es bitte erneut oder kontaktieren Sie den Support.";
    public static final String INPUT_INVALID_ALERT_TITLE = "Angaben unvollständig";
    public static final String INPUT_INVALID_ALERT_MESSAGE = "Es wurden nicht alle Felder ausgefüllt.";
    public static final String DATE_INVALID_ALERT_TITLE = "Falsches Datum";
    public static final String DATE_INVALID_ALERT_MESSAGE = "Das gewählte Datum ist heute oder liegt in der Vergangenheit.";
    public static final String CSV_SAVED_TOAST = "CSV-Datei gespeichert";
    public static final String EXPERIMENT_STARTED_TOAST = "Experiment gestartet";
    public static final String OK = "Ok";
    public static final String JA = "Ja";
    public static final String NEIN = "Nein";
    public static final String MESSAGE_SURVEY_TIMEOUT = "Zeit abgelaufen.\nBefragung beendet.";

    // Event types
    public static final String EVENT_NEXT_STEP = "nextStep";
    public static final String EVENT_SURVEY_STARTED = "surveyStarted";
    public static final String EVENT_SURVEY_TIMEOUT = "surveyTimeoutEvent";
    public static final String EVENT_SURVEY_ALARM = "surveyAlarmEvent";
    public static final String EVENT_NOTIFICATION_TIMEOUT = "notificationTimeoutEvent";
    public static final String EVENT_STEP_TIMER = "stepTimeoutEvent";
    public static final String EVENT_WAITING_ROOM_ENTERED = "waitingRoomEntered";
    public static final String EVENT_NEXT_QUESTION = "eventNextQuestion";
    public static final String EVENT_CSV_REQUEST = "eventCsvRequest";

    // Intent Extra Keys
    public static final String EXIT_APP_KEY = "exitApp";
    public static final String ALARM_PURPOSE_KEY = "purpose";
    public static final String CREATE_CSV_KEY = "createCSV";
    public static final String START_EXPERIMENT_KEY = "startExperiment";
    public static final String START_TIME_MS_KEY = "startTimeMs";
    public static final String PROGRESS_MAXIMUM_KEY = "progressMaximum";
    public static final String GROUP_NAMES_KEY = "groupNames";
    public static final String STEP_ID_KEY = "stepId";
    public static final String INSTRUCTION_HEADER_KEY = "instructionHeader";
    public static final String INSTRUCTION_TEXT_KEY = "instructionText";
    public static final String INSTRUCTION_IMAGE_KEY = "instructionImage";
    public static final String INSTRUCTION_VIDEO_KEY = "instructionVideo";
    public static final String WAITING_ROOM_TEXT_KEY = "waitingRoomText";
    public static final String BREATHING_MODE_KEY = "breathingMode";
    public static final String BREATHING_DURATION_KEY = "breathingDuration";
    public static final String BREATHING_FREQUENCY_KEY = "breathingFrequency";
    public static final String QUESTION_TEXT_KEY = "questionText";
    public static final String QUESTION_HINT_KEY = "questionHint";
    public static final String QUESTION_TYPE_KEY = "questionType";
    public static final String POINT_OF_TIME_TYPES_KEY = "pointOfTimeTypes";
    public static final String ANSWER_TEXTS_KEY = "answerTexts";

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
    public static final String FILE_NAME_START_DATE = "startDate";
    public static final String FILE_NAME_START_TIME = "startTime";
    public static final String FILE_NAME_CSV = "csv";
    public static final String FILE_NAME_CSV_STATUS = "csvStatus";
    public static final String FILE_NAME_PROGRESS = "progress";
    public static final String FILE_NAME_SURVEY_ENTRANCE = "surveyEntrance";
    public static final String FILE_NAME_PASSWORD = "password";
    public static final String FILE_NAME_EXPERIMENT_STATUS = "experimentStatus";

    // File Values
    public static final String SURVEY_ENTRANCE_OPENED = "surveyEntranceOpened";
    public static final String SURVEY_ENTRANCE_CLOSED = "surveyEntranceClosed";
    public static final String INITIAL_CSV_VALUE = "VP_ID,VP_GROUP,SURVEY_NAME,QUESTION_NAME,ANSWER_CODE,TIME_STAMP*";
    public static final String EXPERIMENT_RUNNING = "experimentRunning";
    public static final String EXPERIMENT_FINISHED = "experimentFinished";
    public static final String CSV_SAVED = "CSV-Datei wurde bereits gespeichert";
    public static final String CSV_NOT_SAVED = "CSV-Datei wurde noch nicht gespeichert";
}
