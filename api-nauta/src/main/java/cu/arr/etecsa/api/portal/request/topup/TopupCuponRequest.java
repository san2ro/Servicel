package cu.arr.etecsa.api.portal.request.topup;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

@Keep
public class TopupCuponRequest {

    private String url;
    private List<Param> param;

    public TopupCuponRequest(String account, String code) {
        this.url = "recargarConCupon";
        this.param = new ArrayList<>();
        this.param.add(new Param("accessLogin", "hidden", account));
        this.param.add(new Param("cardPassword", "STRING", code));
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
