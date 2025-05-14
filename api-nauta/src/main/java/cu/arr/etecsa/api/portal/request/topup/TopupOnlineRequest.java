package cu.arr.etecsa.api.portal.request.topup;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class TopupOnlineRequest {

    private String url;
    private List<Param> param;

    public TopupOnlineRequest(String account, String monto) {
        this.url = "recargaOtraCuentaOnline";
        this.param = new ArrayList<>();
        this.param.add(new Param("accessLogin", "STRING", account));
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
