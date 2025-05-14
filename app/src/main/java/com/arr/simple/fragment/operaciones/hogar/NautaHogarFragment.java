package com.arr.simple.fragment.operaciones.hogar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.simple.adapters.OpAdapter;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentOpBinding;
import java.util.ArrayList;
import java.util.List;

public class NautaHogarFragment extends Fragment {

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
                break;
        }
    }

    private void listOperations() {
        list.clear();
        list.add(new OpAdapter.Items(R.drawable.ic_pay_card, "Pagar Nauta Hogar en línea"));
    }
}
