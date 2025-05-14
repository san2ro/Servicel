package com.arr.simple.helpers.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.databinding.LayoutViewBuyBinding;
import com.arr.simple.databinding.ViewItemsBuyBinding;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.ussd.USSDUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class BuyDialog extends BottomSheetDialog {

    private LayoutViewBuyBinding binding;

    private DetailsAdapter adapter;
    private List<DetailsAdapter.Items> listItems = new ArrayList<>();

    private SPHelper sp;
    private String ussd;

    public BuyDialog setAllNetwork(int value) {
        listItems.add(new DetailsAdapter.Items(getContext().getString(value)));
        return this;
    }

    public BuyDialog setDatosLte(int value) {
        listItems.add(new DetailsAdapter.Items(getContext().getString(value)));
        return this;
    }

    public BuyDialog setMinutos(int value) {
        listItems.add(new DetailsAdapter.Items(getContext().getString(value)));
        return this;
    }

    public BuyDialog setMensajes(int value) {
        listItems.add(new DetailsAdapter.Items(getContext().getString(value)));
        return this;
    }

    public BuyDialog setNacional(int value) {
        listItems.add(new DetailsAdapter.Items(getContext().getString(value)));
        return this;
    }

    public BuyDialog setPrecio(String precio) {
        binding.precio.setText(precio);
        return this;
    }

    public BuyDialog setUSSD(String ussd) {
        this.ussd = ussd;
        return this;
    }

    public BuyDialog(Context context) {
        super(context);
        this.sp = new SPHelper(context);
        binding = LayoutViewBuyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DetailsAdapter(listItems);
        binding.recycler.setAdapter(adapter);

        binding.close.setOnClickListener(v -> dismiss());
        binding.btnOk.setOnClickListener(
                v -> {
                    if (isPermissionCallGrated()) {
                        boolean confirmed = !sp.getBoolean("confirm", false);
                        String check = confirmed ? "" : "*1";
                        new USSDUtils(getContext()).executeUSSD(ussd + check + Uri.encode("#"));
                    } else {
                        Toast.makeText(getContext(), "Permiso de llamada no concedido", Toast.LENGTH_LONG).show();
                    }
                    dismiss();
                });
    }

    public static class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Items> list;

        public DetailsAdapter(List<Items> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewItemsBuyBinding binding =
                    ViewItemsBuyBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new DetailsView(binding);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Items model = list.get(position);
            if (holder instanceof DetailsView view) {
                view.binding.title.setText(model.title);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class DetailsView extends RecyclerView.ViewHolder {

            private ViewItemsBuyBinding binding;

            public DetailsView(ViewItemsBuyBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        public static class Items {

            public String title;

            public Items(String title) {
                this.title = title;
            }
        }
    }

    private boolean isPermissionCallGrated() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED;
    }
}
