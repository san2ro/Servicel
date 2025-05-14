package cu.arr.etecsa.api.portal.adapters.mobile;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.models.servicios.mobile.MobilePerfil;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.servicios.mobile.ServiciosMoviles;

@Keep
public class ServiciosMovilesAdapter extends JsonAdapter<ServiciosMoviles> {

    private JsonReader.Options OPTIONS = JsonReader.Options.of("perfil");
    private Moshi moshi = new Moshi.Builder().build();

    @Override
    public ServiciosMoviles fromJson(JsonReader reader) throws IOException {
        MobilePerfil perfil = null;

        reader.beginObject();
        while (reader.hasNext()) {
            int index = reader.selectName(OPTIONS);
            switch (index) {
                case 0:
                    perfil = moshi.adapter(MobilePerfil.class).fromJson(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        if (perfil != null) {
            return new ServiciosMoviles(perfil);
        } else {
            throw new JsonDataException("Missing required 'perfil' field");
        }
    }

    @Override
    public void toJson(JsonWriter arg0, ServiciosMoviles arg1) throws IOException {
        throw new UnsupportedOperationException("toJson unsupported");
    }
}
