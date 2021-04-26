package de.ur.remex.utilities;

import android.app.AlertDialog;
import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ur.remex.Config;

public class JSONParser {

    private final Context context;

    public JSONParser(Context context) {
        this.context = context;
    }

    public Object parse(String JSONString, Class<?> targetClass) {
        // JSON-String to target class object
        Object parsedObject = null;
        if (JSONString != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                parsedObject = mapper.readValue(JSONString, targetClass);
            }
            catch (JsonProcessingException e) {
                new AlertDialog.Builder(context)
                        .setTitle(Config.JSON_PARSE_ALERT_TITLE)
                        .setMessage(Config.JSON_PARSE_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
        return parsedObject;
    }

    /* Currently unused:
    public String stringify(Object object) {
        // Object to JSON-String
        String jsonString = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            new AlertDialog.Builder(context)
                    .setTitle(Config.JSON_PARSE_ALERT_TITLE)
                    .setMessage(Config.JSON_PARSE_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
        }
        return jsonString;
    }*/
}
