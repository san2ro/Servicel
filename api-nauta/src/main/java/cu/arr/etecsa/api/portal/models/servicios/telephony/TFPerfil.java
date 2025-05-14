package cu.arr.etecsa.api.portal.models.servicios.telephony;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class TFPerfil {

    @Json(name = "Número de teléfono")
    public String numero;

    public String id;

    public TFPerfil(String phone, String id) {
        this.numero = phone;
        this.id = id;
    }
}
