package cu.arr.etecsa.api.portal.request.telephony;

import androidx.annotation.Keep;

@Keep
public class TelephonyRequest {

    private String url;

    public TelephonyRequest(String numero, String mes) {
        this.url = "queryFacturaFija?telefono=" + numero + "&yearMes=" + mes;
    }
}
