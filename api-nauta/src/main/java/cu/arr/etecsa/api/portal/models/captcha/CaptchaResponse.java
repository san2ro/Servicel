package cu.arr.etecsa.api.portal.models.captcha;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.annotation.Keep;
import com.caverock.androidsvg.SVG;
import com.squareup.moshi.Json;
import java.io.IOException;

@Keep
public class CaptchaResponse {

    @Json(name = "text")
    private String text;

    @Json(name = "data")
    private String data;

    /**
     * Devuelve el ID de la solicitud o token de acceso al servidor. Este método se utiliza para
     * recuperar el valor del campo 'text', que puede ser necesario para futuras solicitudes o
     * validaciones en el servidor.
     *
     * @return El ID de la solicitud o token de acceso.
     */
    public String getRequestId() {
        return text;
    }

    /**
     * Convierte el código SVG recibido en 'data' en un Bitmap. El SVG proporcionado por el servidor
     * se convierte en una imagen que puede mostrarse en la interfaz de usuario. Si el ancho o alto
     * del documento SVG no está definido, se utilizan dimensiones predeterminadas (250x60 píxeles).
     * La imagen se escala para duplicar su altura manteniendo las proporciones.
     *
     * @return Un Bitmap generado a partir del SVG.
     * @throws IOException Si ocurre algún error al procesar el SVG.
     */
    public Bitmap getCaptchaBitmap() throws IOException {
        try {
            SVG svg = SVG.getFromString(data);
            float originalWidth = svg.getDocumentWidth();
            float originalHeight = svg.getDocumentHeight();

            if (originalWidth <= 0 || originalHeight <= 0) {
                originalWidth = 250;
                originalHeight = 60;
            }

            float desiredHeight = originalHeight * 2;
            float scaleFactor = desiredHeight / originalHeight;
            float desiredWidth = originalWidth * scaleFactor;

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            (int) desiredWidth, (int) desiredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(scaleFactor, scaleFactor);
            svg.renderToCanvas(canvas);

            return bitmap;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
