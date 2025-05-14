package cu.arr.etecsa.api.portal.models.operations;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class OperationId {

    @Json(name = "operacionId")
    private String operacionId;

    public String getOperacionId() {
        return operacionId;
    }
}
