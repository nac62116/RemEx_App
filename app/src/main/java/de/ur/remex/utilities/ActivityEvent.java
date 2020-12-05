package de.ur.remex.utilities;

import android.content.Context;

public class ActivityEvent {

    private String type;
    private String experimentData;
    private Context context;

    public ActivityEvent(Context context, String type, String data) {
        this.context = context;
        this.experimentData = data;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getExperimentData() {
        return experimentData;
    }

    public Context getContext() {
        return context;
    }
}
