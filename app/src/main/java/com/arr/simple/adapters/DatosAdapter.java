package com.arr.simple.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.databinding.ViewItemsDatosBinding;
import java.util.List;

public class DatosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Items> mList;
    private OnClickListener mListener;

    public DatosAdapter(List<Items> list, OnClickListener listener) {
        this.mList = list;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewItemsDatosBinding binding =
                ViewItemsDatosBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new DatosView(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Items model = mList.get(position);
        if (holder instanceof DatosView view) {
            view.binding.title.setText(model.title);
            view.binding.subtitle.setText(model.subtitle);
            view.binding.icon.setImageResource(model.icon);

            // onclick
            view.binding.rootItem.setOnClickListener(v -> mListener.onClickItem(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class DatosView extends RecyclerView.ViewHolder {

        private ViewItemsDatosBinding binding;

        public DatosView(ViewItemsDatosBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class Items {
        public int icon;
        public String title;
        public String subtitle;

        public Items(String title, String subtitle, int icon) {
            this.title = title;
            this.subtitle = subtitle;
            this.icon = icon;
        }
    }

    public interface OnClickListener {
        void onClickItem(int position);
    }
}
