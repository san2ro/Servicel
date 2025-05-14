package com.arr.simple.fragment.datos.paquetes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.simple.R;
import com.arr.simple.adapters.DatosAdapter;
import com.arr.simple.databinding.FragmentPaquetesBinding;

import com.arr.simple.helpers.dialogs.BuyDialog;
import java.util.ArrayList;
import java.util.List;

public class LteFragment extends Fragment {

    private FragmentPaquetesBinding binding;

    private List<DatosAdapter.Items> list = new ArrayList<>();
    private DatosAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentPaquetesBinding.inflate(inflater, container, false);

        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DatosAdapter(list, position -> onClickItem(position));
        binding.recycler.setAdapter(adapter);
        list.clear();
        showItems(R.drawable.ic_paquete_lte_24dp, "1 GB", "Paquete de datos sólo para la red 4G");
        showItems(R.drawable.ic_paquete_lte_24dp, "2.5 GB", "Paquete de datos sólo para la red 4G");
        showItems(R.drawable.ic_paquete_lte_24dp, "16 GB", "Paquete de datos sólo para la red 4G");

        return binding.getRoot();
    }

    private void showItems(int icon, String title, String subtitle) {
        list.add(new DatosAdapter.Items(title, subtitle, icon));
    }

    private void onClickItem(int position) {
        switch (position) {
            case 0 -> showBuyPlanBasico("*133*1*4*1");
            case 1 -> showBuyPlanMedio("*133*1*4*2");
            case 2 -> showBuyPlanExtra("*133*1*4*3");
        }
    }

    private void showBuyPlanBasico(String code) {
        new BuyDialog(requireContext())
                .setDatosLte(R.string.paquete_basico_lte_network)
                .setNacional(R.string.plan_nacional)
                .setPrecio("100 CUP")
                .setUSSD(code)
                .show();
    }

    private void showBuyPlanMedio(String code) {
        new BuyDialog(requireContext())
                .setDatosLte(R.string.paquete_medio_lte_network)
                .setNacional(R.string.plan_nacional)
                .setPrecio("200 CUP")
                .setUSSD(code)
                .show();
    }

    private void showBuyPlanExtra(String code) {
        new BuyDialog(requireContext())
                .setAllNetwork(R.string.paquete_extra_all_network)
                .setDatosLte(R.string.paquete_extra_lte_network)
                .setNacional(R.string.plan_nacional)
                .setPrecio("950 CUP")
                .setUSSD(code)
                .show();
    }
}
