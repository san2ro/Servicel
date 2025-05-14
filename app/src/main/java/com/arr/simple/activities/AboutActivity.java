package com.arr.simple.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import com.arr.simple.BaseActivity;
import com.arr.simple.R;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.BuildConfig;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.ActivityAboutBinding;

public class AboutActivity extends BaseActivity {

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        goBackPressed();

        binding.version.setText("Versión " + BuildConfig.VERSION_NAME);

        binding.whatsapp.setOnClickListener(
                v -> startLink("https://whatsapp.com/channel/0029VabYDCFAu3aVs2zjBs35"));
        binding.correo.setOnClickListener(v -> sendEmail());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startToMainActivity();
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
                                startToMainActivity();
                            }
                        });
    }

    private void startToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void startLink(String link) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.putExtra(Intent.EXTRA_EMAIL, new String[] {"alessandroguez98@gmail.com"});
        i.setType("text/plain");
        i.setData(Uri.parse("mailto:"));
        try {
            startActivity(Intent.createChooser(i, "Enviar por correo"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_email_client, Toast.LENGTH_LONG).show();
        }
    }
}
