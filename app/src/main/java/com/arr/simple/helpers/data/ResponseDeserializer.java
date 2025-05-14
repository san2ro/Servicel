package com.arr.simple.helpers.data;

import androidx.annotation.Keep;
import com.arr.simple.helpers.data.models.Client;
import com.arr.simple.helpers.data.models.Response;
import com.arr.simple.helpers.data.models.Telefono;
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

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (key.equals("Cliente")) {
                response.cliente = context.deserialize(value, Client.class);
            } else if (key.equals("updated_plans")) {
                response.updated_plans = value.getAsBoolean();
            } else {
                try {
                    Telefono telefono = context.deserialize(value, Telefono.class);
                    response.telefonos.put(key, telefono);
                } catch (JsonParseException e) {
                    // Manejar el caso donde no es un teléfono válido
                    e.printStackTrace();
                }
            }
        }

        return response;
    }
}
