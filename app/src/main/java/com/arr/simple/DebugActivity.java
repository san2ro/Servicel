package com.arr.simple;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.arr.simple.BuildConfig;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String debugInfo = intent.getStringExtra("DEBUG_INFO");
        StringBuilder str = new StringBuilder();
        str.append("Dispositivo\n");
        str.append("Brand: ").append(Build.BRAND).append("\n");
        str.append("Model: ").append(Build.MODEL).append("\n");
        str.append("SDK: ").append(Build.VERSION.SDK).append("\n");
        str.append("\n*****Error encontrado******\n");
        str.append(getFirst30Lines(debugInfo));

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_error)
                .setIcon(R.drawable.ic_bug_24dp)
                .setMessage(R.string.message_error)
                .setCancelable(false)
                .setPositiveButton(
                        R.string.btn_send_error,
                        (dialog, width) -> {
                            setSendError(str.toString());
                            finish();
                        })
                .setNeutralButton(
                        R.string.btn_cancel_error,
                        (dialog, width) -> {
                            finish();
                        })
                .show();
    }

    private void setSendError(String error) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.putExtra(Intent.EXTRA_EMAIL, new String[] {"alessandroguez98@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "SERVICEL v" + BuildConfig.VERSION_NAME);
        i.putExtra(Intent.EXTRA_TEXT, error);
        i.setType("text/plain");
        i.setData(Uri.parse("mailto:"));
        try {
            startActivity(Intent.createChooser(i, "Enviar por correo"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_email_client, Toast.LENGTH_LONG).show();
        }
    }

    public static String getFirst30Lines(String text) {
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();
        int limit = Math.min(30, lines.length);
        for (int i = 0; i < limit; i++) {
            result.append(lines[i]).append("\n");
        }

        return result.toString();
    }
}
