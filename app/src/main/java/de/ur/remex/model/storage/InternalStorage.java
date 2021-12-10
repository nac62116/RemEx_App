package de.ur.remex.model.storage;

import android.app.AlertDialog;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipInputStream;

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

public class InternalStorage {

    private final Context context;

    public InternalStorage(Context context) {
        this.context = context;
    }

    public void saveFileContentString(String fileName, String content) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        }
        catch (Exception e){
            new AlertDialog.Builder(context)
                    .setTitle(Config.INTERNAL_STORAGE_SAVING_ALERT_TITLE)
                    .setMessage(Config.INTERNAL_STORAGE_SAVING_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
        }
    }

    public String getFileContentString(String fileName) {
        FileInputStream fis;
        InputStreamReader inputStreamReader;
        String content;

        try {
            fis = context.openFileInput(fileName);
            inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            new AlertDialog.Builder(context)
                    .setTitle(Config.INTERNAL_STORAGE_LOADING_ALERT_TITLE)
                    .setMessage(Config.INTERNAL_STORAGE_LOADING_ALERT_MESSAGE)
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
                    .setTitle(Config.INTERNAL_STORAGE_READING_ALERT_TITLE)
                    .setMessage(Config.INTERNAL_STORAGE_READING_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
            return null;
        } finally {
            content = stringBuilder.toString();
        }
        return content;
    }

    public void saveZipEntry(String fileName, ZipInputStream zis) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            byte[] buffer = new byte[Config.RESOURCE_UPLOAD_BUFFER_LENGTH];
            int length = zis.read(buffer);
            while (length > 0) {
                fos.write(buffer, 0, length);
                length = zis.read(buffer);
            }
        }
        catch (Exception e){
            new AlertDialog.Builder(context)
                    .setTitle(Config.INTERNAL_STORAGE_SAVING_ALERT_TITLE)
                    .setMessage(Config.INTERNAL_STORAGE_SAVING_ALERT_MESSAGE)
                    .setPositiveButton(Config.OK, null)
                    .show();
        }
    }

    public File getFile(String fileName) {
        return new File(context.getFilesDir() + "/" + fileName);
    }

    public void clear() {
        for (String fileName: Objects.requireNonNull(context.getFilesDir().list())) {
            context.deleteFile(fileName);
        }
    }

    public boolean fileExists(String fileName){
        File file = context.getFileStreamPath(fileName);
        return file.exists();
    }

}
