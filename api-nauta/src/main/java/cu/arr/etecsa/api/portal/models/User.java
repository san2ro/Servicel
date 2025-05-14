package cu.arr.etecsa.api.portal.models;

import androidx.annotation.Keep;
import com.squareup.moshi.Json;
import cu.arr.etecsa.api.portal.models.cliente.Cliente;
import cu.arr.etecsa.api.portal.models.servicios.Services;

@Keep
public class User {

    @Json(name = "cliente")
    private Cliente cliente;

    @Json(name = "Servicios")
    private Services services;

    @Json(name = "servicios_actualizados")
    private String updateServices;

    @Json(name = "completado")
    private String completed;

    @Json(name = "fechaActualizacion")
    private String dateUpdate;

    public User(
            Cliente cliente,
            Services serv,
            String updateServices,
            String completed,
            String dateUpdate) {
        this.cliente = cliente;
        this.services = serv;
        this.updateServices = updateServices;
        this.completed = completed;
        this.dateUpdate = dateUpdate;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Services getServicios() {
        return services;
    }

    public String getUpdateServices() {
        return updateServices;
    }

    public String getCompleted() {
        return completed;
    }

    public String getUpdateDate() {
        return dateUpdate;
    }
}
