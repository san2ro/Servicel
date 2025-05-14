package com.arr.simple.helpers.profile.models;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;

@Keep
public class Updated {

    @SerializedName("updated_plans")
    public boolean updated;

    @SerializedName("last_updated")
    public String last_updated;
}
