package cu.arr.etecsa.api.portal.models.servicios.mobile;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class ServiciosMoviles {

    @Json(name = "perfil")
    public MobilePerfil perfil;

    public ServiciosMoviles(MobilePerfil perfil) {
        this.perfil = perfil;
    }

    public MobilePerfil getPerfil() {
        return perfil;
    }
}
