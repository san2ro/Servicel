package com.arr.apklistool;

import androidx.annotation.Keep;
import com.arr.apklistool.models.ApklisResponse;
import com.arr.apklistool.models.UrlRequest;
import com.arr.apklistool.models.UrlResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Keep
public interface ApklisClient {

    @GET("application/{packageName}")
    Single<ApklisResponse> getAppInfo(@Path("packageName") String packageName);

    @POST("release/get_url/")
    Single<UrlResponse> getUrl(@Body UrlRequest body);
}
