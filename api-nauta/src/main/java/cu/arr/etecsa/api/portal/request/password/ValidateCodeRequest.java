package cu.arr.etecsa.api.portal.request.password;
import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class ValidateCodeRequest {
    
    private List<Param> param = new ArrayList<>();

    public ValidateCodeRequest(String username, String code) {
        this.param.add(new Param("usuarioPortal", username));
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
