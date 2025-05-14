package com.arr.simple.helpers.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import com.arr.simple.databinding.LayoutViewPaymentDetailsBinding;
import com.arr.simple.helpers.qr.QRGen;
import com.arr.simple.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PaymentDetails extends BottomSheetDialog {

    private LayoutViewPaymentDetailsBinding binding;
    private Context mContext;
    private String linkTransfermovil;

    public PaymentDetails setDescuento(String descuento) {
        binding.descuento.setText(descuento != null ? descuento + "%" : "");
        return this;
    }

    public PaymentDetails setMontoPagado(String montoPagado) {
        binding.monto.setText(montoPagado != null ? montoPagado + " CUP" : "");
        return this;
    }

    public PaymentDetails setMontoFinal(String montoFinal) {
        binding.total.setText(montoFinal != null ? montoFinal + " CUP" : "");
        return this;
    }

    public PaymentDetails setLinkTm(String link) {
        linkTransfermovil = link;
        generateCodeQR(link);
        return this;
    }

    public PaymentDetails(Context context) {
        super(context);
        this.mContext = context;
        binding = LayoutViewPaymentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toTm.setOnClickListener(this::openTm);
        binding.close.setOnClickListener(v -> dismiss());
    }

    private void openTm(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkTransfermovil));
        mContext.startActivity(intent);
        dismiss();
    }

    private void generateCodeQR(String stringQr) {
        try {
            Bitmap bitmap = QRGen.generate(getContext(), stringQr, R.mipmap.ic_launcher);
            binding.codeQr.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
