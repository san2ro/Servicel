package cu.arr.etecsa.api.portal.utils;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SessionCookieInterceptor implements Interceptor {

    private String sessionCookie;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // Agregar la cookie de sesión si ya fue capturada
        if (sessionCookie != null) {
            request = request.newBuilder().addHeader("Cookie", sessionCookie).build();
        }
        Response response = chain.proceed(request);
        // Capturar la cookie de sesión de la respuesta si está presente
        if (response.headers("Set-Cookie").size() > 0) {
            for (String header : response.headers("Set-Cookie")) {
                if (header.startsWith("connect.sid")) {
                    sessionCookie = header;
                    break;
                }
            }
        }
        return response;
    }
}
