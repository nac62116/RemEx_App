package de.ur.remex.utilities;

import android.app.AlertDialog;
import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;

import de.ur.remex.Config;

/*
MIT License

Copyright (c) 2021 Colin Nash

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

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
                new AlertDialog.Builder(context)
                        .setTitle(Config.JSON_PARSE_ALERT_TITLE)
                        .setMessage(Config.JSON_PARSE_ALERT_MESSAGE)
                        .setPositiveButton(Config.OK, null)
                        .show();
            }
        }
        return parsedObject;
    }
}
