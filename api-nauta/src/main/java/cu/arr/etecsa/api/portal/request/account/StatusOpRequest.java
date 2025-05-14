package cu.arr.etecsa.api.portal.request.account;

import androidx.annotation.Keep;

@Keep
public class StatusOpRequest {

    private String operacionId;

    public StatusOpRequest(String operacionId) {
        this.operacionId = operacionId;
    }
}
