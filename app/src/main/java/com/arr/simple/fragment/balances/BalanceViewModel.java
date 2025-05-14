package com.arr.simple.fragment.balances;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.arr.simple.helpers.portal.RetrofitUrlChecker;

public class BalanceViewModel extends ViewModel {
    private final RetrofitUrlChecker urlChecker = new RetrofitUrlChecker();
    private final MutableLiveData<Boolean> isUrlAvailable = new MutableLiveData<>();
    private Handler handler;
    private Runnable checkRunnable;

    public LiveData<Boolean> getUrlStatus() {
        return isUrlAvailable;
    }

    public void startChecking(String url) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }

        checkRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        urlChecker.checkUrl(
                                url,
                                isAvailable -> {
                                    isUrlAvailable.postValue(isAvailable);
                                    // Si no está disponible, programar próxima verificación
                                    if (handler != null && !isAvailable) {
                                        handler.postDelayed(this, 6000);
                                    }
                                });
                    }
                };

        handler.post(checkRunnable);
    }

    public void stopChecking() {
        if (handler != null && checkRunnable != null) {
            handler.removeCallbacks(checkRunnable);
        }
    }

    @Override
    protected void onCleared() {
        stopChecking();
        super.onCleared();
    }
}
