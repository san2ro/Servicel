package cu.arr.etecsa.api.portal.models.cliente.operaciones;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class ClienteId {

    @Json(name = "valor")
    public String valor;

    public ClienteId(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
