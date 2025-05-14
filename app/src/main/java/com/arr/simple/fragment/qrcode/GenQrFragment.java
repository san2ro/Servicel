package com.arr.simple.fragment.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.arr.simple.R;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentGenQrBinding;
import com.arr.simple.helpers.data.ProcessData;
import com.arr.simple.helpers.qr.QRGen;

public class GenQrFragment extends Fragment {

    private FragmentGenQrBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentGenQrBinding.inflate(inflater, container, false);

        try {
            String number = ProcessData.getPhone(requireActivity());
            Bitmap qr = QRGen.generate(requireActivity(), number, R.mipmap.ic_launcher);
            binding.code.setImageBitmap(qr);
        } catch (Exception err) {
            err.printStackTrace();
        }
        
        return binding.getRoot();
    }
}
