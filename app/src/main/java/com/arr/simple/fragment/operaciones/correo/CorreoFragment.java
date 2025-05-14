package com.arr.simple.fragment.operaciones.correo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.simple.activities.operaciones.correo.ChangePasswordActivity;
import com.arr.simple.adapters.OpAdapter;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentOpBinding;
import com.arr.simple.helpers.dialogs.InfoCorreo;
import java.util.ArrayList;
import java.util.List;

public class CorreoFragment extends Fragment {

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
                new InfoCorreo(requireContext()).show();
                break;
            case 1:
                startActivity(new Intent(requireActivity(), ChangePasswordActivity.class));
                requireActivity().finish();
                break;
        }
    }

    private void listOperations() {
        list.clear();
        list.add(new OpAdapter.Items(R.drawable.ic_about_24dp, "Información"));
        list.add(new OpAdapter.Items(R.drawable.ic_password_unfill, "Cambiar contraseña"));
        
        /*
        list.add(new OpAdapter.Items(R.drawable.ic_email_24dp, "Crear cuenta de correo"));
        list.add(new OpAdapter.Items(R.drawable.ic_about_24dp, "Recuperar contraseña"));
        */
    }
}
