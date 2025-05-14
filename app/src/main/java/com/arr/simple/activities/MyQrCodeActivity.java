package com.arr.simple.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.arr.simple.BaseActivity;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.adapters.TabAdapter;
import com.arr.simple.databinding.ActivityQrcodeBinding;
import com.arr.simple.fragment.qrcode.GenQrFragment;
import com.arr.simple.fragment.qrcode.ScannerQrFragment;
import com.arr.simple.helpers.data.ProcessData;
import com.arr.simple.helpers.qr.QRGen;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyQrCodeActivity extends BaseActivity {

    private ActivityQrcodeBinding binding;
    private ActivityResultLauncher<String> cameraPermission;
    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        goBackPressed();

        // Primero inicializa el ActivityResultLauncher
        cameraPermission =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                adapter.addFragment(new ScannerQrFragment());
                                binding.viewPager.getAdapter().notifyDataSetChanged();
                            }
                        });

        adapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());
        adapter.addFragment(new GenQrFragment());

        // Ahora llama a setScannerQR después de que cameraPermission está inicializado
        setScannerQR();

        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPager.setAdapter(adapter);
        TabLayoutMediator mediator =
                new TabLayoutMediator(
                        binding.tab,
                        binding.viewPager,
                        (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText("Mi código");
                                    break;
                                case 1:
                                    tab.setText("Escanear código");
                                    break;
                            }
                        });
        mediator.attach();
    }

    private void setScannerQR() {
        if (getCameraPermission()) {
            adapter.addFragment(new ScannerQrFragment());
        } else {
            cameraPermission.launch(Manifest.permission.CAMERA);
        }
    }

    private boolean getCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
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
                                startActivity(
                                        new Intent(MyQrCodeActivity.this, MainActivity.class));
                                finish();
                            }
                        });
    }
}
