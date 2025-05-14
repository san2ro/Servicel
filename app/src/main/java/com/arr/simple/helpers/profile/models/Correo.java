package com.arr.simple.helpers.profile.models;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

@Keep
public class Correo {

    public Map<String, CuentaCorreo> mail;

    @Keep
    public static class CuentaCorreo {

        @SerializedName("cuenta")
        public String cuenta;

        @SerializedName("venta")
        public String venta;

        @SerializedName("id")
        public String id;
    }
}
