package cu.arr.etecsa.api.portal.adapters.navigation;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.models.servicios.navigation.NavigationPerfil;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.servicios.navigation.ServiciosNavegacion;

@Keep
public class ServiciosNavigationAdapter extends JsonAdapter<ServiciosNavegacion> {

    private JsonReader.Options options = JsonReader.Options.of("perfil", "tipoProducto");
    private Moshi moshi = new Moshi.Builder().build();

    @Override
    public ServiciosNavegacion fromJson(JsonReader reader) throws IOException {
        NavigationPerfil perfil = null;
        String typeProduct = null;

        reader.beginObject();
        while (reader.hasNext()) {
            int index = reader.selectName(options);
            switch (index) {
                case 0:
                    perfil = moshi.adapter(NavigationPerfil.class).fromJson(reader);
                    break;
                case 1:
                    typeProduct = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        if (perfil != null && typeProduct != null) {
            return new ServiciosNavegacion(perfil, typeProduct);
        } else {
            throw new JsonDataException("Missing required fields");
        }
    }

    @Override
    public void toJson(JsonWriter writer, ServiciosNavegacion value) throws IOException {
        throw new UnsupportedOperationException("toJson unsupported");
    }
}
