package com.arr.apklistool;

import androidx.annotation.Keep;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Keep
public class ApklisRetrofit {

    private static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    static {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    // Configurar el cliente OkHttp con timeouts y logging
    private static final OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();
    }

    private static Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl("https://api.apklis.cu/v3/")
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

    public static ApklisClient auth() {
        return retrofit.create(ApklisClient.class);
    }
}
