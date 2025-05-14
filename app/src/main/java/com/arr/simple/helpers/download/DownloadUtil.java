package com.arr.simple.helpers.download;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

public class DownloadUtil {

    public static void downloadFile(Context context, String url, String fileName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            // Configuración adicional
            request.setTitle(fileName)
                    .setDescription("Descargando actualización...")
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(false)
                    .setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            // Para Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                request.setRequiresCharging(false);
                request.setRequiresDeviceIdle(false);
            }

            DownloadManager dm =
                    (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
                Toast.makeText(
                                context,
                                "Descarga iniciada, consulte la barra de notificaciones",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            Toast.makeText(
                            context,
                            "Error al iniciar la descarga: " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }
}
