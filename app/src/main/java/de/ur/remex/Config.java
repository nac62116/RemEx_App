package de.ur.remex;

// Class for shared values

// Can't be extended
public final class Config {

    private Config() {
        // Can't be instantiated
    }

    // General app settings
    public static final int ADMIN_TIMEOUT_MIN = 10;
    public static final String INITIAL_PASSWORD = "Medbo_TSST";
    public static final String EXPERIMENT_NAME_FIELD_SUFFIX = "Aktuelles Experiment:\n";
    public static final String EXPERIMENT_NAME_FIELD_NO_EXPERIMENT_LOADED = "Kein Experiment geladen";

    // User feedback
    public static final String INTERNAL_STORAGE_SAVING_ALERT_TITLE = "Fehler beim Speichern";
    public static final String INTERNAL_STORAGE_SAVING_ALERT_MESSAGE = "Die Datei konnte nicht im internen Appspeicher abgelegt werden. Versuchen Sie es bitte erneut oder kontaktieren Sie den Support.";
    public static final String INTERNAL_STORAGE_LOADING_ALERT_TITLE = "Fehler beim Laden";
    public static final String INTERNAL_STORAGE_LOADING_ALERT_MESSAGE = "Die angeforderte Datei konnte nicht aus dem internen Appspeicher geladen werden. Versuchen Sie es bitte erneut oder kontaktieren Sie den Support.";
    public static final String INTERNAL_STORAGE_READING_ALERT_TITLE = "Fehler beim Lesen";
    public static final String INTERNAL_STORAGE_READING_ALERT_MESSAGE = "Die angeforderte Datei aus dem internen Appspeicher konnte nicht gelesen werden. Versuchen Sie es bitte erneut oder kontaktieren Sie den Support.";
    public static final String CLEAN_UP_ALERT_TITLE = "Fehler beim aufräumen";
    public static final String CLEAN_UP_ALERT_MESSAGE = "Die Bilder und Videos des letzten Experiments konnten nicht vollständig entfernt werden. Das ist nicht weiter schlimm für die Funktionalität der App. Sollte diese Nachricht öfter auftauchen, kontaktieren Sie bitte den Support.";
    public static final String PASSWORD_ALERT_TITLE = "Login fehlgeschlagen";
    public static final String PASSWORD_ALERT_MESSAGE = "Das eingegebene Passwort ist falsch.";
    public static final String EXPERIMENT_NOT_FINISHED_ALERT_TITLE = "Experiment läuft noch";
    public static final String EXPERIMENT_NOT_FINISHED_ALERT_MESSAGE = "Das Experiment der aktuellen Versuchsperson läuft noch. Möchten Sie trotzdem das Experiment mit einer neuen Versuchsperson starten? Dadurch wird das aktuelle Experiment gestoppt und die dazugehörige CSV-Datei geht verloren.";
    public static final String CSV_NOT_SAVED_ALERT_TITLE = "CSV-Datei noch nicht gespeichert";
    public static final String CSV_NOT_SAVED_ALERT_MESSAGE = "Die CSV-Datei der letzten Versuchsperson wurde noch nicht gespeichert und geht verloren wenn ein neues Experiment gestartet wird. Möchten Sie trotzdem das Experiment starten?";
    public static final String EXTERNAL_WRITE_ALERT_TITLE = "Speichern fehlgeschlagen";
    public static final String EXTERNAL_WRITE_ALERT_MESSAGE = "Die CSV-Datei konnte nicht gespeichert werden. Versuchen Sie es bitte erneut oder kontaktieren Sie den Support.";
    public static final String EXTERNAL_READ_ALERT_TITLE = "Laden fehlgeschlagen";
    public static final String EXTERNAL_READ_ALERT_MESSAGE = "Die JSON-Datei konnte nicht geladen werden. Versuchen Sie es bitte erneut oder kontaktieren Sie den Support.";
    public static final String JSON_PARSE_ALERT_TITLE = "Parse fehlgeschlagen.";
    public static final String JSON_PARSE_ALERT_MESSAGE = "Die JSON-Datei konnte nicht richtig geparsed werden. Versuchen Sie es bitte erneut oder kontaktieren Sie den Support.";
    public static final String INPUT_INVALID_ALERT_TITLE = "Angaben unvollständig";
    public static final String INPUT_INVALID_ALERT_MESSAGE = "Es wurden nicht alle Felder ausgefüllt.";
    public static final String DATE_INVALID_ALERT_TITLE = "Falsches Datum";
    public static final String DATE_INVALID_ALERT_MESSAGE = "Das gewählte Datum ist heute oder liegt in der Vergangenheit.";
    public static final String EXPERIMENT_NOT_LOADED_ALERT_TITLE = "Kein Experiment geladen";
    public static final String EXPERIMENT_NOT_LOADED_ALERT_MESSAGE = "Es kann keine Versuchsperson erstellt werden, da noch kein Experiment geladen wurde. Bitte zuerst ein Experiment hochladen. (Ein Experiment kann über den RemEx Editor im Browser erstellt, dann via USB auf das Smartphone übertragen und anschließend über den Button \"Neues Experiment laden\" in die App geladen werden)";
    public static final String CSV_SAVED_TOAST = "CSV-Datei gespeichert";
    public static final String EXPERIMENT_LOADED_TOAST = "Experiment erfolgreich geladen";
    public static final String EXPERIMENT_STARTED_TOAST = "Experiment gestartet";
    public static final String SURVEY_TIMEOUT_TOAST = "Zeit abgelaufen.\nBefragung beendet.";
    public static final String OK = "Ok";
    public static final String JA = "Ja";
    public static final String NEIN = "Nein";

