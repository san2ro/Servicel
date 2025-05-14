package cu.arr.etecsa.api.portal.models.users;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;

@Keep
public class UsersDataJson {

    @Json(name = "resp")
    private UsersResponse response;

    public UsersResponse getResp() {
        return response;
    }
}
