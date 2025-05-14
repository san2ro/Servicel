package cu.arr.etecsa.api.portal.request.register;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;
import java.util.ArrayList;
import java.util.List;

@Keep
public class RegisterRequest {

    private List<Param> param;
    private String idRequest;

    @Json(name = "captchatext")
    private String captcha;

    public RegisterRequest(String username, String dni, String captcha, String idRequest) {
        this.param = new ArrayList<>();
        this.param.add(new Param("via", "SERVICIO_MOVIL"));
        this.param.add(new Param("noIdentidad", dni));
        this.param.add(new Param("servicio", username));
        this.idRequest = idRequest;
        this.captcha = captcha;
    }

    @Keep
    public static class Param {
        private String name;
        private String value;

        public Param(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
