package com.arr.simple.activities.operaciones;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.BaseActivity;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.ActivityNewNavAccountBinding;
import com.arr.simple.helpers.dialogs.ProgressDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import cu.arr.etecsa.api.portal.PortalRetrofit;
import cu.arr.etecsa.api.portal.models.accounts.ProcessResponse;
import cu.arr.etecsa.api.portal.models.operations.Detalle;
import cu.arr.etecsa.api.portal.models.operations.OperationId;
import cu.arr.etecsa.api.portal.models.operations.OperationsResponse;
import cu.arr.etecsa.api.portal.request.account.StatusOpRequest;
import cu.arr.etecsa.api.portal.request.operations.navigation.accounts.newaccount.OpCreateAccount;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Map;

public class NewAccountNavActivity extends BaseActivity {

    private ActivityNewNavAccountBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewNavAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        goBackPressed();

        dialog = new ProgressDialog(this);

        // close
        binding.close.setOnClickListener(this::startToMainActivity);

        // validar usuario
        binding.account.addTextChangedListener(new ValidateEmail());

        // validar contraseña
        binding.password.addTextChangedListener(new ValidatePassword());

        binding.btnOk.setOnClickListener(this::create);
    }

    private void create(View view) {
        String username = binding.account.getText().toString();
        String password = binding.password.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            createAccount(username + "@nauta.com.cu", password);
        }
    }

    private void createAccount(String username, String password) {
        String clientId = ProcessProfile.getClientId(this);
        String auth = new SPHelper(this).getString("authentication", "");
        String passwordApp = new SPHelper(this).getString("passwordApp", "");

        OpCreateAccount body = new OpCreateAccount(username, password, clientId);
        Disposable disponsable =
                PortalRetrofit.auth()
                        .opCreateAccount(body, passwordApp, auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> dialog.show())
                        .subscribe(this::handlerResponse, this::handlerError);
        compositeDisposable.add(disponsable);
    }

    private void handlerResponse(OperationsResponse response) {
        if (response == null || response.getData() == null) return;

        Detalle detalle = response.getData().getDetalle();
        String resultado = response.getData().getResultado();

        switch (resultado) {
            case "PROCESANDO":
                if (response.getData().getDetalle() != null
                        && response.getData().getDetalle().isOperacion()) {
                    OperationId opId = response.getData().getDetalle().getAsOperacion();
                    String operationId = opId.getOperacionId();
                    checkStatus(operationId);
                }
                break;

            case "ERROR":
                if (response.getData().getDetalle() != null
                        && response.getData().getDetalle().isString()) {
                    String errorMsg = response.getData().getDetalle().getAsString();
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Error")
                            .setMessage(errorMsg)
                            .setPositiveButton("Aceptar", null)
                            .show();
                }
                dialog.dismiss();
                break;
        }
    }

    private void checkStatus(String operationId) {
        String auth = new SPHelper(this).getString("authentication", "");
        String passwordApp = new SPHelper(this).getString("passwordApp", "");

        StatusOpRequest body = new StatusOpRequest(operationId);
        Disposable disposable =
                PortalRetrofit.auth()
                        .statusOp(body, passwordApp, auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handlerStatusOp, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerStatusOp(ProcessResponse response) {
        dialog.dismiss();
        new MaterialAlertDialogBuilder(this)
                .setMessage("Estado de su cuenta: " + response.data.estado)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void handlerError(Throwable e) {
        dialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    public class ValidateEmail implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void afterTextChanged(Editable e) {
            String value = e.toString();
            if (value.contains("@")) {
                binding.account.getText().clear();
                binding.layoutAccount.setError("Inserte su usuario sin el @nauta.com.cu");
                binding.layoutAccount.setErrorEnabled(true);
            } else {
                binding.layoutAccount.setErrorEnabled(false);
            }
        }
    }

    public class ValidatePassword implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void afterTextChanged(Editable e) {
            String value = e.toString();
            if (value.length() < 8) {
                binding.layoutPassword.setError("La contraseña es muy débil");
                binding.layoutPassword.setErrorEnabled(true);
            } else if (value.length() >= 8) {
                binding.layoutPassword.setErrorEnabled(false);
            }
        }
    }
}
