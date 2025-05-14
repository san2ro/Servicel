package com.arr.simple.helpers.profile.route;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileRoute {

    public static void createJson(Context context, Map<String, Object> map) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(map);
        File file = getDirectory(context);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(jsonString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> getExistingData(Context context) {
        File file = getDirectory(context);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] data = new byte[1024];
                int nRead;
                while ((nRead = fis.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                String json = buffer.toString("UTF-8");
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                return gson.fromJson(json, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new LinkedHashMap<>();
    }

    public static boolean deleteFileData(Context context) {
        try {
            File file = getDirectory(context); // Reutiliza tu método existente para obtener la ruta
            if (file != null && file.exists()) {
                return file.delete();
            }
            return false; // El archivo no existía
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean dataExist(Context context) {
        try {
            File file = getDirectory(context);
            return file != null && file.exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File getDirectory(Context context) {
        try {
            File file = context.getExternalFilesDir(".servicel");
            if (file != null && !file.exists()) {
                file.mkdirs();
            }
            return new File(file, "data.json");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
