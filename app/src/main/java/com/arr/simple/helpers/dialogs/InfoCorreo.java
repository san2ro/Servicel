package com.arr.simple.helpers.dialogs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.arr.simple.R;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.arr.simple.databinding.LayoutInfoAccountsBinding;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.arr.simple.helpers.profile.models.Correo;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoCorreo extends BottomSheetDialog {

    private LayoutInfoAccountsBinding binding;

    public InfoCorreo(Context context) {
        super(context);
        binding = LayoutInfoAccountsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.close.setOnClickListener(
                v -> getBehavior().setState(BottomSheetBehavior.STATE_HIDDEN));

        Map<String, Correo.CuentaCorreo> cuentasMap = ProcessProfile.getCuentasCorreo(context);
        setupNavegacionListView(cuentasMap);
    }

    private void setupNavegacionListView(Map<String, Correo.CuentaCorreo> cuentasMap) {
        List<NavegacionItem> items = new ArrayList<>();

        for (Map.Entry<String, Correo.CuentaCorreo> entry : cuentasMap.entrySet()) {
            items.add(new NavegacionItem(entry.getKey()));

            Correo.CuentaCorreo cuenta = entry.getValue();
            items.add(new NavegacionItem("Cuenta", cuenta.cuenta));
            items.add(new NavegacionItem("Fecha de venta", cuenta.venta));
        }

        NavegacionAdapter adapter = new NavegacionAdapter(getContext(), items);
        binding.listView.setAdapter(adapter);
    }

    public static class NavegacionItem {
        public static final int TYPE_HEADER = 0;
        public static final int TYPE_DETAIL = 1;

        private int type;
        private String key;
        private String value;

        // Constructor para header
        public NavegacionItem(String email) {
            this.type = TYPE_HEADER;
            this.key = email;
        }

        // Constructor para detalles
        public NavegacionItem(String key, String value) {
            this.type = TYPE_DETAIL;
            this.key = key;
            this.value = value;
        }

        // Getters
        public int getType() {
            return type;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    public static class NavegacionAdapter extends BaseAdapter {
        private List<NavegacionItem> items;
        private LayoutInflater inflater;

        public NavegacionAdapter(Context context, List<NavegacionItem> items) {
            this.items = items;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).getType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NavegacionItem item = items.get(position);

            if (convertView == null) {
                if (item.getType() == NavegacionItem.TYPE_HEADER) {
                    convertView = inflater.inflate(R.layout.item_view_header, parent, false);
                } else {
                    convertView = inflater.inflate(R.layout.item_detail_account, parent, false);
                }
            }

            if (item.getType() == NavegacionItem.TYPE_HEADER) {
                TextView tvHeader = convertView.findViewById(R.id.header_text);
                tvHeader.setText(item.getKey());
            } else {
                TextView tvKey = convertView.findViewById(R.id.tvKey);
                TextView tvValue = convertView.findViewById(R.id.tvValue);

                tvKey.setText(item.getKey());
                tvValue.setText(item.getValue());
            }

            return convertView;
        }
    }
}
