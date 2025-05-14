package cu.arr.etecsa.api.portal.models.login;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class LoginDataJson {

    @Json(name = "resp")
    private LoginResponse response;

    public LoginResponse getResp() {
        return response;
    }
}