    // Event types
    public static final String EVENT_NEXT_STEP = "nextStepEvent";
    public static final String EVENT_SURVEY_STARTED = "surveyStartedEvent";
    public static final String EVENT_SURVEY_TIMEOUT = "surveyTimeoutEvent";
    public static final String EVENT_SURVEY_ALARM = "surveyAlarmEvent";
    public static final String EVENT_NOTIFICATION_TIMEOUT = "notificationTimeoutEvent";
    public static final String EVENT_STEP_TIMER = "stepTimeoutEvent";
    public static final String EVENT_WAITING_ROOM_ENTERED = "waitingRoomEnteredEvent";
    public static final String EVENT_NEXT_QUESTION = "nextQuestionEvent";
    public static final String EVENT_CSV_REQUEST = "csvRequestEvent";
    public static final String EVENT_APP_KILLED = "appKilledEvent";

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
    public static final String CHOICE_TYPE_KEY = "choiceType";
    public static final String POINT_OF_TIME_TYPES_KEY = "pointOfTimeTypes";
    public static final String TIME_INTERVALL_TYPES_KEY = "timeIntervallTypes";
    public static final String ANSWER_TEXTS_KEY = "answerTexts";
    public static final String SCALE_LABEL_MIN_KEY = "scaleLabelMin";
    public static final String SCALE_LABEL_MAX_KEY = "scaleLabelMax";
    public static final String INITIAL_SCALE_VALUE_KEY = "initialScaleValue";
    public static final String SCALE_ITEM_COUNT_KEY = "scaleItemCount";

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
    public static final String FILE_NAME_FIRST_START = "firstApplicationStart";
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
    public static final String FILE_NAME_EXPERIMENT_JSON = "experimentJSON";
    public static final String FILE_NAME_NEXT_SURVEY_ALARM = "nextSurveyAlarmInMillis";
    public static final String FILE_NAME_DECODED_VIDEO = "current_video.mp4";

    // File Values
    public static final String SURVEY_ENTRANCE_OPENED = "surveyEntranceOpened";
    public static final String SURVEY_ENTRANCE_CLOSED = "surveyEntranceClosed";
    public static final String INITIAL_CSV_VALUE = "VP_ID,VP_GROUP,SURVEY_NAME,QUESTION_NAME,ANSWER_CODE,TIME_STAMP*";
    public static final String EXPERIMENT_RUNNING = "experimentRunning";
    public static final String EXPERIMENT_FINISHED = "experimentFinished";
    public static final String CSV_SAVED = "CSV-Datei wurde bereits gespeichert";
    public static final String CSV_NOT_SAVED = "CSV-Datei wurde noch nicht gespeichert";
}
