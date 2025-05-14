package com.arr.apklistool.models;

import androidx.annotation.Keep;

@Keep
public class UrlRequest {

    private String sha256;

    public UrlRequest(String sha256) {
        this.sha256 = sha256;
    }
}
