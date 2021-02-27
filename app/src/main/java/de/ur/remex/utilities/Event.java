package de.ur.remex.utilities;

import android.content.Context;

public class Event {

    private final String type;
    private final Object data;
    private final Context context;

    public Event(Context activityContext, String type, Object data) {
        this.context = activityContext;
        this.data = data;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public Context getContext() {
        return context;
    }
}
