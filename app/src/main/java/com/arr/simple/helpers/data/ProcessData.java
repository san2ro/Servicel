package com.arr.simple.helpers.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Keep;

import com.arr.simple.helpers.data.models.Response;
import com.arr.simple.helpers.data.models.Telefono;
import com.arr.simple.helpers.data.models.Values;
import com.arr.simple.models.ListItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Keep
public class ProcessData {

    private static Response cachedResponse = null;
    private static long lastCacheTime = 0;
    private static final long CACHE_EXPIRY_MS = 5000; // 5 segundos

    public static void clearCache() {
        cachedResponse = null;
        lastCacheTime = 0;
    }

    private static Response getCachedResponse(Context context) throws IOException {
        long currentTime = System.currentTimeMillis();
        if (cachedResponse == null || (currentTime - lastCacheTime) > CACHE_EXPIRY_MS) {
            Gson gson =
                    new GsonBuilder()
                            .registerTypeAdapter(Response.class, new ResponseDeserializer())
                            .create();

            try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
                String jsonContent = new Scanner(reader).useDelimiter("\\Z").next();
                cachedResponse = gson.fromJson(jsonContent, Response.class);
                lastCacheTime = currentTime;
            }
        }
        return cachedResponse;
    }

    // Obtener todos los números de teléfono disponibles
    public static List<String> getPhoneNumbers(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            String jsonContent = new Scanner(reader).useDelimiter("\\Z").next();
            Response resp = gson.fromJson(jsonContent, Response.class);
            if (resp.telefonos != null) {
                Log.d("JSON_DEBUG", "Números encontrados: " + resp.telefonos.keySet());
                return new ArrayList<>(resp.telefonos.keySet());
            }
        } catch (IOException e) {
            Log.e("JSON_DEBUG", "Error al leer el JSON", e);
        }
        return Collections.emptyList();
    }

    // Obtener saldo para un número específico
    public static String getSaldoForNumber(Context context, String phoneNumber) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phoneNumber)) {
                return resp.telefonos.get(phoneNumber).saldo + " CUP";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // obtener si se actualizó planes o no
    public static boolean updatedPlans(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            return resp.updated_plans;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getPhone(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.cliente != null) {
                return resp.cliente.movil;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "sin datos";
    }

    public static String getEmail(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.cliente != null) {
                if (!TextUtils.isEmpty(resp.cliente.email)) {
                    return resp.cliente.email;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No disponible";
    }

    public static String getNotificationEmail(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.cliente != null) {
                switch (resp.cliente.notifi_mail) {
                    case "true":
                        return "Activado";
                }
                return "Desactivado";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "sin datos";
    }

    public static String getNotificationSms(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.cliente != null) {
                switch (resp.cliente.notifi_movil) {
                    case "true":
                        return "Activado";
                }
                return "Desactivado";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "sin datos";
    }

    // Obtener planes DATOS para un número específico
    public static Pair<String, String> getPlanDatosForNumber(Context context, String phoneNumber) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phoneNumber)) {
                Telefono telefono = resp.telefonos.get(phoneNumber);
                if (telefono.Planes != null && telefono.Planes.containsKey("DATOS")) {
                    Values plan = telefono.Planes.get("DATOS");
                    return new Pair<>(plan.dispone, plan.vence);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>("0 B", "0 B");
    }

    // Obtener planes DATOS LTE para un número específico
    public static Pair<String, String> getDatosLTEForNumber(Context context, String phoneNumber) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phoneNumber)) {
                Telefono telefono = resp.telefonos.get(phoneNumber);
                if (telefono.Planes != null && telefono.Planes.containsKey("DATOS LTE")) {
                    Values plan = telefono.Planes.get("DATOS LTE");
                    return new Pair<>(plan.dispone, plan.vence);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>("0 B", "0 B");
    }

    // Obtener bonos para un número específico
    public static Map<String, Pair<String, String>> getBonosForNumber(
            Context context, String phone) {
        Map<String, Pair<String, String>> bonos = new HashMap<>();
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phone)) {
                Telefono telefono = resp.telefonos.get(phone);
                if (telefono.Bonos != null) {
                    for (Map.Entry<String, Values> entry : telefono.Bonos.entrySet()) {
                        bonos.put(
                                entry.getKey(),
                                new Pair<>(entry.getValue().dispone, entry.getValue().vence));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bonos;
    }

    // Los métodos para obtener datos del cliente pueden permanecer igual
    public static String getNameUser(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.cliente != null) {
                return resp.cliente.nombre;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "null";
    }
    /*
        public static List<ListItem> getDataForRecyclerView(Context context, String phoneNumber) {
            List<ListItem> items = new ArrayList<>();
            Gson gson =
                    new GsonBuilder()
                            .registerTypeAdapter(Response.class, new ResponseDeserializer())
                            .create();

            try (FileReader reader = new FileReader(StorageRoute.getDirectory(context))) {
                Response resp = gson.fromJson(reader, Response.class);
                if (resp.telefonos != null && resp.telefonos.containsKey(phoneNumber)) {
                    Telefono telefono = resp.telefonos.get(phoneNumber);

                    boolean hasPlanes = telefono.Planes != null && !telefono.Planes.isEmpty();
                    boolean hasBonos = telefono.Bonos != null && !telefono.Bonos.isEmpty();

                    // Si no hay datos en Planes ni Bonos, retornar null
                    if (!hasPlanes && !hasBonos) {
                        return null;
                    }

                    // Procesar Planes (si existen)
                    if (hasPlanes) {
                        items.add(new ListItem("Planes")); // Header
                        for (Map.Entry<String, Values> entry : telefono.Planes.entrySet()) {
                            items.add(
                                    new ListItem(
                                            entry.getKey(),
                                            entry.getValue().dispone,
                                            entry.getValue().vence));
                        }
                    }

                    // Procesar Bonos (si existen)
                    if (hasBonos) {
                        items.add(new ListItem("Bonos")); // Header
                        for (Map.Entry<String, Values> entry : telefono.Bonos.entrySet()) {
                            items.add(
                                    new ListItem(
                                            entry.getKey(),
                                            entry.getValue().dispone,
                                            entry.getValue().vence));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return items.isEmpty() ? null : items; // Si no se agregó nada, retornar null
        }
    */
}
