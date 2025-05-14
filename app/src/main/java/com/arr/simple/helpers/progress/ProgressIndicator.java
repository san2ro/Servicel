package com.arr.simple.helpers.progress;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.arr.simple.R;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;

public class ProgressIndicator {

    public static IndeterminateDrawable indicator(@NonNull Context context) {
        // Crear el spec para el indicador circular
        CircularProgressIndicatorSpec spec =
                new CircularProgressIndicatorSpec(
                        context,
                        null,
                        0,
                        com.google.android.material.R.style
                                .Widget_Material3_CircularProgressIndicator_ExtraSmall);
        spec.indicatorColors = new int[] {context.getColor(R.color.color_white)};
        // Crear el drawable del indicador circular
        return IndeterminateDrawable.createCircularDrawable(context, spec);
    }
}
