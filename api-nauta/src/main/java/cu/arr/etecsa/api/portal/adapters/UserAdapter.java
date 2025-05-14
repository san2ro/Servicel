package cu.arr.etecsa.api.portal.adapters;

import android.util.Log;
import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.adapters.cliente.ClienteAdapter;
import cu.arr.etecsa.api.portal.adapters.servicios.ServicesAdapter;
import cu.arr.etecsa.api.portal.models.cliente.Cliente;
import cu.arr.etecsa.api.portal.models.servicios.Services;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.User;

@Keep
public class UserAdapter extends JsonAdapter<Object> {

    @Override
    public Object fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case STRING:
                Log.e("User", "es un string");
                return reader.nextString();
            case BEGIN_OBJECT:
                Log.e("USER", "es un object");
                Moshi moshi =
                        new Moshi.Builder()
                                .add(Cliente.class, new ClienteAdapter())
                                .add(Services.class, new ServicesAdapter())
                                .build();

                JsonAdapter<User> userJsonAdapter = moshi.adapter(User.class);
                try {
                    return userJsonAdapter.fromJson(reader);
                } catch (JsonDataException e) {
                    throw new IOException(e.getMessage());
                }
            default:
                throw new JsonDataException("Exception: " + reader.peek());
        }
    }

    @Override
    public void toJson(JsonWriter writer, Object value) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
