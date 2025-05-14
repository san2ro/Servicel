package com.arr.simple.fragment.operaciones;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.viewpager2.widget.ViewPager2;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.arr.simple.adapters.TabAdapter;
import com.arr.simple.databinding.FragmentOperacionesBinding;
import com.arr.simple.fragment.operaciones.accounts.CuentasNautaFragment;
import com.arr.simple.fragment.operaciones.correo.CorreoFragment;
import com.arr.simple.fragment.operaciones.hogar.NautaHogarFragment;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.update.UpdateData;
import com.google.android.material.tabs.TabLayoutMediator;
import cu.arr.etecsa.api.portal.utils.ValidateToken;

public class OpFragment extends Fragment {

    private FragmentOperacionesBinding binding;
    private UpdateData update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentOperacionesBinding.inflate(inflater, container, false);

        update = new UpdateData(requireContext());

        TabAdapter adapter = new TabAdapter(getChildFragmentManager(), getLifecycle());
        adapter.addFragment(new CuentasNautaFragment());
        adapter.addFragment(new CorreoFragment());

        // adapter.addFragment(new NautaHogarFragment());

        binding.viewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewpager.setAdapter(adapter);

        new TabLayoutMediator(
                        binding.tab,
                        binding.viewpager,
                        (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText("Cuenta de navegación");
                                    break;
                                case 1:
                                    tab.setText("Correo nauta");
                                    break;
                                case 2:
                                    tab.setText("Telefonía fija");
                                    break;
                            }
                        })
                .attach();

        binding.viewpager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        switch (position) {
                            case 0:
                                binding.title.setText("Navegación");
                                break;
                            case 1:
                                binding.title.setText("Correo");
                                break;
                            case 2:
                                binding.title.setText("Telefonía");
                                break;
                        }
                    }
                });

        String auth = new SPHelper(requireContext()).getString("authentication", "");
        boolean isTokenValid = ValidateToken.isValidToken(auth.replace("Bearer ", ""));
        Log.e("TOKEN", "status token " + isTokenValid);
        if (!isTokenValid) {
            performMainTask();
        }

        return binding.getRoot();
    }

    private void performMainTask() {
        update.updateDataPortal(
                new UpdateData.UpdateDataCallback() {
                    @Override
                    public void onProgress(UpdateData.SyncStatus status, String message) {
                        // Actualizar UI con el progreso
                        Log.d("SyncProgress", status + ": " + message);
                    }

                    @Override
                    public void onSuccess(UpdateData.SyncResult result) {}

                    @Override
                    public void onError(UpdateData.SyncResult result) {
                        // Mostrar error detallado
                        Log.e("SyncError", "Error durante sincronización", result.error);
                    }

                    @Override
                    public void onComplete(UpdateData.SyncResult result) {}
                });
    }
}
