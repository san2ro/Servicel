package com.arr.simple;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.arr.apklistool.ApklisRetrofit;
import com.arr.apklistool.models.ApklisResponse;
import com.arr.apklistool.models.UrlRequest;
import com.arr.apklistool.models.UrlResponse;
import com.arr.simple.activities.LoginActivity;
import com.arr.simple.databinding.ActivityMainBinding;
import com.arr.simple.helpers.download.DownloadUtil;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.theme.ThemePreference;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 40;

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfig;
    private NavController navController;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String urlDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViews();
        setupNavigation();
        checkForAppUpdates();
        restoreNavigationState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        compositeDisposable.clear();
    }

    // Configuración de Navegación
    private void setupNavigation() {
        setupAppBarConfiguration();
        setupNavController();
        setupBottomNavigation();
        setupDestinationChangedListener();
    }

    private void setupAppBarConfiguration() {
        appBarConfig =
                new AppBarConfiguration.Builder(
                                R.id.home, R.id.shop, R.id.balances, R.id.operations)
                        .build();
    }

    private void setupNavController() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
    }

    private void setupBottomNavigation() {
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        // Configurar visibilidad de operaciones basado en primer acceso
        boolean isFirstLogin = new SPHelper(this).getBoolean("firstAccess", true);
        setOperationsVisibility(!isFirstLogin);
    }

    private void setupDestinationChangedListener() {
        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {
                    handleDestinationChange(destination.getId());
                    saveCurrentDestination(destination.getId());
                });
    }

    private void restoreNavigationState() {
        int savedNavId = new SPHelper(this).getInt("nav", R.id.home);
        if (navController.getCurrentDestination() != null) {
            navController.navigate(savedNavId);
        }
    }

    //  Manejo de Actualizaciones
    private void checkForAppUpdates() {
        Disposable disposable =
                ApklisRetrofit.auth()
                        .getAppInfo(this.getPackageName())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleUpdateResponse, this::handleError);
        compositeDisposable.add(disposable);
    }

    private void handleUpdateResponse(ApklisResponse response) {
        if (response.getLastRelease().getVersionCode() > BuildConfig.VERSION_CODE) {
            showUpdateDialog(response);
        }
    }

    private void showUpdateDialog(ApklisResponse response) {
        genUrl(response.getLastRelease().sha);

        StringBuilder message = new StringBuilder();
        message.append(
                "Hay una nueva actualización de la aplicación disponible en Apklis.<br><br>");
        message.append("<b>Versión:</b> ").append(response.getLastRelease().versionName);
        message.append("<br>");
        message.append("<b>Tamaño:</b> ").append(response.size);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Nueva versión")
                .setMessage(Html.fromHtml(message.toString(), Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(
                        "Descargar",
                        (dialog, which) -> {
                            String fileName = buildApkFileName(response);
                            initDownload(urlDownload, fileName);
                        })
                .show();
    }

    private String buildApkFileName(ApklisResponse response) {
        return response.getLastRelease().appName
                + "-"
                + response.getLastRelease().versionName
                + ".apk";
    }

    private void initDownload(String url, String fileName) {
        if (requiresStoragePermission()) {
            requestStoragePermission();
        }
        DownloadUtil.downloadFile(this, url, fileName);
    }

    private boolean requiresStoragePermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                && ContextCompat.checkSelfPermission(
                                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }

    private void genUrl(String release) {
        UrlRequest body = new UrlRequest(release);
        Disposable disposable =
                ApklisRetrofit.auth()
                        .getUrl(body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleUrlResponse, this::handleError);
        compositeDisposable.add(disposable);
    }

    // Métodos de UI
    private void initializeViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().hide();
    }

    private void setOperationsVisibility(boolean visible) {
        binding.bottomNavigation.getMenu().findItem(R.id.operations).setVisible(visible);
    }

    //  Manejo de Eventos
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Métodos Auxiliares
    private void handleDestinationChange(int destinationId) {
        switch (destinationId) {
            case R.id.scanner:
                binding.bottomNavigation.setVisibility(View.GONE);
                break;
            default:
                binding.bottomNavigation.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void saveCurrentDestination(int destinationId) {
        if (destinationId == R.id.home
                || destinationId == R.id.balances
                || destinationId == R.id.shop
                || destinationId == R.id.operations) {
            new SPHelper(this).setInt("nav", destinationId);
        }
    }

    private void handleUrlResponse(UrlResponse response) {
        urlDownload = response.url;
    }

    private void handleError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
