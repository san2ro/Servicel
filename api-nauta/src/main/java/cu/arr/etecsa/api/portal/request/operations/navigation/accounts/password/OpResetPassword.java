package cu.arr.etecsa.api.portal.request.operations.navigation.accounts.password;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class OpResetPassword {

    private String url;
    private List<Param> param;

    public OpResetPassword(String account, String oldPassword, String newPassword) {
        this.url = "changeAccessPassword";
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
