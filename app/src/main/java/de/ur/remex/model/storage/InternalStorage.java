package de.ur.remex.model.storage;

import android.app.AlertDialog;
import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import de.ur.remex.R;

public class InternalStorage {

    public void saveFileContent(Context context, String fileName, String content) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        }
        catch (Exception e){
            // AlertDialog: Systemfehler
            new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.fos_alert_title))
                    .setMessage(context.getResources().getString(R.string.fos_alert_text))
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

    public String getFileContent(Context context, String fileName) {
        FileInputStream fis;
        InputStreamReader inputStreamReader;
        String content;

        try {
            fis = context.openFileInput(fileName);
            inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            // AlertDialog: Systemfehler
            new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.fis_alert_title))
                    .setMessage(context.getResources().getString(R.string.fis_alert_text))
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            // AlertDialog: Systemfehler
            new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.buffered_reader_alert_title))
                    .setMessage(context.getResources().getString(R.string.buffered_reader_alert_text))
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return null;
        } finally {
            content = stringBuilder.toString();
        }
        return content;
    }
}
