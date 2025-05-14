package cu.arr.etecsa.api.portal.helpers;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeadersInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request =
                original.newBuilder()
                        .header("Accept", "application/json, text/plain, */*")
                        .header("Accept-Language", "es-ES,es;q=0.9")
                        .header("Connection", "keep-alive")
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .header("Origin", "https://www.nauta.cu")
                        .header("Referer", "https://www.nauta.cu/")
                        .header("Sec-Fetch-Dest", "empty")
                        .header("Sec-Fetch-Mode", "cors")
                        .header("Sec-Fetch-Site", "same-site")
                        .method(original.method(), original.body())
                        .build();

        return chain.proceed(request);
    }
}
