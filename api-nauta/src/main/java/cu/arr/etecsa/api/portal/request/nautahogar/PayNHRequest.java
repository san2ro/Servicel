package cu.arr.etecsa.api.portal.request.nautahogar;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class PayNHRequest {

    private String url;
    private List<Param> param;

    public PayNHRequest(String account, String monto) {
        this.url = "pagoOnlineCMNH";
        this.param = new ArrayList<>();
        this.param.add(new Param("cuentaAcceso", "CUENTA_ACCESO", account));
        this.param.add(new Param("monto", "MONEY", monto));
        this.param.add(new Param("pasarela", "SELECT", "TRANSFERMOVIL"));
    }

    @Keep
    private static class Param {

        private String name;
        private String type;
        private String value;

        public Param(String name, String type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }
}
