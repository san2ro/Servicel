package com.arr.simple.fragment.datos.paquetes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.arr.simple.R;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.simple.adapters.DatosAdapter;
import com.arr.simple.databinding.FragmentPaquetesBinding;
import com.arr.simple.helpers.dialogs.BuyDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.ussd.USSDUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;

public class BolsasFragment extends Fragment {

    private FragmentPaquetesBinding binding;

    private List<DatosAdapter.Items> list = new ArrayList<>();
    private DatosAdapter adapter;

    private SPHelper sp;
    private ActivityResultLauncher<String> callLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentPaquetesBinding.inflate(inflater, container, false);

        // sharedPreferences
        sp = new SPHelper(requireContext());

        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DatosAdapter(list, position -> onClickItem(position));
        binding.recycler.setAdapter(adapter);
        list.clear();
        showItems(
                R.drawable.ic_bolsa_24dp,
                "200 MB",
                getContext().getString(R.string.subtitle_diaria));
        showItems(
                R.drawable.ic_bolsa_24dp,
                "600 MB",
                getContext().getString(R.string.subtitle_mensajeria));

        return binding.getRoot();
    }

    private void showItems(int icon, String title, String subtitle) {
        list.add(new DatosAdapter.Items(title, subtitle, icon));
    }

    private void onClickItem(int position) {
        switch (position) {
            case 0 -> showBuyBolsaDiaria("*133*1*3");
            case 1 -> showBuyMensajeria("*133*1*2");
        }
    }

    private void showBuyBolsaDiaria(String code) {
        new BuyDialog(requireContext())
                .setDatosLte(R.string.plan_bolsa_diaria)
                .setPrecio("25 CUP")
                .setUSSD(code)
                .show();
    }

    private void showBuyMensajeria(String code) {
        new BuyDialog(requireContext())
                .setNacional(R.string.plan_mensajeria)
                .setPrecio("25 CUP")
                .setUSSD(code)
                .show();
    }
}
