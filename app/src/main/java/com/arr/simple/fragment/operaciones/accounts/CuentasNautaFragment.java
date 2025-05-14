package com.arr.simple.fragment.operaciones.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.arr.simple.R;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.simple.activities.operaciones.ChangePasswordActivity;
import com.arr.simple.activities.operaciones.NewAccountNavActivity;
import com.arr.simple.activities.operaciones.PayOnlineActivity;
import com.arr.simple.activities.operaciones.TopUpCuponActivity;
import com.arr.simple.activities.operaciones.TransferActivity;
import com.arr.simple.adapters.OpAdapter;
import com.arr.simple.databinding.FragmentOpBinding;
import com.arr.simple.databinding.FragmentPaquetesBinding;
import com.arr.simple.helpers.dialogs.InfoAccounts;
import java.util.ArrayList;
import java.util.List;

public class CuentasNautaFragment extends Fragment {

    private FragmentOpBinding binding;

    private List<OpAdapter.Items> list = new ArrayList<>();
    private OpAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentOpBinding.inflate(inflater, container, false);

        adapter = new OpAdapter(list, this::onClickItem);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);
        listOperations();

        return binding.getRoot();
    }

    private void onClickItem(int position) {
        switch (position) {
            case 0:
                new InfoAccounts(requireContext()).show();
                break;
            case 1:
                startActivity(new Intent(requireActivity(), ChangePasswordActivity.class));
                requireActivity().finish();
                break;
            case 2:
                startActivity(new Intent(requireActivity(), PayOnlineActivity.class));
                requireActivity().finish();
                break;
            case 3:
                startActivity(new Intent(requireActivity(), TopUpCuponActivity.class));
                requireActivity().finish();
                break;
            case 4:
                startActivity(new Intent(requireActivity(), TransferActivity.class));
                requireActivity().finish();
                break;
            case 5:
                startActivity(new Intent(requireActivity(), NewAccountNavActivity.class));
                requireActivity().finish();
                break;
        }
    }

    private void listOperations() {
        list.clear();
        list.add(new OpAdapter.Items(R.drawable.ic_about_24dp, "Detalle de mis cuentas"));
        list.add(new OpAdapter.Items(R.drawable.ic_password_unfill, "Cambiar contraseña"));
        list.add(new OpAdapter.Items(R.drawable.ic_pay_card, "Recargar cuenta en línea"));
        list.add(new OpAdapter.Items(R.drawable.ic_recarga_cupon, "Recargar con cupón"));
        list.add(new OpAdapter.Items(R.drawable.ic_send_money, "Transferir saldo"));
        list.add(new OpAdapter.Items(R.drawable.ic_navigation_24dp, "Crear cuenta de navegación"));
        /*
        list.add(new OpAdapter.Items(R.drawable.ic_logout, "Cerrar sesión"));
        */
    }
}
