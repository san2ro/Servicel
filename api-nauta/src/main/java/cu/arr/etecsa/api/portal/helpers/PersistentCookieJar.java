package cu.arr.etecsa.api.portal.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PersistentCookieJar implements Interceptor {

    private String sessionCookie;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (sessionCookie != null) {
            request = request.newBuilder().addHeader("Cookie", sessionCookie).build();
        }
        Response response = chain.proceed(request);
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
