package cu.arr.etecsa.api.portal.models.servicios.correo;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class ServiciosCorreo {

    @Json(name = "perfil")
    public CorreoPerfil perfil;

    @Json(name = "tipoProducto")
    public String tipoProducto;

    public ServiciosCorreo(CorreoPerfil perfil, String tipo) {
        this.perfil = perfil;
        this.tipoProducto = tipo;
    }
}
