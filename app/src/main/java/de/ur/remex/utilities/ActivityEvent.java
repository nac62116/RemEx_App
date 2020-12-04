package de.ur.remex.utilities;

import android.content.Context;

public class ActivityEvent {

    private String type;
    private String data;
    private Context context;

    public ActivityEvent(Context context, String type, String data) {
        this.context = context;
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
