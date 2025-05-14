package cu.arr.etecsa.api.portal.request.password;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class ResetPassRequest {

    private List<Param> param = new ArrayList<>();

    public ResetPassRequest(String username, String password, String code) {
        this.param.add(new Param("cuenta", username));
        this.param.add(new Param("password", password));
        this.param.add(new Param("codigoActivacion", code));
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
