package cu.arr.etecsa.api.portal.request.password;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;
import java.util.ArrayList;
import java.util.List;

@Keep
public class ValidateUserRequest {

    private List<Param> param = new ArrayList<>();
    private String idRequest;

    @Json(name = "captchatext")
    private String captchaText;

    public ValidateUserRequest(String username, String idRequest, String captcha) {
        this.param.add(new Param("usuarioPortal", username));
        this.idRequest = idRequest;
        this.captchaText = captcha;
    }

    @Keep
    private static class Param {
        private String name;
        private String value;

        public Param(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
