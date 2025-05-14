package cu.arr.etecsa.api.portal.request.navigation.password;

import androidx.annotation.Keep;

@Keep
public class ProcessRespRequest {

    
    private String operacionId;

    public ProcessRespRequest(String operationId) {
        this.operacionId = operationId;
    }
}
