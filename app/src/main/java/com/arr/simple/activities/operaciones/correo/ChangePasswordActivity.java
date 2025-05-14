package com.arr.simple.activities.operaciones.correo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import com.arr.simple.BaseActivity;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.ActivityChangePasswordBinding;
import com.arr.simple.helpers.dialogs.ProgressDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import cu.arr.etecsa.api.portal.PortalRetrofit;
import cu.arr.etecsa.api.portal.models.accounts.ProcessResponse;
import cu.arr.etecsa.api.portal.models.operations.OperationId;
import cu.arr.etecsa.api.portal.models.operations.OperationsResponse;
import cu.arr.etecsa.api.portal.request.account.StatusOpRequest;
import cu.arr.etecsa.api.portal.request.correo.ChangeMailPassword;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.List;

public class ChangePasswordActivity extends BaseActivity {

    private ActivityChangePasswordBinding binding;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().hide();
        goBackPressed();
        binding.subtitle.setText("Cambiar contraseña de su correo");

        dialog = new ProgressDialog(this);

        // cambiar contraseña
        binding.btnOk.setOnClickListener(this::change);

        // close
        binding.close.setOnClickListener(this::startToMainActivity);

        // listas de cuentas
        List<String> accounts = ProcessProfile.getCorreos(this);
        if (!accounts.isEmpty()) {
            binding.listAccounts.setText(accounts.get(0), false);
        }
        binding.listAccounts.setAdapter(getListAccounts());
    }

    private void change(View view) {
        String account = binding.listAccounts.getText().toString();
        String password = binding.password.getText().toString();
        String newPassword = binding.newPassword.getText().toString();
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(newPassword)) {
            changePassword(account, password, newPassword);
        }
    }

    private void changePassword(String account, String password, String newPassword) {
        String auth = new SPHelper(this).getString("authentication", "");
        String passwordApp = new SPHelper(this).getString("passwordApp", "");
        ChangeMailPassword body = new ChangeMailPassword(account, password, newPassword);
        Disposable disposable =
                PortalRetrofit.auth()
                        .changeMailPassword(body, passwordApp, auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> dialog.show())
                        .subscribe(this::handlerResponse, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerResponse(OperationsResponse response) {
        if (response == null || response.getData() == null) return;
        String resultado = response.getData().getResultado();
        switch (resultado) {
            case "PROCESANDO":
                if (response.getData().getDetalle() != null
                        && response.getData().getDetalle().isOperacion()) {
                    OperationId opId = response.getData().getDetalle().getAsOperacion();
                    String operationId = opId.getOperacionId();
                    checkOperation(operationId);
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

    private void checkOperation(String operationId) {
        String auth = new SPHelper(this).getString("authentication", "");
        String passwordApp = new SPHelper(this).getString("passwordApp", "");

        StatusOpRequest body = new StatusOpRequest(operationId);
        Disposable disposable =
                PortalRetrofit.auth()
                        .statusOp(body, passwordApp, auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handlerOperationResult, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerOperationResult(ProcessResponse response) {
        showMessage("Contraseña restablecida");
        dialog.dismiss();
    }

    private void handlerError(Throwable e) {
        dialog.dismiss();
        showMessage(e.getMessage());
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private ArrayAdapter<String> getListAccounts() {
        List<String> accounts = ProcessProfile.getCorreos(this);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this, android.R.layout.simple_dropdown_item_1line, accounts);
        return adapter;
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
}
