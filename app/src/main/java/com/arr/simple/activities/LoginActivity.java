package com.arr.simple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import com.arr.simple.BaseActivity;
import com.arr.simple.MainActivity;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.databinding.ActivityLoginBinding;
import com.arr.simple.helpers.data.LoginData;
import com.arr.simple.helpers.dialogs.ProgressDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.arr.simple.helpers.profile.ProfileManager;
import com.arr.simple.helpers.progress.ProgressIndicator;
import com.arr.simple.helpers.ui.TypeUser;
import com.google.android.material.snackbar.Snackbar;

import cu.arr.etecsa.api.portal.PortalRetrofit;
import cu.arr.etecsa.api.portal.models.User;
import cu.arr.etecsa.api.portal.models.captcha.CaptchaResponse;
import cu.arr.etecsa.api.portal.models.login.LoginResponse;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;
import cu.arr.etecsa.api.portal.request.login.LoginRequest;
import cu.arr.etecsa.api.portal.request.users.UsersRequest;
import cu.arr.etecsa.api.portal.utils.PasswordApp;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String idRequest;
    private boolean typeUsername;

    private String mToken;

    private ProgressDialog dialog;

    private long firstFalseTime = 0;
    private int falseCounter = 0;

    private boolean plansUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        goBackPressed();

        dialog = new ProgressDialog(this);

        // se actualizó los planes
        plansUpdated = ProcessProfile.updatedPlans(this);

        loadCaptcha(null); // cargar código captcha
        binding.imageCaptcha.setOnClickListener(this::loadCaptcha);

        // cambiar el tipo de usuario
        binding.layoutUsername.setEndIconOnClickListener(this::setTypeUsername);

        // iniciar sesión
        binding.btnLogin.setOnClickListener(this::loginPortal);
    }

    private void loadCaptcha(View view) {
        Disposable disposable =
                PortalRetrofit.auth()
                        .getCaptcha()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> binding.imageCaptcha.setEnabled(false))
                        .doFinally(() -> binding.imageCaptcha.setEnabled(true))
                        .subscribe(this::handlerCaptcha, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void loginPortal(View view) {
        String username = binding.editUsername.getText().toString();
        String password = binding.editPassword.getText().toString();
        String captcha = binding.editCaptcha.getText().toString();
        username = typeUsername ? username + "@nauta.cu" : "+53" + username;
        if (!TextUtils.isEmpty(username)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(captcha)) {
            loginUser(username, password, captcha);
        } else {
            showMessage("No deje campos vacíos");
        }
    }

    private void loginUser(String username, String password, String captcha) {
        new SPHelper(this).setString("username", username);
        new SPHelper(this).setString("password", password);
        new SPHelper(this).setString("captcha", captcha);
        new SPHelper(this).setString("idRequest", idRequest);

        LoginRequest body = new LoginRequest(username, password, idRequest, captcha);
        String passwordApp = PasswordApp.getPasswordApp();
        Disposable disposable =
                PortalRetrofit.auth()
                        .login(body, passwordApp)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> dialog.show())
                        .map(loginResponse -> Pair.create(loginResponse, passwordApp))
                        .subscribe(
                                pair -> handlerLogin(pair.first, pair.second), this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void users(String email, String lastUpdate, String passwordApp, String auth) {
        UsersRequest body = new UsersRequest(email, lastUpdate);

        new SPHelper(this).setString("lastUpdate", lastUpdate);
        new SPHelper(this).setString("authentication", "Bearer " + auth);
        new SPHelper(this).setString("passwordApp", passwordApp);

        Disposable disposable =
                PortalRetrofit.auth()
                        .authUser(body, passwordApp, "Bearer " + auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                resp -> {
                                    handlerUsers(resp, passwordApp);
                                },
                                this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerCaptcha(CaptchaResponse response) {
        try {
            binding.imageCaptcha.setScaleType(ImageView.ScaleType.FIT_XY);
            binding.imageCaptcha.setPadding(0, 0, 0, 0);
            binding.imageCaptcha.setImageBitmap(response.getCaptchaBitmap());

            // idRequest login
            idRequest = response.getRequestId();
        } catch (Exception err) {
            binding.imageCaptcha.setEnabled(false);
            err.printStackTrace();
        }
    }

    private void handlerLogin(LoginResponse response, String passwordApp) {
        if ("ok".equalsIgnoreCase(response.getResultado())) {
            Object userObject = response.getUser();
            if (userObject instanceof User) {
                User user = (User) userObject;
                if ("false".equals(user.getUpdateServices())) {
                    String email = user.getCliente().getUsuarioPortal();
                    String lastUpdate = user.getUpdateDate();
                    mToken = response.getToken();
                    users(email, lastUpdate, passwordApp, mToken);
                }
            }
        }
    }

    private void handlerUsers(UsersResponse response, String passwordApp) {
        if ("ok".equalsIgnoreCase(response.getResultado())) {
            Object userObject = response.getUser();
            if (userObject instanceof User) {
                User user = (User) userObject;
                if ("false".equals(user.getCompleted())) {
                    if (falseCounter == 0) {
                        firstFalseTime = System.currentTimeMillis();
                    }
                    falseCounter++;
                    if (falseCounter >= 10
                            || (System.currentTimeMillis() - firstFalseTime) > 30000) {
                        new SPHelper(this).setBoolean("firstAccess", false);
                        new ProfileManager(this).processUserResponse(response);
                        dialog.dismiss();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                        showMessage("El servidor ha tardado mucho en dar una respuesta");
                        return;
                    }
                    String username = user.getCliente().getUsuarioPortal();
                    String lastUpdate = user.getUpdateDate();
                    users(username, lastUpdate, passwordApp, mToken);
                } else {
                    new SPHelper(this).setBoolean("firstAccess", false);
                    new ProfileManager(this).processUserResponse(response);
                    dialog.dismiss();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            }
        }
    }

    private void handlerError(Throwable e) {
        if (e instanceof IOException err) {
            dialog.dismiss();
            showMessage("Error de red, intentelo de nuevo");
        } else {
            dialog.dismiss();
            Log.e("ERR", e.getMessage());
            showMessage("Error " + e.getMessage());
        }
    }

    private void showMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void setTypeUsername(View view) {
        if (typeUsername) {
            TypeUser.typeMobileUsername(binding.layoutUsername, binding.editUsername);
            typeUsername = false;
        } else {
            TypeUser.typeMailUsername(binding.layoutUsername, binding.editUsername);
            typeUsername = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        dialog.dismiss();
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
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        });
    }
}
