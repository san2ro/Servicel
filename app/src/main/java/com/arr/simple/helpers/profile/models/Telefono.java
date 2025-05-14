package com.arr.simple.helpers.profile.models;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

@Keep
public class Telefono {

    @SerializedName("número")
    public String numero;

    @SerializedName("saldo")
    public String saldo;

    @SerializedName("adelanta")
    public String adelanta;

    @SerializedName("Planes")
    public Map<String, Values> Planes;

    @SerializedName("Bonos")
    public Map<String, Values> Bonos;
}
