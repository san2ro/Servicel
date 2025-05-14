package cu.arr.etecsa.api.portal.models.cliente.operaciones;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class Operaciones {

    @Json(name = "parametros")
    public Parametros parametros;

    public Operaciones(Parametros parametros) {
        this.parametros = parametros;
    }

    public Parametros getParametros() {
        return parametros;
    }
}
