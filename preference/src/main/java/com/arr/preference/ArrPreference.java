package com.arr.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class ArrPreference extends Preference {

    private Drawable mIcon;
    private int mIconResId;
    private boolean mIconSpaceReserved;

    private final Context mContext;

    private int mTitleColor;

    public ArrPreference(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
        init(attr);
        setLayoutResource(R.layout.layout_view_preference);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a =
                    getContext().obtainStyledAttributes(attrs, R.styleable.ArrPreferenceTheme);

            try {
                if (a.hasValue(R.styleable.ArrPreferenceTheme_titleColor)) {
                    // Si se especifica un color, úsalo
                    setTitleColor(a.getColor(R.styleable.ArrPreferenceTheme_titleColor, 0));
                } else {
                    // Si no se especifica un color, no hagas nada y se usará el color por defecto
                    setTitleColor(0); // O no hagas nada
                }
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View itemView = holder.itemView;

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
                if (mTitleColor != 0) {
                    titleView.setTextColor(mTitleColor);
                }
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

    public void setTitleColor(int color) {
        this.mTitleColor = color;
    }
}
