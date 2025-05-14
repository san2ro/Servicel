package com.arr.simple.fragment.datos.paquetes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.simple.R;
import androidx.fragment.app.Fragment;
import com.arr.simple.adapters.DatosAdapter;
import com.arr.simple.databinding.FragmentPaquetesBinding;
import com.arr.simple.helpers.dialogs.BuyDialog;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.ussd.USSDUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;

public class CombosFragment extends Fragment {

    private FragmentPaquetesBinding binding;

    private List<DatosAdapter.Items> list = new ArrayList<>();
    private DatosAdapter adapter;

    private SPHelper sp;

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
                R.drawable.ic_planes_combinados_24dp,
                "1.4 GB",
                getContext().getString(R.string.subtitle_plan_basico));
        showItems(
                R.drawable.ic_planes_combinados_24dp,
                "3.5 GB",
                getContext().getString(R.string.subtitle_plan_medio));
        showItems(
                R.drawable.ic_planes_combinados_24dp,
                "8 GB",
                getContext().getString(R.string.subtitle_plan_extra));

        return binding.getRoot();
    }

    private void showItems(int icon, String title, String subtitle) {
        list.add(new DatosAdapter.Items(title, subtitle, icon));
    }

    private void onClickItem(int position) {
        switch (position) {
            case 0 -> showBuyPlanBasico("*133*5*1");
            case 1 -> showBuyPlanMedio("*133*5*2");
            case 2 -> showBuyPlanExtra("*133*5*3");
        }
    }

    private void showBuyPlanBasico(String code) {
        new BuyDialog(requireContext())
                .setAllNetwork(R.string.plan_basico_all_network)
                .setDatosLte(R.string.plan_basico_lte_network)
                .setMinutos(R.string.minutos_plan_basico)
                .setMensajes(R.string.mensajes_plan_basico)
                .setNacional(R.string.plan_nacional)
                .setPrecio("110 CUP")
                .setUSSD(code)
                .show();
    }

    private void showBuyPlanMedio(String code) {
        new BuyDialog(requireContext())
                .setAllNetwork(R.string.plan_medio_all_network)
                .setDatosLte(R.string.plan_medio_lte_network)
                .setMinutos(R.string.minutos_plan_medio)
                .setMensajes(R.string.mensajes_plan_medio)
                .setNacional(R.string.plan_nacional)
                .setPrecio("250 CUP")
                .setUSSD(code)
                .show();
    }

    private void showBuyPlanExtra(String code) {
        new BuyDialog(requireContext())
                .setAllNetwork(R.string.plan_extra_all_network)
                .setDatosLte(R.string.plan_extra_lte_network)
                .setMinutos(R.string.minutos_plan_extra)
                .setMensajes(R.string.mensajes_plan_extra)
                .setNacional(R.string.plan_nacional)
                .setPrecio("500 CUP")
                .setUSSD(code)
                .show();
    }
}
