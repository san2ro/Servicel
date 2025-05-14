package com.arr.simple.helpers.profile;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.Keep;
import com.arr.simple.helpers.profile.models.Correo;
import com.arr.simple.helpers.profile.models.Cuentas;
import com.arr.simple.helpers.profile.models.Response;
import com.arr.simple.helpers.profile.models.Telefono;
import com.arr.simple.helpers.profile.models.Values;
import com.arr.simple.helpers.profile.route.FileRoute;
import com.arr.simple.models.ListItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cu.arr.etecsa.api.portal.models.servicios.mobile.Plan;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Keep
public class ProcessProfile {

    private static Response cachedResponse = null;
    private static long lastCacheTime = 0;
    private static final long CACHE_EXPIRY_MS = 5000;

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

            try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
                String jsonContent = new Scanner(reader).useDelimiter("\\Z").next();
                cachedResponse = gson.fromJson(jsonContent, Response.class);
                lastCacheTime = currentTime;
            }
        }
        return cachedResponse;
    }

    public static String getNameUser(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.cliente != null) {
                return resp.cliente.nombre;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Usuario";
    }

    public static String getPhone(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
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
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
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
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
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
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
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

    public static String getClientId(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.cliente != null) {
                return resp.cliente.clientId;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSaldoForNumber(Context context, String phoneNumber) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phoneNumber)) {
                return resp.telefonos.get(phoneNumber).saldo + " CUP";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0.00 CUP";
    }

    public static String getStatusAdelanta(Context context, String phoneNumber) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phoneNumber)) {
                return resp.telefonos.get(phoneNumber).adelanta;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean updatedPlans(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            return resp.updated_plans.updated;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isServicesUpdated(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            return resp.telefonos.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getPlans(Context context, String phone) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phone)) {
                Telefono telefono = resp.telefonos.get(phone);
                if (telefono.Planes != null) {
                    for (Map.Entry<String, Values> entry : telefono.Planes.entrySet()) {
                        return entry.getValue().plan;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "0 B";
    }

    public static boolean isEmptyPlansOrBonos(Context context, String phone) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();

        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phone)) {
                Telefono telefono = resp.telefonos.get(phone);
                // Retorna true SOLO si AMBAS listas están vacías o son nulas
                return (telefono.Planes == null || telefono.Planes.isEmpty())
                        && (telefono.Bonos == null || telefono.Bonos.isEmpty());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return true; // En caso de error, asume que no hay datos
        }
        return false; // Teléfono no encontrado
    }

    public static List<String> getPhoneNumbers(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            String jsonContent = new Scanner(reader).useDelimiter("\\Z").next();
            Response resp = gson.fromJson(jsonContent, Response.class);
            if (resp.telefonos != null) {
                Log.d("JSON_DEBUG", "Números encontrados: " + resp.telefonos.keySet());
                return new ArrayList<>(resp.telefonos.keySet());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("JSON_DEBUG", "Error al leer el JSON", e);
        }
        return Collections.emptyList();
    }

    public static List<ListItem> getDataForRecyclerView(Context context, String phoneNumber) {
        List<ListItem> items = new ArrayList<>();
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();

        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);
            if (resp.telefonos != null && resp.telefonos.containsKey(phoneNumber)) {
                Telefono telefono = resp.telefonos.get(phoneNumber);

                // Procesar Planes
                processSection(items, telefono.Planes, "");

                // Procesar Bonos
                processSection(items, telefono.Bonos, "Bonos");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    /** Método auxiliar para procesar una sección (Planes/Bonos) y agregar items ordenados. */
    private static void processSection(
            List<ListItem> items, Map<String, Values> sectionData, String sectionTitle) {
        if (sectionData != null && !sectionData.isEmpty()) {
            if (!sectionTitle.isEmpty()) {
                items.add(new ListItem(sectionTitle));
            }
            // Encabezado de sección

            // Convertir a lista ordenable
            List<Map.Entry<String, Values>> entries = new ArrayList<>(sectionData.entrySet());

            // Ordenar con prioridad para "DATOS" y "DATOS LTE"
            Collections.sort(
                    entries,
                    new Comparator<Map.Entry<String, Values>>() {
                        @Override
                        public int compare(
                                Map.Entry<String, Values> e1, Map.Entry<String, Values> e2) {
                            String key1 = e1.getKey().toUpperCase();
                            String key2 = e2.getKey().toUpperCase();

                            // Prioridad 1: "DATOS"
                            if (key1.equals("DATOS")) return -1;
                            if (key2.equals("BONO DATOS")) return 1;

                            // Prioridad 2: "DATOS LTE"
                            if (key1.equals("DATOS LTE")) return -1;
                            if (key2.equals("DATOS NACIONALES")) return 1;

                            // Orden alfabético para el resto
                            return key1.compareTo(key2);
                        }
                    });

            // Agregar items ordenados
            for (Map.Entry<String, Values> entry : entries) {
                items.add(
                        new ListItem(
                                entry.getKey(),
                                entry.getValue().dispone,
                                entry.getValue().plan,
                                entry.getValue().vence));
            }
        }
    }

    // obtener listas de cuentas
    public static List<String> getAccounts(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            String jsonContent = new Scanner(reader).useDelimiter("\\Z").next();
            Response resp = gson.fromJson(jsonContent, Response.class);
            if (resp.navegacion.account != null) {
                return new ArrayList<>(resp.navegacion.account.keySet());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static Map<String, Cuentas> getCuentas(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            String jsonContent = new Scanner(reader).useDelimiter("\\Z").next();
            Response resp = gson.fromJson(jsonContent, Response.class);
            if (resp.navegacion.account != null) {
                return resp.navegacion.account;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    // cuentas de correo
    public static Map<String, Correo.CuentaCorreo> getCuentasCorreo(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            String jsonContent = new Scanner(reader).useDelimiter("\\Z").next();
            Response resp = gson.fromJson(jsonContent, Response.class);
            if (resp.correo.mail != null) {
                return resp.correo.mail;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    // lista de correos
    public static List<String> getCorreos(Context context) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();
        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            String jsonContent = new Scanner(reader).useDelimiter("\\Z").next();
            Response resp = gson.fromJson(jsonContent, Response.class);
            if (resp.correo.mail != null) {
                return new ArrayList<>(resp.correo.mail.keySet());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static Map<String, String> getDataPlans(Context context, String phoneNumber) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();

        Map<String, String> result = new HashMap<>();
        result.put("DATOS", "-");
        result.put("DATOS_LTE", "-");
        result.put("DATOS_VENCE", "-");

        try (FileReader reader = new FileReader(FileRoute.getDirectory(context))) {
            Response resp = gson.fromJson(reader, Response.class);

            // Buscar el teléfono por el número completo
            Telefono telefono = resp.telefonos.get(phoneNumber);
            if (telefono != null && telefono.Planes != null) {
                // Obtener DATOS
                Values datos = telefono.Planes.get("DATOS");
                if (datos != null && datos.dispone != null) {
                    result.put("DATOS", datos.dispone);
                    result.put("DATOS_VENCE", datos.vence);
                }

                // Obtener DATOS LTE
                Values datosLte = telefono.Planes.get("DATOS LTE");
                if (datosLte != null && datosLte.dispone != null) {
                    result.put("DATOS_LTE", datosLte.dispone);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
