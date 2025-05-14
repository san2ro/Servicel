package cu.arr.etecsa.api.portal.request.register;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class CodeRequest {

    private List<Param> param = new ArrayList<>();

    public CodeRequest(String ci, String code) {
        this.param.add(new Param("identidad", ci));
        this.param.add(new Param("codigoActivacion", code));
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
