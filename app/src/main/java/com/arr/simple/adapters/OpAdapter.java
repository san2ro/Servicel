package com.arr.simple.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.databinding.ItemsOperacionesBinding;
import java.util.List;

public class OpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Items> list;

    private OnClickItemListener listener;

    public interface OnClickItemListener {
        void onClickItem(int position);
    }

    public OpAdapter(List<Items> list, OnClickItemListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemsOperacionesBinding binding =
                ItemsOperacionesBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new OpView(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Items model = list.get(position);
        if (holder instanceof OpView view) {
            view.binding.icon.setImageResource(model.icon);
            view.binding.title.setText(model.title);
            view.binding.subtitle.setVisibility(View.GONE);

            view.binding.rootItem.setOnClickListener(v -> listener.onClickItem(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Items {

        public int icon;
        public String title;
        public String subtitle;

        public Items(int icon, String title) {
            this.icon = icon;
            this.title = title;
        }
    }

    class OpView extends RecyclerView.ViewHolder {

        private ItemsOperacionesBinding binding;

        public OpView(ItemsOperacionesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
