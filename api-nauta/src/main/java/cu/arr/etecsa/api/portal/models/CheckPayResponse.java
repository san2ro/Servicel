package cu.arr.etecsa.api.portal.models;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class CheckPayResponse {

    @Json(name = "data")
    public Data data;

    @Keep
    public static class Data {
        
        @Json(name = "resultado")
        public String resultado;

        @Json(name = "detalle")
        public String detalle;
    }
}
