package cu.arr.etecsa.api.portal.models.profile;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class UpdateProfileResult {

    @Json(name = "resultado")
    public String result;

    public String getResultado() {
        return result;
    }
}
