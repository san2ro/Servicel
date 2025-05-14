package cu.arr.etecsa.api.portal.adapters.mobile;

import androidx.annotation.Keep;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.models.servicios.mobile.Bono;
import cu.arr.etecsa.api.portal.models.servicios.mobile.Listas;
import cu.arr.etecsa.api.portal.models.servicios.mobile.Plan;
import java.io.IOException;
import com.squareup.moshi.JsonReader;
import cu.arr.etecsa.api.portal.models.servicios.mobile.MobilePerfil;
import java.util.HashMap;
import java.util.Map;

@Keep
public class MobilePerfilAdapter extends JsonAdapter<MobilePerfil> {

    private Moshi moshi = new Moshi.Builder().build();

    @Override
    public MobilePerfil fromJson(JsonReader reader) throws IOException {
        String id = null;
        String numeroTelefono = null;
        String estado = null;
        String saldoPrincipal = null;
        String fechaVenta = null;
        String fechaBloqueo = null;
        String fechaEliminacion = null;
        String internet = null;
        String cuatroG = null;
        String adelantaSaldo = null;
        String tarifaPorConsumo = null;
        String moneda = null;
        Listas listas = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    id = reader.nextString();
                    break;
                case "Número de Teléfono":
                    numeroTelefono = reader.nextString();
                    break;
                case "Estado":
                    estado = reader.nextString();
                    break;
                case "Saldo Principal":
                    saldoPrincipal = reader.nextString();
                    break;
                case "Fecha de Venta":
                    fechaVenta = reader.nextString();
                    break;
                case "Fecha de Bloqueo":
                    fechaBloqueo = reader.nextString();
                    break;
                case "Fecha de Eliminación":
                    fechaEliminacion = reader.nextString();
                    break;
                case "Internet":
                    internet = reader.nextString();
                    break;
                case "4G":
                    cuatroG = reader.nextString();
                    break;
                case "Adelanta Saldo":
                    adelantaSaldo = reader.nextString();
                    break;
                case "Tarifa por Consumo":
                    tarifaPorConsumo = reader.nextString();
                    break;
                case "Moneda":
                    moneda = reader.nextString();
                    break;
                case "Listas":
                    listas = readListas(reader);
                    break;
            }
        }
        reader.endObject();

        return new MobilePerfil(
                id,
                numeroTelefono,
                estado,
                saldoPrincipal,
                fechaVenta,
                fechaBloqueo,
                fechaEliminacion,
                internet,
                cuatroG,
                adelantaSaldo,
                tarifaPorConsumo,
                moneda,
                listas);
    }

    private Listas readListas(JsonReader reader) throws IOException {
        Map<String, Plan> planes = new HashMap<>();
        Map<String, Bono> bonos = new HashMap<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "Planes":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String key = reader.nextName();
                        Plan plan = moshi.adapter(Plan.class).fromJson(reader);
                        planes.put(key, plan);
                    }
                    reader.endObject();
                    break;
                case "Bonos":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String key = reader.nextName();
                        Bono bono = moshi.adapter(Bono.class).fromJson(reader);
                        bonos.put(key, bono);
                    }
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return new Listas(planes, bonos);
        
    }

    @Override
    public void toJson(JsonWriter writer, MobilePerfil value) throws IOException {
        throw new UnsupportedOperationException("toJson not supported");
    }
}
