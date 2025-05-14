package com.arr.simple.helpers.update;

import android.content.Context;
import android.util.Pair;

import com.arr.simple.helpers.preferences.SPHelper;

import cu.arr.etecsa.api.portal.PortalRetrofit;
import cu.arr.etecsa.api.portal.models.User;
import cu.arr.etecsa.api.portal.models.login.LoginResponse;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;
import cu.arr.etecsa.api.portal.request.login.LoginRequest;
import cu.arr.etecsa.api.portal.request.users.UsersRequest;
import cu.arr.etecsa.api.portal.utils.PasswordApp;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Date;

public class UpdateData {

    private final Context mContext;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SPHelper sp;
    private SyncStatus currentStatus = SyncStatus.IDLE;
    private SyncResult lastSyncResult;
    private Date lastSyncTime;

    // Estadísticas de sincronización
    private int loginAttempts = 0;
    private int dataFetchAttempts = 0;
    private int totalErrors = 0;
    private long startTime;
    private long endTime;

    public enum SyncStatus {
        IDLE,
        LOGIN_IN_PROGRESS,
        DATA_FETCH_IN_PROGRESS,
        COMPLETED,
        ERROR
    }

    public static class SyncResult {
        public boolean success;
        public String token;
        public UsersResponse response;
        public Throwable error;
        public String errorMessage;
        public long executionTime;
        public int loginAttempts;
        public int dataFetchAttempts;
        public Date timestamp;
    }

    public interface UpdateDataCallback {
        void onProgress(SyncStatus status, String message);

        void onSuccess(SyncResult result);

        void onError(SyncResult result);

        void onComplete(SyncResult result);
    }

    public UpdateData(Context context) {
        this.mContext = context;
        this.sp = new SPHelper(context);
    }

    public void updateDataPortal(UpdateDataCallback callback) {
        startTime = System.currentTimeMillis();
        currentStatus = SyncStatus.LOGIN_IN_PROGRESS;
        loginAttempts = 0;
        dataFetchAttempts = 0;
        totalErrors = 0;

        // notifyProgress(callback, "Iniciando proceso de sincronización...");

        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        String captcha = sp.getString("captcha", "");
        String idRequest = sp.getString("idRequest", "");
        String passwordApp = PasswordApp.getPasswordApp();

        LoginRequest body = new LoginRequest(username, password, idRequest, captcha);

        loginAttempts++;
        notifyProgress(callback, "Autenticando con el servidor...");

        Disposable disposable =
                PortalRetrofit.auth()
                        .login(body, passwordApp)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> handleLoginResponse(response, passwordApp, callback),
                                error -> handleError(error, "Error en autenticación", callback));

        compositeDisposable.add(disposable);
    }

    private void handleLoginResponse(
            LoginResponse response, String passwordApp, UpdateDataCallback callback) {
        if ("ok".equalsIgnoreCase(response.getResultado())) {
            Object userObject = response.getUser();
            if (userObject instanceof User) {
                User user = (User) userObject;
                if ("false".equals(user.getUpdateServices())) {
                    String email = user.getCliente().getUsuarioPortal();
                    String lastUpdate = user.getUpdateDate();
                    String token = response.getToken();

                    currentStatus = SyncStatus.DATA_FETCH_IN_PROGRESS;
                    // notifyProgress(callback, "Autenticación exitosa, obteniendo datos...");
                    fetchUsers(email, lastUpdate, passwordApp, token, callback);
                }
            }
        } else {
            handleError(
                    new Exception("Respuesta del servidor no válida"),
                    "Resultado: " + response.getResultado(),
                    callback);
        }
    }

    private void fetchUsers(
            String email,
            String lastUpdate,
            String passwordApp,
            String auth,
            UpdateDataCallback callback) {
        dataFetchAttempts++;
        /*
                notifyProgress(
                        callback, "Solicitando datos actualizados... (Intento " + dataFetchAttempts + ")");
        */
        UsersRequest body = new UsersRequest(email, lastUpdate);
        sp.setString("authentication", "Bearer " + auth);
        sp.setString("passwordApp", passwordApp);

        Disposable disposable =
                PortalRetrofit.auth()
                        .authUser(body, passwordApp, "Bearer " + auth)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                resp -> handleUsersResponse(resp, passwordApp, auth, callback),
                                error -> handleError(error, "Error al obtener datos", callback));

        compositeDisposable.add(disposable);
    }

    private void handleUsersResponse(
            UsersResponse response, String passwordApp, String token, UpdateDataCallback callback) {
        if ("ok".equalsIgnoreCase(response.getResultado())) {
            Object userObject = response.getUser();
            if (userObject instanceof User) {
                User user = (User) userObject;
                if ("false".equals(user.getCompleted())) {
                    // Lógica de reintento
                    if (!user.getServicios().getPerfilMobile().isEmpty()) {
                        if (dataFetchAttempts >= 10
                                || (System.currentTimeMillis() - startTime) > 30000) {
                            handleSuccess(response, token, callback);
                        } else {
                            String username = user.getCliente().getUsuarioPortal();
                            String newLastUpdate = user.getUpdateDate();
                            fetchUsers(username, newLastUpdate, passwordApp, token, callback);
                        }
                    } else {
                        String username = user.getCliente().getUsuarioPortal();
                        String newLastUpdate = user.getUpdateDate();
                        fetchUsers(username, newLastUpdate, passwordApp, token, callback);
                        // notifyProgress(callback, "No se actualizó los servicios móviles");
                    }
                } else {
                    handleSuccess(response, token, callback);
                }
            }
        } else {
            handleError(
                    new Exception("Respuesta de datos no válida"),
                    "Resultado: " + response.getResultado(),
                    callback);
        }
    }

    private void handleSuccess(UsersResponse response, String token, UpdateDataCallback callback) {
        endTime = System.currentTimeMillis();
        currentStatus = SyncStatus.COMPLETED;

        SyncResult result = new SyncResult();
        result.success = true;
        result.token = token;
        result.response = response;
        result.executionTime = endTime - startTime;
        result.loginAttempts = loginAttempts;
        result.dataFetchAttempts = dataFetchAttempts;
        result.timestamp = new Date();

        lastSyncResult = result;
        lastSyncTime = new Date();

        //  notifyProgress(callback, "Sincronización completada con éxito");
        if (callback != null) {
            callback.onSuccess(result);
            callback.onComplete(result);
        }
    }

    private void handleError(Throwable error, String message, UpdateDataCallback callback) {
        endTime = System.currentTimeMillis();
        currentStatus = SyncStatus.ERROR;
        totalErrors++;

        SyncResult result = new SyncResult();
        result.success = false;
        result.error = error;
        result.errorMessage = message;
        result.executionTime = endTime - startTime;
        result.loginAttempts = loginAttempts;
        result.dataFetchAttempts = dataFetchAttempts;
        result.timestamp = new Date();

        lastSyncResult = result;

        notifyProgress(callback, "Error: " + message);
        if (callback != null) {
            callback.onError(result);
            callback.onComplete(result);
        }
    }

    private void notifyProgress(UpdateDataCallback callback, String message) {
        if (callback != null) {
            callback.onProgress(currentStatus, message);
        }
    }

    public void dispose() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        currentStatus = SyncStatus.IDLE;
    }

    // Métodos para obtener información del estado
    public SyncStatus getCurrentStatus() {
        return currentStatus;
    }

    public SyncResult getLastSyncResult() {
        return lastSyncResult;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public int getTotalErrors() {
        return totalErrors;
    }
}
