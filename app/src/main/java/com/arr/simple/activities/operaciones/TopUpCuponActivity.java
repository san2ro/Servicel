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
import com.arr.simple.databinding.ActivityTopupCuponBinding;
import com.arr.simple.helpers.dialogs.ProgressDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import cu.arr.etecsa.api.portal.PortalRetrofit;
import cu.arr.etecsa.api.portal.models.OkResponse;
import cu.arr.etecsa.api.portal.request.topup.TopupCuponRequest;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TopUpCuponActivity extends BaseActivity {

    private ActivityTopupCuponBinding binding;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopupCuponBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        goBackPressed();

        dialog = new ProgressDialog(this);

        binding.cupon.addTextChangedListener(new ValidateCupon());

        binding.btnOk.setOnClickListener(this::recargar);

        // close
        binding.close.setOnClickListener(this::startToMainActivity);
    }

    private void recargar(View view) {
        String account = binding.account.getText().toString();
        String cupon = binding.cupon.getText().toString();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(cupon)) {
            topUpCupon(account, cupon);
        }
    }

    private void topUpCupon(String account, String cupon) {
        String auth = new SPHelper(this).getString("authentication", "");
        String passwordApp = new SPHelper(this).getString("passwordApp", "");
        TopupCuponRequest body = new TopupCuponRequest(account, cupon);

        Disposable disposable =
                PortalRetrofit.auth()
                        .topupCupon(body, passwordApp, auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> dialog.show())
                        .subscribe(this::handlerResponse, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerResponse(OkResponse response) {
        if (response.data.getResult().equals("OK")) {
            dialog.dismiss();
            new MaterialAlertDialogBuilder(this)
                    .setMessage("La cuenta se ha recargado")
                    .setPositiveButton("Aceptar", null)
                    .show();
        } else {
            dialog.dismiss();
            new MaterialAlertDialogBuilder(this)
                    .setMessage(response.data.getDetalle())
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    public class ValidateCupon implements TextWatcher {

        private boolean lock;

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (lock || s.toString().replaceAll("\\s", "").length() > 16) {
                return;
            }
            lock = true;
            int len = s.length();
            for (int i = len - 1; i >= 0; i--) {
                if (s.toString().charAt(i) == ' ') {
                    s.delete(i, i + 1);
                }
            }
            for (int i = 4; i < s.length(); i += 5) {
                s.insert(i, " ");
            }
            lock = false;
        }
    }
}
