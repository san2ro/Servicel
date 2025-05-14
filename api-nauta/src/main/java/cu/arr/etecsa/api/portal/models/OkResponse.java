package cu.arr.etecsa.api.portal.models;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class OkResponse {

    @Json(name = "data")
    public Data data;

    @Keep
    public static class Data {
        @Json(name = "resultado")
        public String resultado;

        @Json(name = "detalle")
        public String detalle;

        public String getResult() {
            return resultado;
        }

        public String getDetalle() {
            return detalle;
        }
    }
}
