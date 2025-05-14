package com.arr.simple.fragment.datos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.Fragment;
import com.arr.simple.adapters.TabAdapter;
import com.arr.simple.databinding.FragmentDatosBinding;
import com.arr.simple.fragment.datos.paquetes.BolsasFragment;
import com.arr.simple.fragment.datos.paquetes.CombosFragment;
import com.arr.simple.fragment.datos.paquetes.LteFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class DatosFragment extends Fragment {

    private FragmentDatosBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentDatosBinding.inflate(inflater, container, false);

        TabAdapter adapter = new TabAdapter(getChildFragmentManager(), getLifecycle());
        adapter.addFragment(new CombosFragment());
        adapter.addFragment(new LteFragment());
        adapter.addFragment(new BolsasFragment());

        binding.viewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewpager.setAdapter(adapter);
        TabLayoutMediator mediator =
                new TabLayoutMediator(
                        binding.tab,
                        binding.viewpager,
                        (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText("Planes");
                                    break;
                                case 1:
                                    tab.setText("Paquetes");
                                    break;
                                case 2:
                                    tab.setText("Bolsas");
                                    break;
                                    /*
                                    case 3:
                                        tab.setText("Minutos");
                                        tab.setIcon(R.drawable.ic_minutos_24dp);
                                        break;
                                    case 4:
                                        tab.setText("Mensajes");
                                        tab.setIcon(R.drawable.ic_mensajes_24dp);
                                        break;
                                    case 5:
                                        tab.setText("Plan Amigo");
                                        break;*/
                            }
                        });
        mediator.attach();

        if (!isPermissionCallGrated()) {
            ActivityCompat.requestPermissions(
                    requireActivity(), new String[] {Manifest.permission.CALL_PHONE}, 80);
        }

        return binding.getRoot();
    }

    private boolean isPermissionCallGrated() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED;
    }
}
