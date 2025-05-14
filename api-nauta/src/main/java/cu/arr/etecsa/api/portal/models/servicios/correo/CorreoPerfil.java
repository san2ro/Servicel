package cu.arr.etecsa.api.portal.models.servicios.correo;

import androidx.annotation.Keep;

@Keep
public class CorreoPerfil {

    public String fechaVenta;
    public String cuenta;
    public String moneda;
    public String id;
    
    public CorreoPerfil(String venta, String cuenta, String moneda, String id){
        this.fechaVenta = venta;
        this.cuenta = cuenta;
        this.moneda = moneda;
        this.id = id;
    }
}
