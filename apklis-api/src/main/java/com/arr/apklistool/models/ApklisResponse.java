package com.arr.apklistool.models;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;

@Keep
public class ApklisResponse {

    @SerializedName("size")
    public String size;

    @SerializedName("last_release")
    public LastRelease lastRelease;

    public LastRelease getLastRelease() {
        return lastRelease;
    }
}
