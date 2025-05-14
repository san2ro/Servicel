package com.arr.simple.activities.operaciones;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.BaseActivity;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.ActivityTransferBinding;
import com.arr.simple.helpers.dialogs.ProgressDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.arr.simple.helpers.update.UpdateData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import cu.arr.etecsa.api.portal.PortalRetrofit;
import cu.arr.etecsa.api.portal.models.accounts.ProcessResponse;
import cu.arr.etecsa.api.portal.models.accounts.SendMoneyResponse;
import cu.arr.etecsa.api.portal.request.account.SendMoneyRequest;
import cu.arr.etecsa.api.portal.request.account.StatusOpRequest;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.text.DecimalFormat;
import java.util.List;

public class TransferActivity extends BaseActivity {

    private ActivityTransferBinding binding;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressDialog dialog;

    private String operationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        goBackPressed();

        dialog = new ProgressDialog(this);

        // listas de cuentas
        List<String> accounts = ProcessProfile.getAccounts(this);
        if (!accounts.isEmpty()) {
            binding.listAccounts.setText(accounts.get(0), false);
        }
        binding.listAccounts.setAdapter(getListAccounts());

        binding.btnOk.setOnClickListener(this::sendMoney);

        // formatear monto
        binding.monto.addTextChangedListener(new ValidateMonto());

        // close
        binding.close.setOnClickListener(this::startToMainActivity);
    }

    private void sendMoney(View view) {
        String from = binding.listAccounts.getText().toString();
        String to = binding.account.getText().toString();
        String password = binding.password.getText().toString();
        String monto = binding.monto.getText().toString().replace(".", "").replace(",", "");
        if (!TextUtils.isEmpty(from)
                || !TextUtils.isEmpty(to)
                || !TextUtils.isEmpty(password)
                || !TextUtils.isEmpty(monto)) {
            send(from, to, password, monto);
        }
    }

    private void send(String account, String accountTo, String password, String monto) {
        String auth = new SPHelper(this).getString("authentication", "");
        String passwordApp = new SPHelper(this).getString("passwordApp", "");

        SendMoneyRequest body = new SendMoneyRequest(account, accountTo, password, monto);
        Disposable disposable =
                PortalRetrofit.auth()
                        .sendCurrency(body, passwordApp, auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> dialog.show())
                        .subscribe(this::handlerResponse, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerResponse(SendMoneyResponse response) {
        if ("PROCESANDO".equals(response.data.getResult())) {
            operationId = response.data.detalle.getOperationId();
            checkStatus(operationId);
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
                .setMessage("Estado de la transferencia: " + response.data.estado)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void handlerError(Throwable e) {
        dialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private ArrayAdapter<String> getListAccounts() {
        List<String> accounts = ProcessProfile.getAccounts(this);
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

    // formatear el monto en números decimales
    public class ValidateMonto implements TextWatcher {

        private boolean isFormatting = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No necesitamos manejar este método
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // No necesitamos manejar este método
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isFormatting) {
                return;
            }

            isFormatting = true;

            try {
                String originalString = s.toString();
                if (originalString.isEmpty()) {
                    return;
                }

                // Elimina cualquier coma o punto existente
                String cleanString = originalString.replaceAll("[,\\.]", "");

                // Convierte el string a número
                double parsed = Double.parseDouble(cleanString);

                // Formatea el número con dos decimales
                DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
                String formattedString = formatter.format(parsed / 100);

                // Establece el texto formateado
                binding.monto.setText(formattedString);
                binding.monto.setSelection(formattedString.length());

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            isFormatting = false;
        }
    }
}
