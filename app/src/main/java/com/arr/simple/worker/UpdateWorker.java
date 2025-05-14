package com.arr.simple.worker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ListenableWorker.Result;
import androidx.work.Worker;
import com.arr.simple.R;
import androidx.work.WorkerParameters;
import com.arr.simple.helpers.data.LoginData;
import com.arr.simple.helpers.data.ProcessData;
import com.arr.simple.helpers.notification.BalanceNotification;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.arr.simple.helpers.profile.ProfileManager;
import com.arr.simple.helpers.update.UpdateData;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateWorker extends Worker {

    private Context mContext;

    private String CHANNEL_NAME = "Actualización de datos";
    private String CHANNEL_ID = "balances";

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters parameters) {
        super(context, parameters);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!hasAccessToUrl("https://www.nauta.cu")) {
            Result.retry();
        }
        try {
            performMainTask();
            Log.e("Worker", "worker success");
            return Result.success();
        } catch (Exception e) {
            Log.e("Worker", e.getMessage());
            return Result.failure();
        }
    }

    private void performMainTask() {
        UpdateData update = new UpdateData(mContext);
        update.updateDataPortal(
                new UpdateData.UpdateDataCallback() {
                    @Override
                    public void onProgress(UpdateData.SyncStatus status, String message) {
                        // Actualizar UI con el progreso
                        Log.d("SyncProgress", status + ": " + message);
                    }

                    @Override
                    public void onSuccess(UpdateData.SyncResult result) {
                        ProcessProfile.clearCache();
                        new ProfileManager(mContext).processUserResponse(result.response);
                        // mostrar notificacion de balances
                        boolean isShow = new SPHelper(mContext).getBoolean("usage_notif", false);
                        if (isShow) {
                            new BalanceNotification(mContext).show();
                        }
                    }

                    @Override
                    public void onError(UpdateData.SyncResult result) {
                        // Mostrar error detallado
                        Log.e("SyncError", "Error durante sincronización", result.error);
                    }

                    @Override
                    public void onComplete(UpdateData.SyncResult result) {
                        Log.d("SyncComplete", "Tiempo ejecución: " + result.executionTime + "ms");
                        boolean showNotifi =
                                new SPHelper(mContext).getBoolean("notif_update", false);
                        if (showNotifi) {
                            showNotification("¡Balances actualizados!");
                        }
                    }
                });
    }

    private boolean hasAccessToUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showNotification(String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                channel.setAllowBubbles(false);
            }
            channel.setShowBadge(false);
            NotificationManager notificationManager =
                    mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_app)
                        .setContentTitle(mContext.getString(R.string.app_name))
                        .setContentText(message)
                        .setVibrate(new long[] {0})
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Obtener el administrador de notificaciones
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, builder.build());
    }
}
