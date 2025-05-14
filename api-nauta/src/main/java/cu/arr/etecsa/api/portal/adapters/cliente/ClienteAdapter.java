package cu.arr.etecsa.api.portal.adapters.cliente;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.models.cliente.operaciones.Operaciones;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.cliente.Cliente;
import java.util.HashMap;
import java.util.Map;

@Keep
public class ClienteAdapter extends JsonAdapter<Cliente> {

    private final JsonReader.Options options =
            JsonReader.Options.of(
                    "nombre",
                    "telefono",
                    "email",
                    "notificaciones_mail",
                    "notificaciones_movil",
                    "usuario_portal",
                    "operaciones");

    private final Moshi moshi = new Moshi.Builder().build();

    @Override
    public Cliente fromJson(JsonReader reader) throws IOException {
        String name = null;
        String phone = null;
        String email = null;
        String notMail = null;
        String notPhone = null;
        String user = null;
        Map<String, Operaciones> operaciones = null;

        reader.beginObject();
        while (reader.hasNext()) {
            int index = reader.selectName(options);
            switch (index) {
                case 0 -> name = reader.nextString();
                case 1 -> phone = reader.nextString();
                case 2 -> email = reader.nextString();
                case 3 -> notMail = reader.nextString();
                case 4 -> notPhone = reader.nextString();
                case 5 -> user = reader.nextString();
                case 6 -> {
                    operaciones = new HashMap<>();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String key = reader.nextName();
                        Operaciones operacion = moshi.adapter(Operaciones.class).fromJson(reader);
                        operaciones.put(key, operacion);
                    }
                    reader.endObject();
                }

                default -> reader.skipValue();
            }
        }
        reader.endObject();
        return new Cliente(name, phone, email, notMail, notPhone, user, operaciones);
    }

    @Override
    public void toJson(JsonWriter arg0, Cliente arg1) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
