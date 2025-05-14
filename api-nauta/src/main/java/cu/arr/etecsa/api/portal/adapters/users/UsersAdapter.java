package cu.arr.etecsa.api.portal.adapters.users;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.adapters.UserAdapter;
import cu.arr.etecsa.api.portal.models.User;
import cu.arr.etecsa.api.portal.models.users.UsersDataJson;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;

@Keep
public class UsersAdapter extends JsonAdapter<UsersResponse> {

    @Override
    public UsersResponse fromJson(JsonReader reader) throws IOException {
        Moshi moshi = new Moshi.Builder().add(Object.class, new UserAdapter()).build();
        JsonAdapter<UsersDataJson> jsonAdapter = moshi.adapter(UsersDataJson.class);
        
        UsersDataJson userJson = jsonAdapter.fromJson(reader);
        
        if (userJson != null) {
            return new UsersResponse(
                    userJson.getResp().getUser(), userJson.getResp().getResultado());
        } else {
            throw new IOException("the JSON body cannot be null");
        }
    }

    @Override
    public void toJson(JsonWriter arg0, UsersResponse arg1) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
