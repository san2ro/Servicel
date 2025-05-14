package cu.arr.etecsa.api.portal.request;

import androidx.annotation.Keep;

@Keep
public class CheckPayRequest {

    private String id;

    public CheckPayRequest(String id) {
        this.id = id;
    }
}
