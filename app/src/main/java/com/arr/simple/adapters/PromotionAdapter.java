package com.arr.simple.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import com.arr.simple.R;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.databinding.LayoutItemsPromotionsBinding;
import com.arr.simple.helpers.glide.GlideToSvg;
import com.arr.simple.helpers.glide.helpers.SvgSoftwareLayerSetter;
import com.bumptech.glide.Glide;
import cu.arr.etecsa.api.etecsa.promo.model.Promotion;
import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Promotion> list;
    private Context mContext;

    public PromotionAdapter(Context c, List<Promotion> list) {
        this.mContext = c;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutItemsPromotionsBinding binding =
                LayoutItemsPromotionsBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new PromoView(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PromoView view) {
            Promotion item = list.get(position);
            try {
                // background
                Glide.with(mContext)
                        .load(item.getBackgroundUrl())
                        .centerCrop()
                        .timeout(10000)
                        .placeholder(R.drawable.bg_headers)
                        .into(view.binding.carouselImageView);

                // svg
                Glide.with(mContext)
                        .as(PictureDrawable.class)
                        .listener(new SvgSoftwareLayerSetter())
                        .load(Uri.parse(item.getSvgUrl()))
                        .into(view.binding.svgImage);

                // link
                view.binding.carouselItemContainer.setOnClickListener(
                        v -> {
                            Intent intent =
                                    new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
                            mContext.startActivity(intent);
                        });
            } catch (Exception e) {
                Log.e("GlideError", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PromoView extends RecyclerView.ViewHolder {

        private LayoutItemsPromotionsBinding binding;

        public PromoView(LayoutItemsPromotionsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
