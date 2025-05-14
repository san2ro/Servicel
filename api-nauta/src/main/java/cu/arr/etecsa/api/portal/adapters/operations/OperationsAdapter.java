package cu.arr.etecsa.api.portal.adapters.operations;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.models.operations.Data;
import cu.arr.etecsa.api.portal.models.operations.Detalle;
import cu.arr.etecsa.api.portal.models.operations.OperationId;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.operations.OperationsResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Keep
public class OperationsAdapter extends JsonAdapter<OperationsResponse> {
    private static final JsonReader.Options OPTIONS = JsonReader.Options.of("data");
    private static final JsonReader.Options DATA_OPTIONS =
            JsonReader.Options.of("resultado", "detalle");

    @Override
    public OperationsResponse fromJson(JsonReader reader) throws IOException {
        reader.beginObject();
        Data data = null;

        while (reader.hasNext()) {
            if (reader.selectName(OPTIONS) == 0) {
                data = parseData(reader);
            } else {
                reader.skipName();
                reader.skipValue();
            }
        }

        reader.endObject();
        return new OperationsResponse(data);
    }

    private Data parseData(JsonReader reader) throws IOException {
        String resultado = null;
        Detalle detalle = null;

        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.selectName(DATA_OPTIONS)) {
                case 0: // "resultado"
                    resultado = reader.nextString();
                    break;
                case 1: // "detalle"
                    detalle = parseDetalle(reader);
                    break;
                default:
                    reader.skipName();
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return new Data(resultado, detalle);
    }

    private Detalle parseDetalle(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case STRING:
                return new Detalle(reader.nextString());
            case BEGIN_OBJECT:
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<OperationId> adapter = moshi.adapter(OperationId.class);
                return new Detalle(adapter.fromJson(reader));
            default:
                reader.skipValue();
                return null;
        }
    }

    @Override
    public void toJson(JsonWriter writer, OperationsResponse value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }

        writer.beginObject();
        writer.name("data");
        writeData(writer, value.getData());
        writer.endObject();
    }

    private void writeData(JsonWriter writer, Data data) throws IOException {
        if (data == null) {
            writer.nullValue();
            return;
        }

        writer.beginObject();
        writer.name("resultado").value(data.getResultado());
        writer.name("detalle");
        writeDetalle(writer, data.getDetalle());
        writer.endObject();
    }

    private void writeDetalle(JsonWriter writer, Detalle detalle) throws IOException {
        if (detalle == null) {
            writer.nullValue();
        } else if (detalle.isString()) {
            writer.value(detalle.getAsString());
        } else if (detalle.isOperacion()) {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<OperationId> adapter = moshi.adapter(OperationId.class);
            adapter.toJson(writer, detalle.getAsOperacion());
        }
    }
}
