package com.arr.simple.helpers.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import com.arr.simple.R;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import java.util.Map;

public class BalanceNotification {
    private static final String CHANNEL_ID = "balance_channel";
    private static final String CHANNEL_NAME = "Balance Notifications";
    private static final int NOTIFICATION_ID = 1001;

    private Context context;
    private SPHelper spHelper;

    public BalanceNotification(Context context) {
        this.context = context;
        this.spHelper = new SPHelper(context);
        createNotificationChannel();
    }

    // Método para verificar si las notificaciones están activadas
    public boolean areNotificationsEnabled() {
        return spHelper.getBoolean("usage_notif", true); // true por defecto
    }

    // Método para activar las notificaciones
    public void enableNotifications() {
        spHelper.setBoolean("usage_notif", true);
        // Opcional: Mostrar notificación inmediatamente al activar
        show();
    }

    // Método para desactivar las notificaciones
    public void disableNotifications() {
        spHelper.setBoolean("usage_notif", false);
        cancelNotification(); // Cancelar notificación actual si existe
    }

    // Método para mostrar la notificación (con verificación)
    public void show() {
        if (!areNotificationsEnabled()) {
            return;
        }

        RemoteViews remoteViews =
                new RemoteViews(
                        context.getPackageName(), R.layout.layout_custom_notification_balance);

        String phone = spHelper.getString("phone", "");
        Map<String, String> planes = ProcessProfile.getDataPlans(context, phone);

        remoteViews.setTextViewText(R.id.all_network, planes.get("DATOS"));
        remoteViews.setTextViewText(R.id.lte_network, planes.get("DATOS_LTE"));
        remoteViews.setTextViewText(R.id.expire, planes.get("DATOS_VENCE"));

        Notification notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_usage_18dp)
                        .setCustomContentView(remoteViews)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setAutoCancel(false)
                        .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

    // Método para cancelar la notificación
    public void cancelNotification() {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Notifications for balance updates");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
