package cu.arr.etecsa.api.portal.adapters.correo;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.servicios.correo.CorreoPerfil;

@Keep
public class CorreoPerfilAdapter extends JsonAdapter<CorreoPerfil> {

    @Override
    public CorreoPerfil fromJson(JsonReader reader) throws IOException {
        String fechaVenta = null;
        String cuenta = null;
        String moneda = null;
        String id = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "Fecha de venta":
                    fechaVenta = reader.nextString();
                    break;
                case "Cuenta de correo":
                    cuenta = reader.nextString();
                    break;
                case "Moneda":
                    moneda = reader.nextString();
                    break;
                case "id":
                    id = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new CorreoPerfil(fechaVenta, cuenta, moneda, id);
    }

    @Override
    public void toJson(JsonWriter writer, CorreoPerfil value) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
