package cu.arr.etecsa.api.portal.models.operations;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.JsonReader;

@Keep
public class OperationsResponse {
    @Json(name = "data")
    private final Data data;

    public OperationsResponse(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }
}
