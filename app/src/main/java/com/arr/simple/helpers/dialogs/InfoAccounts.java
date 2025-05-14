package com.arr.simple.helpers.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.arr.simple.R;
import android.widget.BaseAdapter;
import com.arr.simple.databinding.LayoutInfoAccountsBinding;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.arr.simple.helpers.profile.models.Cuentas;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoAccounts extends BottomSheetDialog {

    private LayoutInfoAccountsBinding binding;

    private static final double COST_PER_HOUR = 12.50;
    private static final double COST_PER_MINUTE = COST_PER_HOUR / 60;
    private static final double COST_PER_SECOND = COST_PER_MINUTE / 60;

    public InfoAccounts(Context context) {
        super(context);
        binding = LayoutInfoAccountsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        getBehavior().setDraggable(false);
        // getBehavior().setHideable(false);

        binding.close.setOnClickListener(
                v -> getBehavior().setState(BottomSheetBehavior.STATE_HIDDEN));

        Map<String, Cuentas> cuentasMap = ProcessProfile.getCuentas(context);
        setupNavegacionListView(cuentasMap);
    }

    private void setupNavegacionListView(Map<String, Cuentas> cuentasMap) {
        List<NavegacionItem> items = new ArrayList<>();

        for (Map.Entry<String, Cuentas> entry : cuentasMap.entrySet()) {
            items.add(new NavegacionItem(entry.getKey()));

            Cuentas cuenta = entry.getValue();
            items.add(
                    new NavegacionItem(
                            "Estado de la cuenta",
                            cuenta.status.equals("HABILITADO") ? "Cuenta activa" : cuenta.status));
            items.add(new NavegacionItem("Saldo", cuenta.saldo));
            items.add(new NavegacionItem("Tiempo", getTimeConection(cuenta.saldo)));
            items.add(
                    new NavegacionItem(
                            "Tipo de cuenta",
                            cuenta.tipo.equals("NAUTA_INTERNACIONAL_RECARGABLE")
                                    ? "Internacional recargable"
                                    : cuenta.tipo));
            items.add(new NavegacionItem("Fecha de venta", cuenta.venta));
            items.add(new NavegacionItem("Fecha de bloqueo", cuenta.bloqueo));
            items.add(new NavegacionItem("Fecha de eliminación", cuenta.delete));
            items.add(new NavegacionItem("Bonificación", cuenta.bono));
            items.add(new NavegacionItem("Horas de bonificación por usar", cuenta.horas_bono));
        }

        NavegacionAdapter adapter = new NavegacionAdapter(getContext(), items);
        binding.listView.setAdapter(adapter);
    }

    public static String getTimeConection(String saldo) {
        try {
            // 1. Validar que el saldo sea un número válido
            double saldoMonetario = Double.parseDouble(saldo);
            if (saldoMonetario < 0) {
                return "El saldo no puede ser negativo";
            }

            // 2. Calcular tiempo disponible en segundos
            double totalSeconds = saldoMonetario / COST_PER_SECOND;

            // 3. Convertir segundos a horas, minutos y segundos
            int hours = (int) (totalSeconds / 3600);
            int remainingSeconds = (int) (totalSeconds % 3600);
            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;

            // 4. Formatear como "HH:MM:SS" (dos dígitos cada uno)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);

        } catch (NumberFormatException e) {
            return "Formato inválido. Use un número (ejemplo: 30.08)";
        }
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
