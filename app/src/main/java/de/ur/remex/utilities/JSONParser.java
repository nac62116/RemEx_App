package de.ur.remex.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.ur.remex.Config;

public class JSONParser {

    private final Context context;

    public JSONParser(Context context) {
        this.context = context;
    }

    public Object parseJSONString(String JSONString, Class<?> targetClass) {
        // JSON-String to target class object
        Object parsedObject = null;
        if (JSONString != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                parsedObject = mapper.readValue(JSONString, targetClass);
            }
            catch (JsonProcessingException e) {
                Log.e("Parser exception", e.toString());
                new AlertDialog.Builder(context)
                        .setTitle(Config.JSON_PARSE_ALERT_TITLE)
                        .setMessage(Config.JSON_PARSE_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
        return parsedObject;
    }

    public void parseInputStream(InputStream inputStream) {
        // InputStream to jsonMap
        HashMap<?, ?> jsonMap = new HashMap<>();
        if (inputStream != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                jsonMap = mapper.readValue(inputStream, HashMap.class);
            }
            catch (Exception e) {
                new AlertDialog.Builder(context)
                        .setTitle(Config.JSON_PARSE_ALERT_TITLE)
                        .setMessage(Config.JSON_PARSE_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
        if (jsonMap != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                jsonMap.forEach((key, value) -> {
                    Log.e("Key", key.toString());
                    Log.e("Value", value.toString());
                });
            }
        }
    }

    public String stringify(Object object) {
        // Object to JSON-String
        String jsonString = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            new AlertDialog.Builder(context)
                    .setTitle(Config.JSON_STRINGIFY_ALERT_TITLE)
                    .setMessage(Config.JSON_STRINGIFY_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
        }
        return jsonString;
    }
}
