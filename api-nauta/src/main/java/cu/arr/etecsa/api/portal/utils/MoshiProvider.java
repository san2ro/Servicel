package cu.arr.etecsa.api.portal.utils;

import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.adapters.UserAdapter;
import cu.arr.etecsa.api.portal.adapters.login.LoginAdapter;

import cu.arr.etecsa.api.portal.adapters.operations.OperationsAdapter;
import cu.arr.etecsa.api.portal.adapters.users.UsersAdapter;
import cu.arr.etecsa.api.portal.models.User;
import cu.arr.etecsa.api.portal.models.login.LoginResponse;
import cu.arr.etecsa.api.portal.models.operations.OperationsResponse;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;

public class MoshiProvider {

    private static final Moshi MOSHI =
            new Moshi.Builder()
                    .add(LoginResponse.class, new LoginAdapter())
                    .add(UsersResponse.class, new UsersAdapter())
                    .add(OperationsResponse.class, new OperationsAdapter())
                    .build();

    public static Moshi getInstance() {
        return MOSHI;
    }
}
