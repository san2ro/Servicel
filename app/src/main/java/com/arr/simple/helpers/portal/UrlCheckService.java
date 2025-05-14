package com.arr.simple.helpers.portal;

import retrofit2.Call;
import retrofit2.http.HEAD;
import retrofit2.http.Url;

public interface UrlCheckService {

    @HEAD
    Call<Void> checkUrl(@Url String url);
}
