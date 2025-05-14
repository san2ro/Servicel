package cu.arr.etecsa.api.portal.models.servicios.telephony;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class ServicioTelFija {

    @Json(name = "perfil")
    private TFPerfil perfil;

    public ServicioTelFija(TFPerfil perfil) {
        this.perfil = perfil;
    }
}
