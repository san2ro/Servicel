package com.arr.simple.fragment.balances;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.arr.simple.R;
import com.arr.simple.activities.LoginActivity;
import com.arr.simple.adapters.BalanceAdapter;
import com.arr.simple.databinding.FragmentBalanceBinding;
import com.arr.simple.helpers.notification.BalanceNotification;
import com.arr.simple.helpers.portal.RetrofitUrlChecker;
import com.arr.simple.helpers.portal.UrlCheckService;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.arr.simple.helpers.profile.ProfileManager;
import com.arr.simple.helpers.ui.NumberList;
import com.arr.simple.helpers.update.UpdateData;
import com.arr.simple.models.ListItem;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BalanceFragment extends Fragment {

    private FragmentBalanceBinding binding;
    private SPHelper sp;

    private boolean updatePlans;
    private List<String> listNumbers = new ArrayList<>();

    private UpdateData update;

    private boolean firstAccess;

    private RetrofitUrlChecker urlCheck;

    private BalanceViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BalanceViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentBalanceBinding.inflate(inflater, container, false);
        update = new UpdateData(requireContext());

        sp = new SPHelper(requireContext());
        firstAccess = sp.getBoolean("firstAccess", true);
        binding.notLoginView.setVisibility(!firstAccess ? View.GONE : View.VISIBLE);
        binding.swipeRefresh.setVisibility(!firstAccess ? View.VISIBLE : View.GONE);

        // acceder a loginActivity
        binding.login.setOnClickListener(this::startToLoginActivity);

        // se ha actualizado los planes
        updatePlans = ProcessProfile.updatedPlans(requireContext());

        // sincronizar los balances
        binding.swipeRefresh.setOnRefreshListener(this::updatedBalances);

        // mostrar datos en la ui
        updatedUI();
        Disposable disposable =
                ProfileManager.getDataUpdates()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                updated -> {
                                    if (updated && isAdded()) {
                                        updatedUI();
                                    }
                                });

        // mostrar listas de números
        binding.phoneNumber.setOnClickListener(this::viewPopupListNumbers);

        // check url
        viewModel
                .getUrlStatus()
                .observe(
                        getViewLifecycleOwner(),
                        isAvailable -> {
                            if (isAvailable == null) return;

                            if (isAvailable) {
                                binding.textStatusPortal.setVisibility(View.GONE);
                            } else {
                                binding.textStatusPortal.setVisibility(View.VISIBLE);
                                binding.textStatusPortal.setText(
                                        "Portal Nauta no disponible por el momento");
                            }
                        });

        viewModel.startChecking("https://www.nauta.cu/");

        // mostrar notificacion de balances
        boolean isShow = sp.getBoolean("usage_notif", false);
        if (isShow) {
            new BalanceNotification(requireContext()).show();
        }
        
        return binding.getRoot();
    }

    private void viewPopupListNumbers(View view) {
        if (listNumbers.size() > 1) {
            new NumberList(requireActivity())
                    .showMenu(
                            view,
                            listNumbers,
                            phone -> {
                                showUI(phone);
                                sp.setString("phone", phone);
                            });
        }
    }

    private void updatedUI() {
        // obtener lista de numeros
        listNumbers = ProcessProfile.getPhoneNumbers(requireContext());
        if (!listNumbers.isEmpty()) {
            String firstNumber = sp.getString("phone", listNumbers.get(0));
            showUI(firstNumber);
        }
        boolean updatedServices = ProcessProfile.isServicesUpdated(requireContext());
        binding.recyclerview.setVisibility(updatedServices ? View.GONE : View.VISIBLE);
        binding.notPlansView.setVisibility(updatedServices ? View.VISIBLE : View.GONE);
        binding.phoneNumber.setVisibility(updatedServices ? View.GONE : View.VISIBLE);
        binding.messageError.setText(
                updatedServices ? getString(R.string.message_error_services) : "");
    }

    private void showUI(String phone) {
        // Mostrar número de teléfono
        if (phone != null && !phone.isEmpty()) {
            binding.phoneNumber.setVisibility(View.VISIBLE);
            binding.phoneNumber.setText(phone.substring(2));
            sp.setString("phone", phone);
        }

        // Mostrar saldo
        binding.saldoMovil.setText(ProcessProfile.getSaldoForNumber(requireContext(), phone));

        // Configurar RecyclerView
        setupRecyclerView(phone);
    }

    private void setupRecyclerView(String phone) {
        // Obtener y filtrar datos
        List<ListItem> items = ProcessProfile.getDataForRecyclerView(requireContext(), phone);
        List<ListItem> filteredItems = new ArrayList<>();

        for (ListItem item : items) {
            if (item.getType() != ListItem.TYPE_ITEM
                    || (item.getValue() != null && !item.getValue().isEmpty())) {
                filteredItems.add(item);
            }
        }

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        BalanceAdapter adapter = new BalanceAdapter(filteredItems, layoutManager);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setAdapter(adapter);
    }

    private List<ListItem> filterItems(List<ListItem> items) {
        List<ListItem> filtered = new ArrayList<>();
        for (ListItem item : items) {
            if (item.getType() != ListItem.TYPE_ITEM
                    || (item.getValue() != null && !item.getValue().isEmpty())) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private void updatedBalances() {
        update.updateDataPortal(
                new UpdateData.UpdateDataCallback() {
                    @Override
                    public void onProgress(UpdateData.SyncStatus status, String message) {
                        // Actualizar UI con el progreso
                        Log.d("SyncProgress", status + ": " + message);
                        showMessage(message);
                        if (isAdded()) { // Verificar antes de actualizar UI
                            binding.loading.setVisibility(View.VISIBLE);
                            binding.recyclerview.setVisibility(View.GONE);
                            binding.notPlansView.setVisibility(View.GONE);
                            binding.swipeRefresh.setProgressViewOffset(false, -10000, -10000);
                        }
                    }

                    @Override
                    public void onSuccess(UpdateData.SyncResult result) {
                        new ProfileManager(requireContext()).processUserResponse(result.response);
                    }

                    @Override
                    public void onError(UpdateData.SyncResult result) {
                        // Mostrar error detallado
                        showMessage("Error: " + result.errorMessage);
                        Log.e("SyncError", "Error durante sincronización", result.error);
                    }

                    @Override
                    public void onComplete(UpdateData.SyncResult result) {
                        // Operaciones finales (ocurre después de onSuccess/onError)
                        ProcessProfile.clearCache();
                        binding.loading.setVisibility(View.GONE);
                        binding.recyclerview.setVisibility(View.VISIBLE);
                        binding.swipeRefresh.setRefreshing(false);
                        Log.d("SyncComplete", "Tiempo ejecución: " + result.executionTime + "ms");
                    }
                });
    }

    private void showMessage(String message) {
        if (isAdded() && binding != null && binding.getRoot() != null) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void startToLoginActivity(View view) {
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        viewModel.stopChecking();
        super.onDestroyView();
        binding = null;
        update.dispose();
    }
}
