package cu.arr.etecsa.api.portal.models.servicios.mobile;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class Bono {

    @Json(name = "tipo")
    public String tipo;

    @Json(name = "Fecha inicio")
    public String fechaInicio;

    @Json(name = "Vence")
    public String vence;

    @Json(name = "Datos")
    public String datos;

    public Bono(String tipo, String fechaInicio, String vence, String datos) {
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.vence = vence;
        this.datos = datos;
    }
}
