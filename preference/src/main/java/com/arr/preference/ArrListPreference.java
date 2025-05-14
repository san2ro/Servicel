package com.arr.preference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ArrListPreference extends ListPreference {

    public ArrListPreference(Context context, AttributeSet attr) {
        super(context, attr);
        setLayoutResource(R.layout.layout_view_preference);
    }

    @Override
    protected void onClick() {
        final CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();
        int selectedIndex = findIndexOfValue(getValue());
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(getDialogTitle())
                .setSingleChoiceItems(
                        entries,
                        selectedIndex,
                        (dialog, which) -> {
                            String value = (String) entryValues[which];
                            if (callChangeListener(value)) {
                                setValue(value);
                            }
                            dialog.dismiss();
                        })
                .setPositiveButton(android.R.string.ok, null)
                .show();
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
