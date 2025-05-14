package com.arr.simple.fragment.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.FragmentQrScannerBinding;
import com.arr.simple.databinding.FragmentScannerBinding;
import com.arr.simple.fragment.home.ScannerViewModel;
import com.budiyev.android.codescanner.CodeScanner;
import com.google.zxing.Result;

public class ScannerQrFragment extends Fragment {

    private FragmentQrScannerBinding binding;
    private CodeScanner codeScanner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentQrScannerBinding.inflate(inflater, container, false);

        codeScanner = new CodeScanner(requireContext(), binding.scannerView);
        codeScanner.startPreview();
        codeScanner.setDecodeCallback(this::resultScanner);
        
        return binding.getRoot();
    }

    private void resultScanner(Result result) {
        requireActivity()
                .runOnUiThread(
                        () -> {
                            String code = result.getText();
                            ScannerViewModel viewModel =
                                    new ViewModelProvider(requireActivity())
                                            .get(ScannerViewModel.class);
                            viewModel.setTextPhone(code);
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                        });
    }

    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        codeScanner.releaseResources();
    }
}
