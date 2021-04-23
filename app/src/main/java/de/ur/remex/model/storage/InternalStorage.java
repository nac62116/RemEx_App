package de.ur.remex.model.storage;

import android.app.AlertDialog;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import de.ur.remex.Config;

public class InternalStorage {

    private final Context context;

    public InternalStorage(Context context) {
        this.context = context;
    }

    public void saveFileContent(String fileName, String content) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        }
        catch (Exception e){
            new AlertDialog.Builder(context)
                    .setTitle(Config.INTERNAL_STORAGE_SAVING_ERROR_TITLE)
                    .setMessage(Config.INTERNAL_STORAGE_SAVING_ERROR_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
        }
    }

    public String getFileContent(String fileName) {
        FileInputStream fis;
        InputStreamReader inputStreamReader;
        String content;

        try {
            fis = context.openFileInput(fileName);
            inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            new AlertDialog.Builder(context)
                    .setTitle(Config.INTERNAL_STORAGE_LOADING_ERROR_TITLE)
                    .setMessage(Config.INTERNAL_STORAGE_LOADING_ERROR_MESSAGE)
                    .setPositiveButton(Config.OK, null)
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
            new AlertDialog.Builder(context)
                    .setTitle(Config.INTERNAL_STORAGE_READING_ERROR_TITLE)
                    .setMessage(Config.INTERNAL_STORAGE_READING_ERROR_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
            return null;
        } finally {
            content = stringBuilder.toString();
        }
        return content;
    }

    public boolean fileExists(String fileName){
        File file = context.getFileStreamPath(fileName);
        return file.exists();
    }
}
