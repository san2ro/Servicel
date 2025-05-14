package cu.arr.etecsa.api.portal;

import android.os.Build;
import androidx.annotation.Keep;
import com.squareup.moshi.Moshi;
import cu.arr.etecsa.api.portal.adapters.UserAdapter;
import cu.arr.etecsa.api.portal.adapters.login.LoginAdapter;
import cu.arr.etecsa.api.portal.adapters.users.UsersAdapter;
import cu.arr.etecsa.api.portal.helpers.HeadersInterceptor;
import cu.arr.etecsa.api.portal.helpers.PersistentCookieJar;
import cu.arr.etecsa.api.portal.helpers.UserAgentInterceptor;
import cu.arr.etecsa.api.portal.models.User;
import cu.arr.etecsa.api.portal.models.login.LoginResponse;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;
import cu.arr.etecsa.api.portal.utils.MoshiProvider;
import cu.arr.etecsa.api.portal.utils.SessionCookieInterceptor;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Keep
public class PortalRetrofit {

    private static final String BASE_URL = "https://www.nauta.cu:5002/";
    private static final Moshi moshi = MoshiProvider.getInstance();
    private static final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

    static {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    // Generador de User-Agent dinámico (versión mejorada)
    private static String generateDynamicUserAgent() {
        try {
            return String.format(
                    "Mozilla/5.0 (Linux; U; Android %s; %s; %s Build/%s) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/110.0.0.0 Mobile Safari/537.36",
                    Build.VERSION.RELEASE,
                    Locale.getDefault().getLanguage(),
                    Build.MODEL,
                    Build.ID);
        } catch (Exception e) {
            return "Nauta/1.0.2"; // User-Agent por defecto si hay error
        }
    }

    // Configuración del OkHttpClient con orden CORRECTO de interceptores
    private static final OkHttpClient okHttpClient =
            new OkHttpClient.Builder()
                    .callTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(new UserAgentInterceptor(generateDynamicUserAgent()))
                    .addInterceptor(new HeadersInterceptor())
                    .addInterceptor(new SessionCookieInterceptor())
                    .addInterceptor(loggingInterceptor)
                    .retryOnConnectionFailure(true)
                    .build();

    // Resto del código permanece igual...
    private static final Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();

    public static PortalClient auth() {
        return retrofit.create(PortalClient.class);
    }
}
