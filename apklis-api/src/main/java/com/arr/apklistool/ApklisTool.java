package com.arr.apklistool;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.annotation.Keep;
import com.arr.apklistool.callback.UpdateCallback;
import com.arr.apklistool.models.LastRelease;
import com.google.gson.Gson;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Keep
public class ApklisTool {

    private final ApklisClient client;

    ApklisTool(ApklisClient client) {
        this.client = client;
    }

    public Disposable hasUpdate(Context context, UpdateCallback callback) {
        return client.getAppInfo("com.arr.simple")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            LastRelease release = response.lastRelease;
                            if (release.versionCode > getCurrentVersionCode(context)) {
                                callback.lastRelease(response.lastRelease);
                            }
                        },
                        throwable -> {
                            callback.onError(throwable);
                        });
    }

    @SuppressWarnings("deprecation")
    private int getCurrentVersionCode(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ApklisTool", "getCurrentVersionCode: " + e.getMessage(), e);
        }
        return 0;
    }

    public static class Builder {

        private ApklisClient client;
        private static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        static {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        // Configurar el cliente OkHttp con timeouts y logging
        private static final OkHttpClient okHttpClient;

        static {
            okHttpClient = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();
        }

        public Builder setClient(ApklisClient client) {
            this.client = client;
            return this;
        }

        public ApklisTool build() {
            if (client == null) {
                client =
                        new Retrofit.Builder()
                                .baseUrl("https://api.apklis.cu/v3/")
                                .client(okHttpClient)
                                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(ApklisClient.class);
            }
            return new ApklisTool(client);
        }
    }
}
