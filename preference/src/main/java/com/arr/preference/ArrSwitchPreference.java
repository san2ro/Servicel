package com.arr.preference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.arr.preference.R;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreferenceCompat;
import com.google.android.material.materialswitch.MaterialSwitch;

public class ArrSwitchPreference extends SwitchPreferenceCompat {

    private Context mContext;
    private int visible;
    private View itemView;

    public ArrSwitchPreference(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
        setLayoutResource(R.layout.layout_view_preference_switch);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        itemView = holder.itemView;

        MaterialSwitch materialSwitch = (MaterialSwitch) holder.findViewById(R.id.switchWidget);
        if (materialSwitch != null) {
            materialSwitch.setChecked(isChecked());
        }
        if (isChecked()) {
            materialSwitch.setThumbIconDrawable(getContext().getDrawable(R.drawable.ic_checked));
        } else {
            materialSwitch.setThumbIconDrawable(null);
        }

        // summary
        final TextView summaryView = (TextView) holder.findViewById(android.R.id.summary);
        if (summaryView != null) {
            final CharSequence summary = getSummary();
            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            } else {
                summaryView.setVisibility(View.GONE);
            }
        }

        // title
        final TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        if (titleView != null) {
            final CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        // icon
        final ImageView imageView = (ImageView) holder.findViewById(android.R.id.icon);
        if (imageView != null) {
            final Drawable mIcon = getIcon();
            if (mIcon != null) {
                imageView.setImageDrawable(mIcon);
                imageView.setVisibility(View.VISIBLE);
            } else {
                if (isIconSpaceReserved()) {
                    imageView.setVisibility(View.INVISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        }
    }
}