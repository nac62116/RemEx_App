package de.ur.remex.utilities;

import android.content.Context;

public class Event {

    private final String type;
    private final String data;
    private final Context context;

    public Event(Context activityContext, String type, String data) {
        this.context = activityContext;
        this.data = data;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public Context getContext() {
        return context;
    }
}
