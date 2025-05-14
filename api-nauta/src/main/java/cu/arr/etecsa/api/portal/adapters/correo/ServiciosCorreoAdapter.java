package cu.arr.etecsa.api.portal.adapters.correo;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.models.servicios.correo.CorreoPerfil;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.servicios.correo.ServiciosCorreo;

@Keep
public class ServiciosCorreoAdapter extends JsonAdapter<ServiciosCorreo> {

    private JsonReader.Options OPTIONS = JsonReader.Options.of("perfil", "tipoProducto");
    private Moshi moshi = new Moshi.Builder().build();

    @Override
    public ServiciosCorreo fromJson(JsonReader reader) throws IOException {
        CorreoPerfil perfil = null;
        String tipoProducto = null;

        reader.beginObject();
        while (reader.hasNext()) {
            int index = reader.selectName(OPTIONS);
            switch (index) {
                case 0:
                    perfil = moshi.adapter(CorreoPerfil.class).fromJson(reader);
                    break;
                case 1:
                    tipoProducto = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        if (perfil != null && tipoProducto != null) {
            return new ServiciosCorreo(perfil, tipoProducto);
        } else {
            throw new JsonDataException("Missing required fields");
        }
    }

    @Override
    public void toJson(JsonWriter writer, ServiciosCorreo value) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
