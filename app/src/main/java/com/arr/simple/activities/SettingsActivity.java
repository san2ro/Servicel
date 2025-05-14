package com.arr.simple.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.arr.preference.ArrPreference;
import com.arr.preference.ArrPreferenceCategory;
import com.arr.preference.ArrSwitchPreference;
import com.arr.simple.BaseActivity;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import com.arr.simple.databinding.ActivitySettingsBinding;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.theme.ThemePreference;
import com.arr.simple.worker.UpdateWorker;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        goBackPressed();

        if (getSupportFragmentManager().findFragmentById(R.id.frame_layout) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new SettingsPreference())
                    .commit();
        }
    }

    public static class SettingsPreference extends PreferenceFragmentCompat {

        private PowerManager powerManager;
        private String BATTERY_OPTIMIZATION = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_root, rootKey);

            // PowerManager service
            powerManager = (PowerManager) requireActivity().getSystemService(Context.POWER_SERVICE);

            // cuenta
            boolean notFirstLogin = new SPHelper(requireContext()).getBoolean("firstAccess", true);
            ArrPreferenceCategory account_category = findPreference("account_category");
            account_category.setVisible(!notFirstLogin);

            ArrPreference cuenta = findPreference("account");
            cuenta.setVisible(!notFirstLogin);
            if (cuenta != null) {
                cuenta.setOnPreferenceClickListener(
                        (preference) -> {
                            startActivity(new Intent(requireActivity(), PerfilActivity.class));
                            requireActivity().finish();
                            return true;
                        });
            }

            // actualizar automáticamente los balances
            ArrSwitchPreference update = findPreference("update");
            if (update != null) {
                update.setEnabled(!notFirstLogin);
                update.setOnPreferenceChangeListener(
                        (preference, newValue) -> {
                            boolean isChecked = (Boolean) newValue;
                            String value = isChecked ? "30" : "manual";
                            updateBalances(value);
                            return true;
                        });
            }

            // amoled
            ArrSwitchPreference amoled = findPreference("amoled");
            if (amoled != null) {
                amoled.setOnPreferenceChangeListener(
                        (preference, newValue) -> {
                            boolean isChecked = (Boolean) newValue;
                            ThemePreference.setAmoledThemeEnabled(requireActivity(), isChecked);
                            restartApp();
                            return true;
                        });
            }

            // ahorro de bateria
            ArrPreference battery = findPreference("ahorro");
            if (battery != null) {
                battery.setOnPreferenceClickListener(this::onPreferenceClick);
            }

            // notificar actualizaciones de balances
            ArrSwitchPreference notification = findPreference("notification");
            if (notification != null) {
                notification.setEnabled(!notFirstLogin);
                notification.setOnPreferenceChangeListener(
                        (preference, newValue) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                                if (ContextCompat.checkSelfPermission(
                                                requireContext(),
                                                Manifest.permission.POST_NOTIFICATIONS)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(
                                            requireActivity(),
                                            new String[] {Manifest.permission.POST_NOTIFICATIONS},
                                            100);
                                    return false;
                                }
                            }
                            return true;
                        });
            }
        }

        private void restartApp() {
            requireActivity().recreate();
        }

        private boolean onPreferenceClick(Preference preference) {
            if (powerManager != null) {
                if (!powerManager.isIgnoringBatteryOptimizations(
                        requireActivity().getPackageName())) {
                    try {
                        @SuppressLint("BatteryLife")
                        Intent intent = new Intent(BATTERY_OPTIMIZATION);
                        intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                        startActivity(intent);
                    } catch (Exception e) {
                        // Si falla, abre la configuración general de batería
                        Intent intent = new Intent(BATTERY_OPTIMIZATION);
                        startActivity(intent);
                        showMessage(
                                "Por favor, desactiva manualmente el ahorro de batería para esta app");
                    }
                } else {
                    showMessage("El ahorro de batería ya está desactivado");
                }
            } else {
                showMessage("No se pudo acceder al servicio de energía");
            }
            return true;
        }

        private void showMessage(String message) {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
        }

        private void updateBalances(Object value) {
            switch (value.toString()) {
                case "manual":
                    WorkManager.getInstance(requireContext()).cancelUniqueWork("UPDATE");
                    Toast.makeText(requireActivity(), "Se detuvo", Toast.LENGTH_LONG).show();
                    break;
                case "30":
                    startWorker(30, TimeUnit.MINUTES);
                    break;
            }
        }

        private void startWorker(int interval, TimeUnit timeUnit) {
            PeriodicWorkRequest workRequest =
                    new PeriodicWorkRequest.Builder(UpdateWorker.class, interval, timeUnit)
                            .setConstraints(
                                    new Constraints.Builder()
                                            .setRequiresBatteryNotLow(true)
                                            .build())
                            .build();
            WorkManager.getInstance(requireContext())
                    .enqueueUniquePeriodicWork(
                            "UPDATE", ExistingPeriodicWorkPolicy.KEEP, workRequest);
        }
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
                                        new Intent(SettingsActivity.this, MainActivity.class));
                                finish();
                            }
                        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.shared_axis_z_in, R.anim.shared_axis_z_out);
    }
}
