package cu.arr.etecsa.api.portal.models.operations;

import androidx.annotation.Keep;
import java.util.Map;

@Keep
public class Detalle {
    
    private Object value; // Puede ser String o DetalleOperacion

    // Constructor para String
    public Detalle(String value) {
        this.value = value;
    }

    // Constructor para DetalleOperacion
    public Detalle(OperationId value) {
        this.value = value;
    }

    // Métodos para obtener el valor
    public boolean isString() {
        return value instanceof String;
    }

    public boolean isOperacion() {
        return value instanceof OperationId;
    }

    public String getAsString() {
        return isString() ? (String) value : null;
    }

    public OperationId getAsOperacion() {
        return isOperacion() ? (OperationId) value : null;
    }
}
