package com.arr.simple;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.google.android.material.color.DynamicColors;

public class AppServicel extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }

        Thread.setDefaultUncaughtExceptionHandler(
                (thread, throwable) -> {
                    Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
                    intent.putExtra("DEBUG_INFO", Log.getStackTraceString(throwable));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    // Kill the app after displaying the error
                    System.exit(1);
                });
    }
}
