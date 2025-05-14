package cu.arr.etecsa.api.portal.models.servicios.mobile;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class Plan {

    @Json(name = "tipo")
    public String tipo;

    @Json(name = "Vence")
    public String vence;

    @Json(name = "Datos")
    public String datos;

    public Plan(String tipo, String vence, String datos) {
        this.tipo = tipo;
        this.vence = vence;
        this.datos = datos;
    }
}
