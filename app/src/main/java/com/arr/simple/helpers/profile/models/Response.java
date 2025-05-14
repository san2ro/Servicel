package com.arr.simple.helpers.profile.models;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

@Keep
public class Response {

    @SerializedName("Cliente")
    public Client cliente;

    @SerializedName("Updated")
    public Updated updated_plans;

    @SerializedName("Navegación")
    public Navegacion navegacion;

    @SerializedName("Correo")
    public Correo correo;

    public Map<String, Telefono> telefonos;
}
