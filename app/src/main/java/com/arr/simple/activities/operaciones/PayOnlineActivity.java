package com.arr.simple.activities.operaciones;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.BaseActivity;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.ActivityPayOnlineBinding;
import com.arr.simple.helpers.dialogs.PaymentDetails;
import com.arr.simple.helpers.dialogs.ProgressDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProfileManager;
import com.arr.simple.helpers.update.UpdateData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import cu.arr.etecsa.api.portal.PortalRetrofit;
import cu.arr.etecsa.api.portal.models.CheckPayResponse;
import cu.arr.etecsa.api.portal.models.PaymentResponse;
import cu.arr.etecsa.api.portal.request.CheckPayRequest;
import cu.arr.etecsa.api.portal.request.topup.TopupOnlineRequest;
import cu.arr.etecsa.api.portal.utils.ValidateToken;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.text.DecimalFormat;

public class PayOnlineActivity extends BaseActivity {

    private ActivityPayOnlineBinding binding;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressDialog dialog;

    private String idPayment;
    private String LINK_TM = "transfermovil://tm_compra_en_linea/action?id_transaccion=";

    private UpdateData update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayOnlineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        goBackPressed();

        update = new UpdateData(this);

        dialog = new ProgressDialog(this);

        binding.btnOk.setOnClickListener(this::pay);

        // formatear monto
        binding.monto.addTextChangedListener(new ValidateMonto());

        // comprobar estado de pago
        binding.btnCheck.setOnClickListener(this::ckeckPayment);

        // close
        binding.close.setOnClickListener(this::startToMainActivity);
    }

    private void ckeckPayment(View view) {
        if (idPayment != null || !TextUtils.isEmpty(idPayment)) {
            checkStatusPayment();
        }
    }

    private void pay(View view) {
        String auth = new SPHelper(this).getString("authentication", "");
        String account = binding.account.getText().toString();
        String monto = binding.monto.getText().toString().replace(".", "").replace(",", "");
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(monto)) {
            boolean isTokenValid = ValidateToken.isValidToken(auth.replace("Bearer ", ""));
            if (isTokenValid) {
                rechargeAccount(account, monto);
            } else {
                updateToken();
            }
        }
    }

    private void updateToken() {
        update.updateDataPortal(
                new UpdateData.UpdateDataCallback() {
                    @Override
                    public void onProgress(UpdateData.SyncStatus status, String message) {
                        dialog.show();
                    }

                    @Override
                    public void onSuccess(UpdateData.SyncResult result) {
                        new ProfileManager(PayOnlineActivity.this)
                                .processUserResponse(result.response);
                    }

                    @Override
                    public void onError(UpdateData.SyncResult result) {
                        // Mostrar error detallado
                        showMessage("Error: " + result.errorMessage);
                    }

                    @Override
                    public void onComplete(UpdateData.SyncResult result) {
                        // Operaciones finales (ocurre después de onSuccess/onError)
                        String account = binding.account.getText().toString();
                        String monto =
                                binding.monto
                                        .getText()
                                        .toString()
                                        .replace(".", "")
                                        .replace(",", "");
                        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(monto)) {
                            rechargeAccount(account, monto);
                        }
                    }
                });
    }

    private void rechargeAccount(String account, String monto) {
        String auth = new SPHelper(this).getString("authentication", "");
        String passwordApp = new SPHelper(this).getString("passwordApp", "");

        TopupOnlineRequest body = new TopupOnlineRequest(account, monto);
        Disposable disposable =
                PortalRetrofit.auth()
                        .topUpOnlineAccount(body, passwordApp, auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> dialog.show())
                        .subscribe(this::handlerResponse, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerResponse(PaymentResponse response) {
        if (response.data.resultado.equals("OK")) {
            idPayment = response.data.detalle.getTransactionId();
            dialog.dismiss();

            StringBuilder str = new StringBuilder();
            str.append(LINK_TM).append(response.data.detalle.getTransactionId());
            str.append("&importe=").append(response.data.detalle.getImporte());
            str.append("&moneda=").append(response.data.detalle.getMoneda());
            str.append("&numero_proveedor=");
            str.append(response.data.detalle.getNumeroProveedor());

            new PaymentDetails(this)
                    .setDescuento(String.valueOf(response.data.info.getDescuento()))
                    .setMontoPagado(response.data.info.getMontoPagado())
                    .setMontoFinal(response.data.info.getMontoFinal())
                    .setLinkTm(str.toString())
                    .show();
        }
    }

    private void checkStatusPayment() {
        String auth = new SPHelper(this).getString("authentication", "");
        String passwordApp = new SPHelper(this).getString("passwordApp", "");

        CheckPayRequest body = new CheckPayRequest(idPayment);
        Disposable disposable =
                PortalRetrofit.auth()
                        .checkPayment(body, passwordApp, auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> dialog.show())
                        .subscribe(this::handlerStatusPayment, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerStatusPayment(CheckPayResponse response) {
        if (response.data.resultado.equals("OK")) {
            dialog.dismiss();
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Estado de pago: " + response.data.detalle)
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }

    private void handlerError(Throwable e) {
        dialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (idPayment != null || !TextUtils.isEmpty(idPayment)) {
            checkStatusPayment();
        }
    }
}
