package cu.arr.etecsa.api.portal.models.servicios.navigation;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class ServiciosNavegacion {

    @Json(name = "perfil")
    public NavigationPerfil perfil;

    @Json(name = "tipoProducto")
    public String tipoProducto;

    public ServiciosNavegacion(NavigationPerfil perfil, String type) {
        this.perfil = perfil;
        this.tipoProducto = type;
    }

    public NavigationPerfil getPerfil() {
        return perfil;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }
}
