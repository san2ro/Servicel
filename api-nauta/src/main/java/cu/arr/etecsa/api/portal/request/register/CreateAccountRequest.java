package cu.arr.etecsa.api.portal.request.register;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class CreateAccountRequest {

    private List<Param> param = new ArrayList<>();

    public CreateAccountRequest(String username, String password, String ci) {
        this.param.add(new Param("usuario", username));
        this.param.add(new Param("password", password));
        this.param.add(new Param("noIdentidad", ci));
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
