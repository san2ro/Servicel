package com.arr.simple.helpers.profile.models;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;

@Keep
public class Cuentas {

    @SerializedName("cuenta")
    public String cuenta;

    @SerializedName("estado")
    public String status;

    @SerializedName("saldo")
    public String saldo;

    @SerializedName("tipo")
    public String tipo;

    @SerializedName("eliminación")
    public String delete;

    @SerializedName("bloqueo")
    public String bloqueo;

    @SerializedName("venta")
    public String venta;

    @SerializedName("horas_de_bonificación")
    public String horas_bono;

    @SerializedName("bonificación")
    public String bono;
}
