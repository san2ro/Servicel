package cu.arr.etecsa.api.portal.models.cliente.operaciones;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class Parametros {

    @Json(name = "Cliente Id")
    public ClienteId clienteId;

    public Parametros(ClienteId clientId) {
        this.clienteId = clientId;
    }

    public ClienteId getClienteId() {
        return clienteId;
    }
}
