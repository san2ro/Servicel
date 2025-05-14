package com.arr.simple.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import com.arr.simple.R;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.databinding.ItemViewBalanceBinding;
import com.arr.simple.databinding.ItemViewDataBinding;
import com.arr.simple.databinding.ItemViewHeaderBinding;
import com.arr.simple.databinding.ItemsViewBalancesBinding;
import com.arr.simple.helpers.profile.utils.DataUsage;
import com.arr.simple.models.ListItem;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BalanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ListItem> items;
    private static final int TYPE_EMPTY = 2;
    private GridLayoutManager layoutManager;

    public BalanceAdapter(List<ListItem> items, GridLayoutManager layoutManager) {
        this.items = items;
        this.layoutManager = layoutManager;
        setupSpanSizeLookup();
    }

    private void setupSpanSizeLookup() {
        layoutManager.setSpanSizeLookup(
                new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int viewType = getItemViewType(position);

                        // Caso 1: Headers y vistas vacías ocupan todo el ancho
                        if (viewType == ListItem.TYPE_HEADER || viewType == TYPE_EMPTY) {
                            return layoutManager.getSpanCount();
                        }

                        // Caso 2: Si es el último elemento, ocupa todo el ancho
                        if (position == items.size() - 1) {
                            return layoutManager.getSpanCount();
                        }

                        // Caso 3: Si el siguiente elemento es un header, ocupa todo el ancho
                        if (position < items.size() - 1
                                && getItemViewType(position + 1) == ListItem.TYPE_HEADER) {
                            return layoutManager.getSpanCount();
                        }

                        // Caso normal: 1 columna
                        return 1;
                    }
                });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ListItem.TYPE_HEADER:
                ItemViewHeaderBinding headerBinding =
                        ItemViewHeaderBinding.inflate(inflater, parent, false);
                return new HeaderViewHolder(headerBinding);
            case TYPE_EMPTY:
                View emptyView = inflater.inflate(R.layout.item_view_empty_balance, parent, false);
                return new EmptyViewHolder(emptyView);
            case ListItem.TYPE_ITEM:
                ItemViewBalanceBinding itemBinding =
                        ItemViewBalanceBinding.inflate(inflater, parent, false);
                return new ItemViewHolder(itemBinding);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ListItem.TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(items.get(position).getHeader());
                break;
            case TYPE_EMPTY:
                break;
            case ListItem.TYPE_ITEM:
                ListItem item = items.get(position);
                ((ItemViewHolder) holder)
                        .bind(
                                item.getTitle(),
                                item.getPlan(),
                                item.getValue(),
                                item.getDate(),
                                position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.isEmpty() ? 1 : items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.isEmpty()) {
            return TYPE_EMPTY;
        }
        return items.get(position).getType();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ItemViewHeaderBinding binding;

        public HeaderViewHolder(@NonNull ItemViewHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String header) {
            binding.headerText.setText(header);
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemViewBalanceBinding binding;

        public ItemViewHolder(@NonNull ItemViewBalanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String title, String plan, String value, String date, int position) {
            binding.title.setText(getTitle(title));

            if (title.equals("MINUTOS") || title.equals("BONO MINUTOS")) {
                binding.textRestante.setText(convertir(value));
                binding.textPlan.setText(convertir(plan));
            } else {
                binding.textRestante.setText(value);
                binding.textPlan.setText(plan);
            }

            binding.textVence.setText(getDays(date));
            binding.progress.setProgress((int) DataUsage.getProgressUsage(value, plan, title));
        }

        private String getTitle(String title) {
            switch (title) {
                case "DATOS":
                    return "Datos";
                case "DATOS LTE":
                    return "Datos 4G";
                case "BONO DATOS":
                    return "Bono de datos";
                case "DATOS NACIONALES":
                    return "Datos nacionales";
                case "SMS":
                    return "Mensajes";
                case "MINUTOS":
                    return "Minutos";
                case "BONO SMS":
                    return "Bono SMS";
                case "BONO MINUTOS":
                    return "Bono de minutos";
                default:
                    return title;
            }
        }

        private String convertir(String str1) {
            if (str1 == null || str1.trim().isEmpty()) {
                return null;
            }

            String[] partes = str1.split(":");

            try {
                if (partes.length == 5) {
                    int dias = Integer.parseInt(partes[1]);
                    int horas = Integer.parseInt(partes[2]);
                    int minutos = Integer.parseInt(partes[3]);
                    int segundos = Integer.parseInt(partes[4]);

                    horas += dias * 24;
                    return String.format("%02d:%02d:%02d", horas, minutos, segundos);
                } else if (partes.length == 3) {
                    int horas = Integer.parseInt(partes[0]);
                    int minutos = Integer.parseInt(partes[1]);
                    int segundos = Integer.parseInt(partes[2]);

                    return String.format("%02d:%02d:%02d", horas, minutos, segundos);
                } else if (partes.length == 2) {
                    int minutos = Integer.parseInt(partes[0]);
                    int segundos = Integer.parseInt(partes[1]);

                    return String.format("00:%02d:%02d", minutos, segundos);
                } else {
                    Log.w("convertir", "Formato no reconocido: " + str1);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.e("convertir", "Error al parsear números: " + str1, e);
            }

            return null;
        }

        public static String getDays(String expireDate) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date fechaDestino = dateFormat.parse(expireDate);
                Calendar calendarActual = Calendar.getInstance();
                Date fechaActual = calendarActual.getTime();

                Calendar calendarDestino = Calendar.getInstance();
                calendarDestino.setTime(fechaDestino);
                calendarDestino.set(Calendar.HOUR_OF_DAY, 0);
                calendarDestino.set(Calendar.MINUTE, 0);
                calendarDestino.set(Calendar.SECOND, 0);
                calendarDestino.set(Calendar.MILLISECOND, 0);

                Calendar calendarActualSinHora = Calendar.getInstance();
                calendarActualSinHora.setTime(fechaActual);
                calendarActualSinHora.set(Calendar.HOUR_OF_DAY, 0);
                calendarActualSinHora.set(Calendar.MINUTE, 0);
                calendarActualSinHora.set(Calendar.SECOND, 0);
                calendarActualSinHora.set(Calendar.MILLISECOND, 0);

                // Calcular la diferencia en milisegundos
                long diferenciaMilis =
                        calendarDestino.getTimeInMillis() - calendarActualSinHora.getTimeInMillis();

                // Convertir la diferencia a días
                long diasRestantes = TimeUnit.MILLISECONDS.toDays(diferenciaMilis);

                if (diasRestantes > 1 && diasRestantes <= 30) {
                    return diasRestantes + " días";
                } else if (diasRestantes == 1) {
                    return "1 día";
                } else if (diasRestantes == 0) {
                    return "Hoy";
                } else {
                    return "0 días";
                }
            } catch (Exception e) {
                return "no disponible";
            }
        }
    }
}
