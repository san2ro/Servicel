package cu.arr.etecsa.api.portal.request.users;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class UsersRequest {

    private String email;

    @Json(name = "ultimaActualizacion")
    private String lastUpdated;

    public UsersRequest(String usuarioPortal, String lastUpdated) {
        this.email = usuarioPortal;
        this.lastUpdated = lastUpdated;
    }
}
