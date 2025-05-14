package com.arr.simple.activities.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.arr.preference.ArrPreference;
import com.arr.preference.ArrPreferenceCategory;
import com.arr.preference.ArrSwitchPreference;
import com.arr.simple.BaseActivity;
import com.arr.simple.R;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.arr.simple.MainActivity;
import com.arr.simple.activities.PerfilActivity;
import com.arr.simple.databinding.ActivitySettingsBinding;
import com.arr.simple.helpers.notification.BalanceNotification;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.theme.ThemePreference;
import com.arr.simple.worker.UpdateWorker;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;
    private static boolean isFirstAccess;

    private static BalanceNotification balanceNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        goBackPressed();

        // boolean para comprobar si el usuario accedió antes al login
        isFirstAccess = new SPHelper(this).getBoolean("firstAccess", true);

        // notificación de balances
        balanceNotif = new BalanceNotification(this);

        // close activity
        binding.close.setOnClickListener(this::launchMainActivity);

        // inflater SettingsPreference
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new SettingsPreferences())
                .commit();
    }

    private void goBackPressed() {
        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                launchMainActivity(null);
                            }
                        });
    }

    private void launchMainActivity(View v) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public static class SettingsPreferences extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_root, rootKey);

            // perfil category
            ArrPreferenceCategory categoryPerfil = findPreference("account_category");
            categoryPerfil.setVisible(!isFirstAccess);

            // acceder a perfil
            ArrPreference perfil = findPreference("account");
            if (perfil == null) return;
            perfil.setOnPreferenceClickListener(this::startToPerfil);

            // activar tema AMOLED
            ArrSwitchPreference amoled = findPreference("amoled");
            if (amoled == null) return;
            amoled.setOnPreferenceChangeListener(this::setAmoledTheme);

            // iniciar un worker para actualizar balances
            ArrSwitchPreference update = findPreference("update_balances");
            if (update == null) return;
            update.setOnPreferenceChangeListener(this::initWorkerUpdateBalances);

            // deshabilitar optimización de batería
            ArrPreference battery = findPreference("ahorro");
            if (battery == null) return;
            battery.setOnPreferenceClickListener(this::disableBatteryOptimization);

            // notificacion de balances
            ArrSwitchPreference notiBalance = findPreference("usage_notif");
            if (notiBalance == null) return;
            notiBalance.setOnPreferenceChangeListener(this::showBalanceNotification);
        }

        private boolean showBalanceNotification(Preference preference, Object newValue) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && ContextCompat.checkSelfPermission(
                                    requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        new String[] {Manifest.permission.POST_NOTIFICATIONS},
                        90);
                return false;
            }
            boolean isChecked = (Boolean) newValue;
            if (isChecked) {
                balanceNotif.enableNotifications();
            } else {
                balanceNotif.disableNotifications();
            }

            return true;
        }

        private boolean startToPerfil(Preference preference) {
            startActivity(new Intent(requireActivity(), PerfilActivity.class));
            getActivity().finish();
            return true;
        }

        private boolean setAmoledTheme(Preference preference, Object newValue) {
            boolean isChecked = (Boolean) newValue;
            ThemePreference.setAmoledThemeEnabled(requireContext(), isChecked);
            requireActivity().recreate();
            return true;
        }

        private boolean initWorkerUpdateBalances(Preference preference, Object newValue) {
            boolean isChecked = (Boolean) newValue;
            String value = isChecked ? "30" : "manual";
            updateBalances(value);
            return true;
        }

        private void updateBalances(Object value) {
            switch (value.toString()) {
                case "manual":
                    WorkManager.getInstance(requireContext()).cancelUniqueWork("BALANCES");
                    break;
                case "30":
                    startWorker(30, TimeUnit.MINUTES);
                    break;
            }
        }

        private void startWorker(int interval, TimeUnit timeUnit) {
            Constraints constraints =
                    new Constraints.Builder().setRequiresBatteryNotLow(true).build();
            PeriodicWorkRequest work =
                    new PeriodicWorkRequest.Builder(UpdateWorker.class, interval, timeUnit)
                            .setConstraints(constraints)
                            .build();
            WorkManager.getInstance(requireContext())
                    .enqueueUniquePeriodicWork("BALANCES", ExistingPeriodicWorkPolicy.KEEP, work);
        }

        private boolean disableBatteryOptimization(Preference preference) {
            initIgnoringBatteryOptimizations();
            return true;
        }

        private void initIgnoringBatteryOptimizations() {
            PowerManager manager =
                    (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            if (!manager.isIgnoringBatteryOptimizations(getActivity().getPackageName())) {
                try {
                    Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    i.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(
                                requireContext(),
                                "Ahorro de batería ya está deshabilitado",
                                Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
