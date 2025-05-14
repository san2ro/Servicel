package cu.arr.etecsa.api.portal.exceptions;

import androidx.annotation.Keep;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLHandshakeException;
import retrofit2.HttpException;

@Keep
public class PortalException implements ErrorHandler {

    @Override
    public String handlerException(Throwable throwable) {
        if (throwable instanceof IOException) {
            return "Problemas de conexión o credenciales inválidas.";
        } else if (throwable instanceof HttpException) {
            int statusCode = ((HttpException) throwable).code();
            if (statusCode >= 500) {
                return "Problemas con el servidor, intente más tarde.";
            } else if (statusCode >= 404) {
                return "Acceso denegado, revise sus datos.";
            } else {
                return "Error de conexión " + statusCode + ". Por favor, intenta de nuevo.";
            }
        } else if (throwable instanceof JsonParseException) {
            return "Error al procesar los datos recibidos. Intenta nuevamente.";
        } else if (throwable instanceof SSLHandshakeException) {
            return "Problemas de seguridad SSL. Verifique la conexión con el servidor.";
        } else if (throwable instanceof TimeoutException) {
            return "El servidor ha tardado en devolver una respuesta. Reintente más tarde";
        }
        return "Error desconocido, intente nuevamente.";
    }
}
