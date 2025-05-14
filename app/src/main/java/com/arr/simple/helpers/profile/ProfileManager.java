package com.arr.simple.helpers.profile;

import android.content.Context;

import android.util.Log;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.route.FileRoute;

import cu.arr.etecsa.api.portal.models.User;
import cu.arr.etecsa.api.portal.models.cliente.Cliente;
import cu.arr.etecsa.api.portal.models.cliente.operaciones.ClienteId;
import cu.arr.etecsa.api.portal.models.cliente.operaciones.Operaciones;
import cu.arr.etecsa.api.portal.models.servicios.Services;
import cu.arr.etecsa.api.portal.models.servicios.correo.CorreoPerfil;
import cu.arr.etecsa.api.portal.models.servicios.mobile.Bono;
import cu.arr.etecsa.api.portal.models.servicios.mobile.MobilePerfil;
import cu.arr.etecsa.api.portal.models.servicios.mobile.Plan;
import cu.arr.etecsa.api.portal.models.servicios.navigation.NavigationPerfil;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProfileManager {

    private static final PublishSubject<Boolean> dataUpdateSubject = PublishSubject.create();
    private final Context context;
    private final boolean isFirstAccess;

    public ProfileManager(Context context) {
        this.context = context;
        this.isFirstAccess = new SPHelper(context).getBoolean("firstAccess", true);
    }

    public static Observable<Boolean> getDataUpdates() {
        return dataUpdateSubject;
    }

    public void processUserResponse(UsersResponse response) {
        if (response.getUser() instanceof User) {
            User user = (User) response.getUser();
            Map<String, Object> userData = processUserData(user);
            FileRoute.createJson(context, userData);
            dataUpdateSubject.onNext(true);
        }
    }

    private Map<String, Object> processUserData(User user) {
        Map<String, Object> root = FileRoute.getExistingData(context);

        root.put("Cliente", processClientData(user.getCliente()));
        root.put("Updated", processUpdated(user, user.getServicios()));
        processMobileServices(user.getServicios(), root);
        root.put("Navegación", processNavigation(user.getServicios(), root));
        root.put("Correo", processCorreo(user.getServicios(), root));

        return root;
    }

    private Map<String, Object> processUpdated(User user, Services services) {
        Map<String, Object> map = new LinkedHashMap<>();

        // Verificar nulidad de services y su perfil móvil
        if (services == null || services.getPerfilMobile() == null) {
            map.put("updated_plans", false);
            map.put("last_updated", user != null ? user.getUpdateDate() : null);
            return map;
        }

        // Procesar perfiles móviles
        boolean hasPlansOrBonuses = false;
        for (Map.Entry<String, MobilePerfil> entry : services.getPerfilMobile().entrySet()) {
            MobilePerfil perfil = entry.getValue();
            if (perfil != null
                    && perfil.listas != null
                    && (!perfil.listas.planes.isEmpty() || !perfil.listas.bonos.isEmpty())) {
                hasPlansOrBonuses = true;
                break;
            }
        }

        map.put("updated_plans", hasPlansOrBonuses);
        map.put("last_updated", user != null ? user.getUpdateDate() : null);
        return map;
    }

    private Map<String, Object> processClientData(Cliente client) {
        if (client == null) return new LinkedHashMap<>();

        Map<String, Object> clientMap = new LinkedHashMap<>();
        clientMap.put("nombre", client.getNombre());
        clientMap.put("movil", client.getTelefono());
        clientMap.put("email", client.getEmail());
        clientMap.put("notifi_mail", client.getNotificacionMail());
        clientMap.put("notifi_movil", client.getNotificacionMobile());
        clientMap.put("usuario", client.getUsuarioPortal());

        // obtener el client id
        for (Map.Entry<String, Operaciones> entry : client.getOperaciones().entrySet()) {
            Operaciones op = entry.getValue();
            if ("Operaciones realizadas en el portal".equals(entry.getKey())) {
                ClienteId clientId = op.getParametros().getClienteId();
                if (clientId != null) {
                    clientMap.put("clientId", clientId.getValor());
                }
            }
        }

        return clientMap;
    }

    private void processMobileServices(Services services, Map<String, Object> root) {
        if (services == null
                || services.getPerfilMobile().isEmpty()
                || services.getPerfilMobile() == null) return;

        for (Map.Entry<String, MobilePerfil> entry : services.getPerfilMobile().entrySet()) {
            String phoneNumber = entry.getKey();
            MobilePerfil profile = entry.getValue();

            Map<String, Object> mobileData = getOrCreateMobileData(root, phoneNumber);
            updateBasicProfileData(mobileData, profile);
            updateServicePlans(mobileData, profile);

            root.put(phoneNumber, mobileData);
        }
    }

    private Map<String, Object> getOrCreateMobileData(
            Map<String, Object> root, String phoneNumber) {
        return root.containsKey(phoneNumber)
                ? (Map<String, Object>) root.get(phoneNumber)
                : new LinkedHashMap<>();
    }

    private void updateBasicProfileData(Map<String, Object> mobileData, MobilePerfil profile) {
        mobileData.put("número", profile.numeroTelefono);
        mobileData.put("saldo", profile.saldoPrincipal);
        mobileData.put("expire", profile.fechaBloqueo);
        mobileData.put("adelanta", profile.adelantaSaldo);
    }

    private void updateServicePlans(Map<String, Object> mobileData, MobilePerfil profile) {
        Map<String, Object> plans = getExistingPlans(mobileData);
        Map<String, Object> bonos = getExistingBonos(mobileData);

        processPlans(profile.listas.planes, plans);
        processBonuses(profile.listas.bonos, bonos);

        if (!plans.isEmpty() || !bonos.isEmpty()) {
            mobileData.put("Planes", plans);
            mobileData.put("Bonos", bonos);
        }
    }

    private Map<String, Object> getExistingPlans(Map<String, Object> mobileData) {
        return mobileData.containsKey("Planes")
                ? (Map<String, Object>) mobileData.get("Planes")
                : new LinkedHashMap<>();
    }

    private Map<String, Object> getExistingBonos(Map<String, Object> mobileData) {
        return mobileData.containsKey("Bonos")
                ? (Map<String, Object>) mobileData.get("Bonos")
                : new LinkedHashMap<>();
    }

    private void processPlans(Map<String, Plan> plans, Map<String, Object> plansData) {
        for (Map.Entry<String, Plan> entry : plans.entrySet()) {
            Plan plan = entry.getValue();
            if (isValidPlanType(plan.tipo)) {
                updatePlanData(plansData, plan);
            }
        }
    }

    private void processBonuses(Map<String, Bono> bonuses, Map<String, Object> plansData) {
        for (Map.Entry<String, Bono> entry : bonuses.entrySet()) {
            Bono bonus = entry.getValue();
            if (isValidBonusType(bonus.tipo)) {
                updateBonusData(plansData, bonus);
            }
        }
    }

    private boolean isValidPlanType(String type) {
        return "DATOS".equals(type)
                || "DATOS LTE".equals(type)
                || "SMS".equals(type)
                || "MINUTOS".equals(type);
    }

    private boolean isValidBonusType(String type) {
        return "BONO DATOS".equals(type) || "DATOS NACIONALES".equals(type);
    }

    private void updatePlanData(Map<String, Object> plansData, Plan plan) {
        Map<String, Object> planData = getOrCreatePlanData(plansData, plan.tipo);

        updatePlanFields(planData, plan);
        updateInitialPlanValue(planData, plan.datos);

        plansData.put(plan.tipo, planData);
    }

    private void updateBonusData(Map<String, Object> plansData, Bono bonus) {
        Map<String, Object> bonusData = getOrCreatePlanData(plansData, bonus.tipo);

        updatePlanFields(bonusData, bonus);
        updateInitialPlanValue(bonusData, bonus.datos);

        plansData.put(bonus.tipo, bonusData);
    }

    private Map<String, Object> getOrCreatePlanData(Map<String, Object> plansData, String type) {
        return plansData.containsKey(type)
                ? (Map<String, Object>) plansData.get(type)
                : new LinkedHashMap<>();
    }

    private void updatePlanFields(Map<String, Object> planData, Plan plan) {
        if (!plan.datos.isEmpty()) planData.put("dispone", plan.datos);
        if (!plan.vence.isEmpty()) planData.put("vence", plan.vence);
    }

    private void updatePlanFields(Map<String, Object> planData, Bono bonus) {
        if (!bonus.datos.isEmpty()) planData.put("dispone", bonus.datos);
        if (!bonus.vence.isEmpty()) planData.put("vence", bonus.vence);
    }

    private void updateInitialPlanValue(Map<String, Object> planData, String currentValue) {
        String previousValue = planData.getOrDefault("plan", currentValue).toString();
        if (isFirstAccess || shouldUpdatePlanValue(currentValue, previousValue)) {
            planData.put("plan", currentValue);
        } else {
            planData.put("plan", previousValue);
        }
    }

    private boolean shouldUpdatePlanValue(String currentValue, String previousValue) {
        try {
            double current = ConvertValues.convertValues(currentValue);
            double previous = ConvertValues.convertValues(previousValue);
            return current > previous;
        } catch (Exception e) {
            return false;
        }
    }

    // Navegación
    private Map<String, Object> processNavigation(Services services, Map<String, Object> root) {
        // Verificación inicial de objetos nulos
        if (services == null || services.getPerfilNavegation() == null) {
            return getOrCreateNavigationData(root, "Navegación");
        }

        Map<String, Object> navegation = getOrCreateNavigationData(root, "Navegación");
        Map<String, NavigationPerfil> navigationProfiles = services.getPerfilNavegation();
        for (Map.Entry<String, NavigationPerfil> entry : navigationProfiles.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                continue; // Saltar entradas nulas
            }

            String account = entry.getKey();
            NavigationPerfil profile = entry.getValue();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("cuenta", profile.cuentaAcceso);
            map.put("estado", profile.estado);
            map.put("venta", profile.fechaVenta);
            map.put("bloqueo", profile.fechaBloqueo);
            map.put("eliminación", profile.fechaEliminacion);
            map.put("saldo", profile.saldo);
            map.put("tipo", profile.tipoAcceso);
            map.put("bonificación", profile.bonificacionDisfrutar);
            map.put("horas_de_bonificación", profile.horasBonificacion);
            map.put("id", profile.id);

            navegation.put(account, map);
        }

        return navegation;
    }

    private Map<String, Object> getOrCreateNavigationData(
            Map<String, Object> root, String account) {
        return root.containsKey(account)
                ? (Map<String, Object>) root.get(account)
                : new LinkedHashMap<>();
    }

    // correo
    private Map<String, Object> processCorreo(Services services, Map<String, Object> root) {
        // Verificación inicial de objetos nulos
        if (services == null || services.getPerfilCorreo() == null) {
            return getOrCreateNavigationData(root, "Correo");
        }

        Map<String, Object> correo = getOrCreateNavigationData(root, "Correo");
        Map<String, CorreoPerfil> correoPerfil = services.getPerfilCorreo();
        for (Map.Entry<String, CorreoPerfil> entry : correoPerfil.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                continue; // Saltar entradas nulas
            }

            String account = entry.getKey();
            CorreoPerfil profile = entry.getValue();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("cuenta", profile.cuenta);
            map.put("venta", profile.fechaVenta);
            map.put("id", profile.id);

            correo.put(account, map);
        }

        return correo;
    }

    private Map<String, Object> getOrCreateCorreoData(Map<String, Object> root, String account) {
        return root.containsKey(account)
                ? (Map<String, Object>) root.get(account)
                : new LinkedHashMap<>();
    }
}
