package cu.arr.etecsa.api.portal.models.cliente;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;
import cu.arr.etecsa.api.portal.models.cliente.operaciones.Operaciones;
import java.util.Map;

@Keep
public class Cliente {

    @Json(name = "nombre")
    private String nombre;

    @Json(name = "telefono")
    public String phone;

    @Json(name = "email")
    public String email;

    @Json(name = "notificaciones_mail")
    public String notification_mail;

    @Json(name = "notificaciones_movil")
    public String notification_mobile;

    @Json(name = "usuario_portal")
    public String usuario;

    @Json(name = "operaciones")
    public Map<String, Operaciones> operaciones;

    public Cliente(
            String name,
            String phone,
            String email,
            String notifMail,
            String notifMobil,
            String user,
            Map<String, Operaciones> operaciones) {
        this.nombre = name;
        this.phone = phone;
        this.email = email;
        this.notification_mail = notifMail;
        this.notification_mobile = notifMobil;
        this.usuario = user;
        this.operaciones = operaciones;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getNotificacionMail() {
        return notification_mail;
    }

    public String getNotificacionMobile() {
        return notification_mobile;
    }

    public String getUsuarioPortal() {
        return usuario;
    }

    public Map<String, Operaciones> getOperaciones() {
        return operaciones;
    }
}
