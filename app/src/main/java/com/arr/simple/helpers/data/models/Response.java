package com.arr.simple.helpers.data.models;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

@Keep
public class Response {

    @SerializedName("Cliente")
    public Client cliente;

    public boolean updated_plans;
    
    public Map<String, Telefono> telefonos;
}
