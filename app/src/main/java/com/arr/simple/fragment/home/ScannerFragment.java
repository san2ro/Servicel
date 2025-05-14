package com.arr.simple.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.arr.simple.databinding.FragmentScannerBinding;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class ScannerFragment extends Fragment {

    private FragmentScannerBinding binding;
    private CodeScanner codeScanner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentScannerBinding.inflate(inflater, container, false);

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
                            viewModel.setText(code);
                            NavController navController =
                                    Navigation.findNavController(requireView());
                            navController.navigateUp();
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
