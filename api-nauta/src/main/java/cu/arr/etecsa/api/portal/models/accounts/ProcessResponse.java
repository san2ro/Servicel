package cu.arr.etecsa.api.portal.models.accounts;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class ProcessResponse {

    @Json(name = "data")
    public Data data;

    @Keep
    public static class Data {

        @Json(name = "estado")
        public String estado;

        @Json(name = "resultado")
        public String resultado;

        @Json(name = "detalle")
        public String detalle;

        public String getResult() {
            return resultado;
        }
    }
}
