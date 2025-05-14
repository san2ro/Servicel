package cu.arr.etecsa.api.portal.request.logout;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class LogoutRequest {

    @Json(name = "usuarioPortal")
    public String usuarioPortal;

    public LogoutRequest(String usuarioPortal) {
        this.usuarioPortal = usuarioPortal;
    }
}
