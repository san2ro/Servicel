package cu.arr.etecsa.api.portal.models.servicios.navigation;
import androidx.annotation.Keep;

@Keep
public class NavigationPerfil {
    
    public String cuentaAcceso;
    public String fechaVenta;
    public String estado;
    public String fechaBloqueo;
    public String fechaEliminacion;
    public String tipoAcceso;
    public String horasBonificacion;
    public String bonificacionDisfrutar;
    public String moneda;
    public String id;
    public String saldo;

    public NavigationPerfil(String cuentaAcceso, String fechaVenta, String estado, String fechaBloqueo, String fechaEliminacion, String tipoAcceso, String horasBonificacion, String bonificacionDisfrutar, String moneda, String id, String saldo) {
        this.cuentaAcceso = cuentaAcceso;
        this.fechaVenta = fechaVenta;
        this.estado = estado;
        this.fechaBloqueo = fechaBloqueo;
        this.fechaEliminacion = fechaEliminacion;
        this.tipoAcceso = tipoAcceso;
        this.horasBonificacion = horasBonificacion;
        this.bonificacionDisfrutar = bonificacionDisfrutar;
        this.moneda = moneda;
        this.id = id;
        this.saldo = saldo;
    }
}
