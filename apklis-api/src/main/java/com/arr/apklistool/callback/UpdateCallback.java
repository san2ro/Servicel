package com.arr.apklistool.callback;
import com.arr.apklistool.models.LastRelease;

public interface UpdateCallback {
    
    void lastRelease(LastRelease response);
    
    void onError(Throwable e);
}
