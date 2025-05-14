package com.arr.simple.helpers.qr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

public class QRGen {

    public static Bitmap generate(Context context, String text, @Nullable Integer iconResId) {
        try {
            int size = 500; // Tamaño del código QR
            BitMatrix bitMatrix =
                    new MultiFormatWriter()
                            .encode(text, BarcodeFormat.QR_CODE, size, size, getEncodeHints());

            Bitmap qrBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Si se proporciona un icono, superponerlo en el centro del código QR
            if (iconResId != null) {
                Bitmap logo = drawableToBitmap(context, iconResId);
                if (logo != null) {
                    int overlaySize = size / 5;
                    Bitmap overlay =
                            Bitmap.createScaledBitmap(logo, overlaySize, overlaySize, false);

                    Canvas canvas = new Canvas(qrBitmap);
                    int left = (qrBitmap.getWidth() - overlay.getWidth()) / 2;
                    int top = (qrBitmap.getHeight() - overlay.getHeight()) / 2;
                    canvas.drawBitmap(overlay, left, top, null);
                } else {
                    Log.e(
                            "QRGen",
                            "No se pudo cargar el logotipo. Se generará el código QR sin logotipo.");
                }
            }

            return qrBitmap;
        } catch (Exception e) {
            Log.e("QRGen", "Error al generar el código QR: " + e.getMessage());
            return null;
        }
    }

    private static Map<EncodeHintType, Object> getEncodeHints() {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(
                EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.H); // Nivel alto de corrección de errores
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        return hints;
    }

    private static Bitmap drawableToBitmap(Context context, int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        if (drawable == null) {
            Log.e("QRGen", "Drawable no encontrado para el recurso ID: " + drawableResId);
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 1;
        int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
