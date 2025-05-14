package cu.arr.etecsa.api.portal.request.operations.navigation.accounts.newaccount;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class OpCreateAccount {

    private String url;
    private List<Param> param;

    public OpCreateAccount(String account, String password, String clientId) {
        this.url = "crearCuentaAcceso";
        this.param = new ArrayList<>();
        this.param.add(new Param("cuenta", "CUENTA_ACCESO", account));
        this.param.add(new Param("password", "NEWPASSWORD", password));
        this.param.add(new Param("clienteId", "hidden", clientId));
        this.param.add(new Param("especificacionProductoId", "hidden", "100"));
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
