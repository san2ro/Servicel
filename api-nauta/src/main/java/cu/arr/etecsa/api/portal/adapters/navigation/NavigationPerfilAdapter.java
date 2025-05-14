package cu.arr.etecsa.api.portal.adapters.navigation;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.servicios.navigation.NavigationPerfil;

@Keep
public class NavigationPerfilAdapter extends JsonAdapter<NavigationPerfil> {

    @Override
    public NavigationPerfil fromJson(JsonReader reader) throws IOException {
        String cuentaAcceso = null;
        String fechaVenta = null;
        String estado = null;
        String fechaBloqueo = null;
        String fechaEliminacion = null;
        String tipoAcceso = null;
        String horasBonificacion = null;
        String bonificacionDisfrutar = null;
        String moneda = null;
        String id = null;
        String saldo = null;
        
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "Cuenta de acceso":
                    cuentaAcceso = reader.nextString();
                    break;
                case "Fecha de venta":
                    fechaVenta = reader.nextString();
                    break;
                case "Estado":
                    estado = reader.nextString();
                    break;
                case "Fecha de bloqueo":
                    fechaBloqueo = reader.nextString();
                    break;
                case "Fecha de eliminación":
                    fechaEliminacion = reader.nextString();
                    break;
                case "Tipo de acceso":
                    tipoAcceso = reader.nextString();
                    break;
                case "Horas de bonificación":
                    horasBonificacion = reader.nextString();
                    break;
                case "Bonificación por disfrutar":
                    bonificacionDisfrutar = reader.nextString();
                    break;
                case "Moneda":
                    moneda = reader.nextString();
                    break;
                case "id":
                    id = reader.nextString();
                    break;
                case "saldo":
                    saldo = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        
        return new NavigationPerfil(cuentaAcceso, fechaVenta, estado,fechaBloqueo,fechaEliminacion,tipoAcceso,horasBonificacion,bonificacionDisfrutar,moneda,id,saldo );
    }

    @Override
    public void toJson(JsonWriter write, NavigationPerfil value) throws IOException {
        throw new UnsupportedOperationException("toJson unsupported");
    }
}
