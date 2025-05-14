package cu.arr.etecsa.api.portal.request.profile;

import androidx.annotation.Keep;
import com.google.gson.Gson;

@Keep
public class UpdateProfileRequest {

    private String type;
    private String params;

    public UpdateProfileRequest(String usuario, String password, String phone, String email, String notiMovil, String notiMail) {
        this.type = "USUARIO_PORTAL";
        // Usa Gson para convertir los parámetros en un JSON String
        Params paramsObject = new Params(usuario, password, phone, email, notiMovil, notiMail);
        this.params = new Gson().toJson(paramsObject);
    }

    public static class Params {
        private String usuario;
        private String password;
        private String telefono;
        private String email;
        private String notificarMovil;
        private String notificarMail;

        public Params(String usuario, String password, String telefono, String email, String notificarMovil, String notificarMail) {
            this.usuario = usuario;
            this.password = password;
            this.telefono = telefono;
            this.email = email;
            this.notificarMovil = notificarMovil;
            this.notificarMail = notificarMail;
        }
    }
}