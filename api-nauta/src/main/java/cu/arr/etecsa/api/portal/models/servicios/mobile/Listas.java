package cu.arr.etecsa.api.portal.models.servicios.mobile;

import androidx.annotation.Keep;
import java.util.Map;

@Keep
public class Listas {

    public Map<String, Plan> planes;
    public Map<String, Bono> bonos;

    public Listas(Map<String, Plan> planes, Map<String, Bono> bonos) {
        this.planes = planes;
        this.bonos = bonos;
    }
}
