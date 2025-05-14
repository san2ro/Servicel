package cu.arr.etecsa.api.portal.request.login;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class LoginRequest {

    private String username;
    private String password;
    private String idRequest;

    @Json(name = "tipoCuenta")
    private String tipoCuenta;

    @Json(name = "captchatext")
    private String captcha;

    public LoginRequest(String username, String password, String idRequest, String captcha) {
        this.username = username;
        this.password = password;
        this.idRequest = idRequest;
        this.tipoCuenta = "USUARIO_PORTAL";
        this.captcha = captcha;
    }
}
