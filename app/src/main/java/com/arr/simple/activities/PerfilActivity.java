package com.arr.simple.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.BaseActivity;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import com.arr.simple.activities.ui.SettingsActivity;
import com.arr.simple.databinding.ActivityPerfilBinding;
import com.arr.simple.databinding.ItemsPerfilBinding;
import com.arr.simple.helpers.data.ProcessData;
import com.arr.simple.helpers.data.StorageRoute;
import com.arr.simple.helpers.dialogs.ProgressDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.arr.simple.helpers.profile.route.FileRoute;
import com.arr.simple.helpers.qr.QRGen;
import cu.arr.etecsa.api.portal.PortalRetrofit;
import cu.arr.etecsa.api.portal.models.OkResponse;
import cu.arr.etecsa.api.portal.request.logout.LogoutRequest;
import cu.arr.etecsa.api.portal.utils.ValidateToken;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PerfilActivity extends BaseActivity {

    private ActivityPerfilBinding binding;

    private PerfilAdapter adapter;
    private List<Perfil> list = new ArrayList<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressDialog dialog;
    private SPHelper sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        goBackPressed();

        dialog = new ProgressDialog(this);
        // iniciar la clase de preferences
        sp = new SPHelper(this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PerfilAdapter(list);
        binding.recyclerView.setAdapter(adapter);
        getListUserProfile();

        // letra del perfil
        String name = ProcessProfile.getNameUser(this);
        String letter = (name != null) ? String.valueOf(name.charAt(0)).toUpperCase() : "S";
        binding.letterName.setText(letter);

        binding.logout.setOnClickListener(this::logOut);
    }

    private void getListUserProfile() {
        String name = ProcessProfile.getNameUser(this);
        String number = ProcessProfile.getPhone(this);
        String email = ProcessProfile.getEmail(this);
        String notifSMS = ProcessProfile.getNotificationSms(this);
        String notifMail = ProcessProfile.getNotificationEmail(this);

        list.add(new Perfil(R.drawable.ic_account_unfill_24dp, "Nombre", name));
        list.add(new Perfil(R.drawable.ic_phone_call_24dp, "Número", number));
        list.add(new Perfil(R.drawable.ic_email_24dp, "Correo electrónico", email));
        list.add(new Perfil(0, "Recibir notificaciones vía", null));
        list.add(new Perfil(R.drawable.ic_sms_notification, "Mensajes", notifSMS));
        list.add(new Perfil(R.drawable.ic_email_notification, "Correo Electrónico", notifMail));
    }

    private void logOut(View view) {
        String username = sp.getString("username", "");
        String passwordApp = sp.getString("passwordApp", "");
        String auth = sp.getString("authentication", "");
        if (!TextUtils.isEmpty(username)) {
            boolean isTokenValid = ValidateToken.isValidToken(auth.replace("Bearer ", ""));
            if (isTokenValid) {
                logoutUser(username, passwordApp, auth);
            } else {
                showMessage("Su token ah expirado");
            }
        }
    }

    private void logoutUser(String username, String passwordApp, String auth) {
        LogoutRequest body = new LogoutRequest(username);
        Disposable disposable =
                PortalRetrofit.auth()
                        .logOut(body, passwordApp, auth)
                        .timeout(20, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disp -> dialog.show())
                        .subscribe(this::handlerLogout, this::handlerError);
        compositeDisposable.add(disposable);
    }

    private void handlerLogout(OkResponse response) {
        if (response.data.getResult().equals("OK")) {
            dialog.dismiss();
            new SPHelper(this).removeKey("authentication");
            new SPHelper(this).removeKey("firstAccess");
            new SPHelper(this).removeKey("phone");
            FileRoute.deleteFileData(this);
            startActivity(new Intent(this, MainActivity.class));
            finish();
            showMessage("¡Sesión cerrada!");
        }
    }

    private void handlerError(Throwable e) {
        if (e instanceof IOException err) {
            dialog.dismiss();
            showMessage("Error de red, intentelo de nuevo");
        } else {
            dialog.dismiss();
            showMessage("Error " + e.getMessage());
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, SettingsActivity.class));
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
                                        new Intent(PerfilActivity.this, SettingsActivity.class));
                                finish();
                            }
                        });
    }

    public static class PerfilAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Perfil> mList;

        public PerfilAdapter(List<Perfil> list) {
            this.mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemsPerfilBinding binding =
                    ItemsPerfilBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new PerfilView(binding);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Perfil model = mList.get(position);
            if (holder instanceof PerfilView view) {
                // title
                view.binding.title.setVisibility(model.title == null ? View.GONE : View.VISIBLE);
                view.binding.title.setText(model.title);

                // subtitle
                view.binding.subtitle.setVisibility(
                        model.subtitle == null ? View.GONE : View.VISIBLE);
                view.binding.subtitle.setText(model.subtitle);

                // icon
                view.binding.icon.setVisibility(model.icon == 0 ? View.GONE : View.VISIBLE);
                view.binding.icon.setImageResource(model.icon);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class PerfilView extends RecyclerView.ViewHolder {

            private ItemsPerfilBinding binding;

            public PerfilView(ItemsPerfilBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public static class Perfil {

        public int icon;
        public String title;
        public String subtitle;

        public Perfil(int icon, String title, String subtitle) {
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
        }
    }
}
