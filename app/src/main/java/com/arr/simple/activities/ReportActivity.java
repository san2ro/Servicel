package com.arr.simple.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.arr.simple.BaseActivity;
import com.arr.simple.R;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.BuildConfig;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.ActivityReportBinding;
import com.bumptech.glide.Glide;

public class ReportActivity extends BaseActivity {

    private ActivityReportBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        goBackPressed();

        binding.editReport.addTextChangedListener(new ValidateReport());
        binding.send.setEnabled(false);

        imagePickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                selectedImageUri = result.getData().getData();
                                binding.added.setVisibility(View.GONE);
                                binding.previewImage.setVisibility(View.VISIBLE);
                                Glide.with(this)
                                        .load(selectedImageUri)
                                        .centerCrop()
                                        .into(binding.previewImage);
                            }
                        });
        binding.send.setOnClickListener(this::sendReport);
        binding.added.setOnClickListener(this::pickImageFromGallery);
    }

    private void pickImageFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Selecciona una imagen"));
    }

    private void sendReport(View view) {
        String valueText = binding.editReport.getText().toString();
        if (!TextUtils.isEmpty(valueText)) {
            StringBuilder str = new StringBuilder();
            str.append("Dispositivo\n");
            str.append("Brand: ").append(Build.BRAND).append("\n");
            str.append("Model: ").append(Build.MODEL).append("\n");
            str.append("SDK: ").append(Build.VERSION.SDK).append("\n");
            str.append("\n****Reporte:\n");
            str.append(valueText);
            if (selectedImageUri != null) {
                sendEmailWithImage(str.toString(), selectedImageUri);
            } else {
                sendEmail(str.toString());
            }
        }
    }

    private void sendEmailWithImage(String report, Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"alessandroguez98@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "SERVICEL v" + BuildConfig.VERSION_NAME);
        intent.putExtra(Intent.EXTRA_TEXT, report);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);

        try {
            startActivity(Intent.createChooser(intent, "Enviar por correo"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_email_client, Toast.LENGTH_LONG).show();
        }
    }

    private void sendEmail(String report) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.putExtra(Intent.EXTRA_EMAIL, new String[] {"alessandroguez98@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "SERVICEL v" + BuildConfig.VERSION_NAME);
        i.putExtra(Intent.EXTRA_TEXT, report);
        i.setType("text/plain");
        i.setData(Uri.parse("mailto:"));
        try {
            startActivity(Intent.createChooser(i, "Enviar por correo"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_email_client, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startToMainActivity(null);
                return true;
            default:
                return false;
        }
    }

    private void goBackPressed() {
        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                startToMainActivity(null);
                            }
                        });
    }

    private void startToMainActivity(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public class ValidateReport implements TextWatcher {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void afterTextChanged(Editable e) {
            String value = e.toString();
            if (!TextUtils.isEmpty(value)) {
                binding.send.setEnabled(true);
            } else {
                binding.send.setEnabled(false);
            }
        }
    }
}
