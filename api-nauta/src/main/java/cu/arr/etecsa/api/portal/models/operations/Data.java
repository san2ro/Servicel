package cu.arr.etecsa.api.portal.models.operations;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class Data {

    @Json(name = "resultado")
    private final String resultado;

    @Json(name = "detalle")
    private final Detalle detalle;

    public Data(String resultado, Detalle detalle) {
        this.resultado = resultado;
        this.detalle = detalle;
    }

    public String getResultado() {
        return resultado;
    }

    public Detalle getDetalle() {
        return detalle;
    }
}
