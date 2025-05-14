package cu.arr.etecsa.api.portal.utils;
import com.auth0.android.jwt.JWT;
import java.util.Calendar;
import java.util.Date;

public class ValidateToken {
    
    /**
     * Verifica si el token JWT es válido en cuanto a la fecha de expiración.
     *
     * @param token El token JWT recibido desde el servidor.
     * @return true si el token no ha expirado, false en caso contrario.
     */
    public static boolean isValidToken(String token) {
        if (token == null || token.isEmpty()) {
            return false; // Token inválido
        }
        try {
            JWT jwt = new JWT(token);
            Date expireAt = jwt.getExpiresAt();
            Calendar calendar = Calendar.getInstance();
            assert expireAt != null;
            calendar.setTime(expireAt);
            return calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis();
        } catch (Exception e) {
            return false;
        }
    }
}
