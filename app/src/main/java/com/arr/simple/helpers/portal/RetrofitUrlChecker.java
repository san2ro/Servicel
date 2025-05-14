package com.arr.simple.helpers.portal;

import android.content.Context;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import java.util.concurrent.TimeUnit;

public class RetrofitUrlChecker {

    private final UrlCheckService service;

    public RetrofitUrlChecker() {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://google.com") // URL base dummy
                        .client(client)
                        .build();

        service = retrofit.create(UrlCheckService.class);
    }

    public void checkUrl(String url, final UrlCheckListener listener) {
        service.checkUrl(url)
                .enqueue(
                        new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                listener.onUrlCheckResult(response.isSuccessful());
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                listener.onUrlCheckResult(false);
                            }
                        });
    }

    public interface UrlCheckListener {
        void onUrlCheckResult(boolean isAvailable);
    }
}
