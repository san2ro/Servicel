package com.arr.simple.helpers.glide.helpers;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.caverock.androidsvg.SVG;
import java.io.InputStream;

@GlideModule
public final class SvgModule extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.register(SVG.class, PictureDrawable.class, new SvgDrawableTranscoder(context))
                .append(InputStream.class, SVG.class, new SvgDecoder());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
