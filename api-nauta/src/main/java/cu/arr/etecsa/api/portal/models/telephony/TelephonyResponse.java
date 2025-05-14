package cu.arr.etecsa.api.portal.models.telephony;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class TelephonyResponse {

    @Json(name = "data")
    public Data data;

    @Keep
    public static class Data {

        @Json(name = "resultado")
        public String resultado;

        @Json(name = "detalle")
        public Details detalle;
    }

    @Keep
    public static class Details {

        @Json(name = "cliente")
        public Client cliente;

        @Json(name = "factura")
        public Factura factura;

        @Json(name = "generales")
        public General general;
    }

    @Keep
    public static class Client {

        @Json(name = "Nombre")
        public String nombre;

        @Json(name = "Direccion")
        public String adress;

        @Json(name = "Zona Postal")
        public String sipZone;

        @Json(name = "Número de Cliente")
        public String clientNumber;
    }

    @Keep
    public static class Factura {

        @Json(name = "Fecha de vencimiento")
        public String expire;

        @Json(name = "Período de consumo")
        public String periodo;

        @Json(name = "Cod. pago cajero")
        public String idCajero;
    }

    @Keep
    public static class General {
        @Json(name = "Total a Pagar")
        public String total;
    }
}
