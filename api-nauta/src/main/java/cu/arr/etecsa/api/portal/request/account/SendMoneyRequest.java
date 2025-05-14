package cu.arr.etecsa.api.portal.request.account;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class SendMoneyRequest {

    private String url;
    private List<Param> param;

    public SendMoneyRequest(String account, String sendTo, String password, String monto) {
        this.url = "transferirSaldo";
        this.param = new ArrayList<>();
        this.param.add(new Param("accessLogin", "hidden", account));
        this.param.add(new Param("targetLogin", "STRING", sendTo));
        this.param.add(new Param("accessPassword", "PASSWORD", password));
        this.param.add(new Param("monto", "MONEY", monto));
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
