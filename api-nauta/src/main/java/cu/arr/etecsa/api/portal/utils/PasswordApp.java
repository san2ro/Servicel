package cu.arr.etecsa.api.portal.utils;

import android.util.Base64;
import androidx.annotation.Keep;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Copyright (c) 2024 SuitETECSA
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
@Keep
public class PasswordApp {

    /**
     * Este método genera una clave de acceso (API Key) basada en la fecha y hora actual,
     * concatenada con una cadena específica. La clave se encripta usando SHA-512 y luego se
     * codifica en Base64.
     *
     * @return String que contiene la clave API en formato Base64 con el prefijo "ApiKey "
     */
    public static String getPasswordApp() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMyyyyHH", Locale.getDefault());
        String dateString = dateFormatter.format(new Date());
        String appKey = "portal" + dateString + "externalPortal";
        byte[] appKeyData = appKey.getBytes(StandardCharsets.UTF_8);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedData = md.digest(appKeyData);
            return "ApiKey " + Base64.encodeToString(hashedData, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 algorithm not found", e);
        }
    }
}
