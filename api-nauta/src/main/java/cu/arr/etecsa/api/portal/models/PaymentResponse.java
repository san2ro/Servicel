package cu.arr.etecsa.api.portal.models;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class PaymentResponse {

    @Json(name = "data")
    public Data data;

    @Keep
    public static class Data {
        @Json(name = "resultado")
        public String resultado;

        @Json(name = "detalle")
        public Detalle detalle;

        @Json(name = "info")
        public Info info;
    }

    @Keep
    public static class Detalle {

        @Json(name = "id_transaccion")
        public String idTransaccion;

        @Json(name = "importe")
        public double importe;

        @Json(name = "moneda")
        public String moneda;

        @Json(name = "numero_proveedor")
        public String numeroProveedor;

        @Json(name = "version")
        public String version;

        public String getTransactionId() {
            return idTransaccion;
        }

        public double getImporte() {
            return importe;
        }

        public String getMoneda() {
            return idTransaccion;
        }

        public String getNumeroProveedor() {
            return numeroProveedor;
        }

        public String getVersion() {
            return version;
        }
    }

    @Keep
    public static class Info {
        @Json(name = "descuento")
        public double descuento;

        @Json(name = "monto_pagado")
        public String montoPagado;

        @Json(name = "monto_final")
        public String montoFinal;

        public double getDescuento() {
            return descuento;
        }

        public String getMontoPagado() {
            return montoPagado;
        }

        public String getMontoFinal() {
            return montoFinal;
        }
    }
}
