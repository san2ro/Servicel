package cu.arr.etecsa.api.portal.models.users;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;
import cu.arr.etecsa.api.portal.models.User;

@Keep
public class UsersResponse {

    @Json(name = "user")
    private Object user;

    @Json(name = "resultado")
    private String resultado;

    public UsersResponse(Object user, String result) {
        this.user = user;
        this.resultado = result;
    }

    public Object getUser() {
        return user;
    }

    public String getResultado() {
        return resultado;
    }
}
