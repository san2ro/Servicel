package cu.arr.etecsa.api.portal.adapters.telephony;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import cu.arr.etecsa.api.portal.models.servicios.telephony.TFPerfil;
import java.io.IOException;

@Keep
public class TFPerfilAdapter extends JsonAdapter<TFPerfil> {

    @Override
    public TFPerfil fromJson(JsonReader reader) throws IOException {
        String id = null;
        String numero = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    id = reader.nextString();
                    break;
                case "Número de teléfono":
                    numero = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new TFPerfil(numero, id);
    }

    @Override
    public void toJson(JsonWriter arg0, TFPerfil arg1) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
