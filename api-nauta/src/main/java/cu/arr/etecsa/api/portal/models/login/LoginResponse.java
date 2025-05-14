package cu.arr.etecsa.api.portal.models.login;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;
import cu.arr.etecsa.api.portal.models.User;

@Keep
public class LoginResponse {

    @Json(name = "token")
    private String token;

    @Json(name = "user")
    private Object user;

    @Json(name = "resultado")
    private String resultado;

    public LoginResponse(String token, Object user, String result) {
        this.token = token;
        this.user = user;
        this.resultado = result;
    }

    public String getToken() {
        return token;
    }

    public Object getUser() {
        return user;
    }

    public String getResultado() {
        return resultado;
    }
}
