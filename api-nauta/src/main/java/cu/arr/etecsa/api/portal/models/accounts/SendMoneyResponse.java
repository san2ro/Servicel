package cu.arr.etecsa.api.portal.models.accounts;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class SendMoneyResponse {

    @Json(name = "data")
    public Data data;

    @Keep
    public static class Data {
        
        @Json(name = "resultado")
        public String resultado;

        @Json(name = "detalle")
        public Detalle detalle;

        public String getResult() {
            return resultado;
        }
    }

    @Keep
    public static class Detalle {
        
        @Json(name = "operacionId")
        public String operationId;

        public String getOperationId() {
            return operationId;
        }
    }
}
