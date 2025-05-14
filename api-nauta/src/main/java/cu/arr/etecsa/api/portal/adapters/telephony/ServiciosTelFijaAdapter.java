package cu.arr.etecsa.api.portal.adapters.telephony;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.models.servicios.telephony.ServicioTelFija;
import cu.arr.etecsa.api.portal.models.servicios.telephony.TFPerfil;
import java.io.IOException;

@Keep
public class ServiciosTelFijaAdapter extends JsonAdapter<ServicioTelFija> {

    private JsonReader.Options OPTIONS = JsonReader.Options.of("perfil");
    private Moshi moshi = new Moshi.Builder().build();

    @Override
    public ServicioTelFija fromJson(JsonReader reader) throws IOException {
        TFPerfil perfil = null;

        reader.beginObject();
        while (reader.hasNext()) {
            int index = reader.selectName(OPTIONS);
            switch (index) {
                case 0:
                    perfil = moshi.adapter(TFPerfil.class).fromJson(reader);
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        if (perfil != null) {
            return new ServicioTelFija(perfil);
        } else {
            throw new JsonDataException("Missing required fields");
        }
    }

    @Override
    public void toJson(JsonWriter arg0, ServicioTelFija arg1) {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
