package com.arr.simple.fragment.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerViewModel extends ViewModel {

    private final MutableLiveData<String> shareData = new MutableLiveData<>();

    public void setText(String data) {
        shareData.setValue(data);
    }

    public void setTextPhone(String data) {
        shareData.setValue(data);
    }

    public LiveData<String> getText() {
        return shareData;
    }
}
