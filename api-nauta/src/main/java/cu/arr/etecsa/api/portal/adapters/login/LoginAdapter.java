package cu.arr.etecsa.api.portal.adapters.login;

import android.util.Log;
import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.adapters.UserAdapter;
import cu.arr.etecsa.api.portal.models.User;
import cu.arr.etecsa.api.portal.models.login.LoginDataJson;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.login.LoginResponse;

@Keep
public class LoginAdapter extends JsonAdapter<LoginResponse> {

    @Override
    public LoginResponse fromJson(JsonReader reader) throws IOException {
        Moshi moshi = new Moshi.Builder().add(Object.class, new UserAdapter()).build();
        JsonAdapter<LoginDataJson> jsonAdapter = moshi.adapter(LoginDataJson.class);

        LoginDataJson loginJson = jsonAdapter.fromJson(reader);

        if (loginJson != null) {
            return new LoginResponse(
                    loginJson.getResp().getToken(),
                    loginJson.getResp().getUser(),
                    loginJson.getResp().getResultado());
        } else {
            throw new IOException("el cuerpo del JSON no puede ser nulo");
        }
    }

    @Override
    public void toJson(JsonWriter arg0, LoginResponse arg1) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
