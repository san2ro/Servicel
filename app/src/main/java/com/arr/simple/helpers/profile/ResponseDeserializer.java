package com.arr.simple.helpers.profile;

import androidx.annotation.Keep;
import com.arr.simple.helpers.profile.models.Client;
import com.arr.simple.helpers.profile.models.Correo;
import com.arr.simple.helpers.profile.models.Cuentas;
import com.arr.simple.helpers.profile.models.Navegacion;
import com.arr.simple.helpers.profile.models.Response;
import com.arr.simple.helpers.profile.models.Telefono;
import com.arr.simple.helpers.profile.models.Updated;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Keep
public class ResponseDeserializer implements JsonDeserializer<Response> {
    @Override
    public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();
        Response response = new Response();
        response.telefonos = new HashMap<>();
        response.navegacion = new Navegacion();
        response.correo = new Correo();
        response.navegacion.account = new HashMap<>();
        response.correo.mail = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            try {
                if (key.equals("Cliente")) {
                    response.cliente = context.deserialize(value, Client.class);
                } else if (key.equals("Updated")) {
                    response.updated_plans = context.deserialize(value, Updated.class);
                } else if (key.equals("Navegación")) {
                    // "Navegación" es un objeto que contiene múltiples cuentas
                    JsonObject cuentasObj = value.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> cuentaEntry : cuentasObj.entrySet()) {
                        String email = cuentaEntry.getKey();
                        Cuentas cuenta = context.deserialize(cuentaEntry.getValue(), Cuentas.class);
                        response.navegacion.account.put(email, cuenta);
                    }
                } else if (key.equals("Correo")) {
                    JsonObject cuentasObj = value.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> cuentaEntry : cuentasObj.entrySet()) {
                        String email = cuentaEntry.getKey();
                        Correo.CuentaCorreo cuenta =
                                context.deserialize(
                                        cuentaEntry.getValue(), Correo.CuentaCorreo.class);
                        response.correo.mail.put(email, cuenta);
                    }
                } else {
                    // Asumimos que es un número de teléfono (como "5354250705")
                    try {
                        Telefono telefono = context.deserialize(value, Telefono.class);
                        response.telefonos.put(key, telefono);
                    } catch (JsonParseException e) {
                        System.err.println("No se pudo deserializar como Teléfono: " + key);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error procesando campo '" + key + "': " + e.getMessage());
            }
        }

        return response;
    }
}
