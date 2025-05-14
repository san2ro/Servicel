package cu.arr.etecsa.api.portal.models.servicios.mobile;

import androidx.annotation.Keep;

@Keep
public class MobilePerfil {

    public String id;
    public String numeroTelefono;
    public String estado;
    public String saldoPrincipal;
    public String fechaVenta;
    public String fechaBloqueo;
    public String fechaEliminacion;
    public String internet;
    public String cuatroG;
    public String adelantaSaldo;
    public String tarifaPorConsumo;
    public String moneda;
    public Listas listas;

    public MobilePerfil(
            String id,
            String numero,
            String estado,
            String saldo,
            String fecha_venta,
            String fecha_bloqueo,
            String fecha_eliminacion,
            String internet,
            String red_4g,
            String adelanta_saldo,
            String tarifa_por_consumo,
            String moneda,
            Listas listas) {
        this.id = id;
        this.numeroTelefono = numero;
        this.estado = estado;
        this.saldoPrincipal = saldo;
        this.fechaVenta = fecha_venta;
        this.fechaBloqueo = fecha_bloqueo;
        this.fechaEliminacion = fecha_eliminacion;
        this.internet = internet;
        this.cuatroG = red_4g;
        this.adelantaSaldo = adelanta_saldo;
        this.tarifaPorConsumo = tarifa_por_consumo;
        this.moneda = moneda;
        this.listas = listas;
    }
}
