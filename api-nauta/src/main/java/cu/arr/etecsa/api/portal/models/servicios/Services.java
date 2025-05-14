package cu.arr.etecsa.api.portal.models.servicios;

import androidx.annotation.Keep;
import cu.arr.etecsa.api.portal.models.servicios.correo.CorreoPerfil;
import cu.arr.etecsa.api.portal.models.servicios.mobile.MobilePerfil;
import cu.arr.etecsa.api.portal.models.servicios.navigation.NavigationPerfil;
import cu.arr.etecsa.api.portal.models.servicios.telephony.TFPerfil;
import java.util.Map;

@Keep
public class Services {

    public Map<String, MobilePerfil> perfilMovil;
    public Map<String, NavigationPerfil> perfilNavigation;
    public Map<String, CorreoPerfil> perfilCorreo;
    public Map<String, TFPerfil> perfilTelFija;

    public Services( Map<String, MobilePerfil> perfilMovil, Map<String, NavigationPerfil> perfilNavigation, Map<String, CorreoPerfil> perfilCorreo, Map<String, TFPerfil> perfilTelFija) {
        this.perfilMovil = perfilMovil;
        this.perfilNavigation = perfilNavigation;
        this.perfilCorreo = perfilCorreo;
        this.perfilTelFija = perfilTelFija;
    }

    public Map<String, MobilePerfil> getPerfilMobile() {
        return perfilMovil;
    }

    public Map<String, NavigationPerfil> getPerfilNavegation() {
        return perfilNavigation;
    }

    public Map<String, CorreoPerfil> getPerfilCorreo() {
        return perfilCorreo;
    }

    public Map<String, TFPerfil> getTelefoniaFija() {
        return perfilTelFija;
    }
}
