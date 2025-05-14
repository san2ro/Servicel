package com.arr.simple.helpers.data;

import android.content.Context;

import android.text.TextUtils;
import android.util.Log;
import com.arr.simple.helpers.data.models.Response;
import com.arr.simple.helpers.data.models.Telefono;
import com.arr.simple.helpers.data.models.Values;
import com.arr.simple.helpers.preferences.SPHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cu.arr.etecsa.api.portal.models.User;
import cu.arr.etecsa.api.portal.models.cliente.Cliente;
import cu.arr.etecsa.api.portal.models.servicios.Services;
import cu.arr.etecsa.api.portal.models.servicios.mobile.Bono;
import cu.arr.etecsa.api.portal.models.servicios.mobile.MobilePerfil;
import cu.arr.etecsa.api.portal.models.servicios.mobile.Plan;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginData {

    private static final PublishSubject<Boolean> dataUpdateSubject = PublishSubject.create();
    private final Context mContext;

    private boolean firstAccess;

    public LoginData(Context context) {
        this.mContext = context;
        this.firstAccess = new SPHelper(context).getBoolean("firstLogin", false);
    }

    public static Observable<Boolean> getDataUpdates() {
        return dataUpdateSubject;
    }

    public void insertLoginJson(UsersResponse response) {
        Object jsonObject = response.getUser();
        if (jsonObject instanceof User) {
            User user = (User) jsonObject;
            Map<String, Object> root = buildUserDataMap(user);

            StorageRoute.saveJson(mContext, root);
            dataUpdateSubject.onNext(true);
        }
    }

    private Map<String, Object> buildUserDataMap(User user) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("Cliente", clientToMap(user.getCliente()));

        Services servicio = user.getServicios();

        // servicios moviles
        for (Map.Entry<String, MobilePerfil> entry : servicio.getPerfilMobile().entrySet()) {
            MobilePerfil perfil = entry.getValue();
            root.put("updated_plans", true);
            if (perfil.listas.planes.isEmpty() || perfil.listas.bonos.isEmpty()) {
                root.put("updated_plans", false);
            }
            root.put("status_access", firstAccess);
            root.put(entry.getKey(), mobileToMap(perfil, entry.getKey()));
        }

        return root;
    }

    private Map<String, Object> clientToMap(Cliente client) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (client == null) return map;

        map.put("nombre", client.getNombre());
        map.put("movil", client.getTelefono());
        map.put("email", client.getEmail());
        map.put("notifi_mail", client.getNotificacionMail());
        map.put("notifi_movil", client.getNotificacionMobile());
        map.put("usuario", client.getUsuarioPortal());

        return map;
    }

    private Map<String, Object> mobileToMap(MobilePerfil perfil, String phone) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (perfil == null) return map;

        map.put("número", perfil.numeroTelefono);
        map.put("saldo", perfil.saldoPrincipal);

        /**
         * map se llena solo si bonos y planes no está vacio de lo contrario se insertan los datps
         * existentes si existen
         */
        if (!perfil.listas.bonos.isEmpty() || !perfil.listas.planes.isEmpty()) {
            map.put("Planes", mobilePlan(perfil, phone));
            map.put("Bonos", mobileBonos(perfil, phone));
        } else {
            map.put("Planes", getPlanValue(phone));
            map.put("Bonos", getBonosValue(phone));
        }
        return map;
    }

    private Map<String, Object> mobilePlan(MobilePerfil perfil, String phone) {
        Map<String, Object> root = new LinkedHashMap<>();
        for (Map.Entry<String, Plan> entry : perfil.listas.planes.entrySet()) {
            Plan plan = entry.getValue();

            if (isValidPlanType(plan.tipo)) {
                Map<String, Object> planMap = buildPlanMap(plan, phone);
                root.put(plan.tipo, planMap);
            }
        }
        return root;
    }

    private boolean isValidPlanType(String tipo) {
        return "DATOS".equals(tipo)
                || "DATOS LTE".equals(tipo)
                || "SMS".equals(tipo)
                || "MINUTOS".equals(tipo);
    }

    private Map<String, Object> buildPlanMap(Plan plan, String phone) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (!plan.datos.isEmpty()) {
            map.put("dispone", plan.datos);
        }
        if (!plan.vence.isEmpty()) {
            map.put("vence", plan.vence);
        }

        // si el boolean es false se agrega plan.datos si no, se agrega getPlanValue
        map.put("plan", firstAccess ? plan.datos : getPlan(phone));

        return map;
    }

    private Map<String, Object> mobileBonos(MobilePerfil perfil, String phone) {
        Map<String, Object> root = new LinkedHashMap<>();
        for (Map.Entry<String, Bono> entry : perfil.listas.bonos.entrySet()) {
            Bono bono = entry.getValue();

            if (isValidBonoType(bono.tipo)) {
                Map<String, Object> bonoMap = buildBonoMap(bono, phone);
                root.put(bono.tipo, bonoMap);
            }
        }
        return root;
    }

    private boolean isValidBonoType(String tipo) {
        return "BONO DATOS".equals(tipo)
                || "DATOS NACIONALES".equals(tipo)
                || "BONO MINUTOS".equals(tipo)
                || "BONO SMS".equals(tipo);
    }

    private Map<String, Object> buildBonoMap(Bono bono, String phone) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (!bono.datos.isEmpty()) {
            map.put("dispone", bono.datos);
        }
        if (!bono.vence.isEmpty()) {
            map.put("vence", bono.vence);
        }

        // si el boolean es false se agrega plan.datos si no, se agrega getPlanValue
        map.put("plan", firstAccess ? bono.datos : getBono(phone));

        return map;
    }

    private String getPlan(String phone) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();

        try (FileReader reader = new FileReader(StorageRoute.getDirectory(mContext))) {
            Response resp = gson.fromJson(reader, Response.class);

            if (resp.telefonos != null && resp.telefonos.containsKey(phone)) {
                Telefono telefono = resp.telefonos.get(phone);

                if (telefono.Planes != null) {
                    for (Map.Entry<String, Values> entry : telefono.Planes.entrySet()) {
                        return entry.getValue().plan;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "0 B";
    }

    private String getBono(String phone) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();

        try (FileReader reader = new FileReader(StorageRoute.getDirectory(mContext))) {
            Response resp = gson.fromJson(reader, Response.class);

            if (resp.telefonos != null && resp.telefonos.containsKey(phone)) {
                Telefono telefono = resp.telefonos.get(phone);

                if (telefono.Bonos != null) {
                    for (Map.Entry<String, Values> entry : telefono.Bonos.entrySet()) {
                        return entry.getValue().plan;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "0 B";
    }

    private Map<String, Values> getPlanValue(String phone) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();

        try (FileReader reader = new FileReader(StorageRoute.getDirectory(mContext))) {
            Response resp = gson.fromJson(reader, Response.class);

            if (resp.telefonos != null && resp.telefonos.containsKey(phone)) {
                Telefono telefono = resp.telefonos.get(phone);

                if (telefono.Planes != null && !telefono.Planes.isEmpty()) {
                    return telefono.Planes;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, Values> getBonosValue(String phone) {
        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Response.class, new ResponseDeserializer())
                        .create();

        try (FileReader reader = new FileReader(StorageRoute.getDirectory(mContext))) {
            Response resp = gson.fromJson(reader, Response.class);

            if (resp.telefonos != null && resp.telefonos.containsKey(phone)) {
                Telefono telefono = resp.telefonos.get(phone);

                if (telefono.Bonos != null && !telefono.Bonos.isEmpty()) {
                    return telefono.Bonos;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
