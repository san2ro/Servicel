package com.arr.simple.helpers.dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.Toast;
import com.arr.simple.databinding.LayoutQrDonationBinding;
import com.arr.simple.helpers.qr.QRGen;
import com.arr.simple.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DonationDialog extends BottomSheetDialog {

    private LayoutQrDonationBinding binding;

    public DonationDialog(Context context) {
        super(context);
        binding = LayoutQrDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StringBuilder str = new StringBuilder();
        str.append("TRANSFERMOVIL_ETECSA,TRANSFERENCIA,");
        str.append("9205129974463446,");
        str.append("54250705,");

        try {
            Bitmap qr = QRGen.generate(getContext(), str.toString(), R.mipmap.ic_launcher);
            binding.qrCode.setImageBitmap(qr);
        } catch (Exception err) {
            err.printStackTrace();
        }

        binding.copy.setOnClickListener(
                v -> copyToClipboard(context, "Tarjeta", "9205129974463446"));
    }

    private void copyToClipboard(Context context, String label, String text) {
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
