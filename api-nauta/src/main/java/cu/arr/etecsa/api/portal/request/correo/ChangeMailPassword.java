package cu.arr.etecsa.api.portal.request.correo;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class ChangeMailPassword {

    private String url;
    private List<Param> param;

    public ChangeMailPassword(String account, String oldPassword, String newPassword) {
        this.url = "changeMailPassword";
        this.param = new ArrayList<>();
        this.param.add(new Param("cuenta", "hidden", account));
        this.param.add(new Param("oldPassword", "PASSWORD", oldPassword));
        this.param.add(new Param("newPassword", "NEWPASSWORD", newPassword));
    }

    @Keep
    private static class Param {
        public String name;
        public String type;
        public String value;

        public Param(String name, String type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }
}
