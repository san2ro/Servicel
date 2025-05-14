package com.arr.simple.helpers.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.arr.simple.helpers.glide.helpers.GlideToSvgListener;
import com.arr.simple.helpers.glide.helpers.SvgSoftwareLayerSetter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class GlideToSvg {

    private static GlideToSvg instance;

    private RequestBuilder<PictureDrawable> requestBuilder;
    private int placeHolderLoading = -1;
    private int placeHolderError = -1;

    public static GlideToSvg init() {
        if (instance == null) {
            instance = new GlideToSvg();
        }

        return instance;
    }

    public GlideToSvg with(Context ctx) {
        createRequestBuilder(ctx);

        return instance;
    }

    public GlideToSvg withListener(GlideToSvgListener listener) {
        requestBuilder.listener(new SvgSoftwareLayerSetter(listener));

        return instance;
    }

    public GlideToSvg setPlaceHolder(int placeHolderLoading, int placeHolderError) {
        this.placeHolderError = placeHolderError;
        this.placeHolderLoading = placeHolderLoading;

        return instance;
    }

    public GlideToSvg timeout(int timeout) {
        requestBuilder.timeout(timeout);
        return instance;
    }

    public void load(Uri uri, ImageView imageView) {
        if (placeHolderLoading != -1 && placeHolderError != -1) {
            requestBuilder.apply(
                    new RequestOptions().placeholder(placeHolderLoading).error(placeHolderError));
        }

        requestBuilder.load(uri).into(imageView);
    }

    public static void justLoadImage(Context context, Uri uri, ImageView imageView) {
        Glide.with(context)
                .as(PictureDrawable.class)
                .listener(new SvgSoftwareLayerSetter())
                .load(uri)
                .into(imageView);
    }

    public static void justLoadImageAsBackground(Context context, Uri uri, final View view) {
        Glide.with(context)
                .load(uri)
                .into(
                        new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(
                                    @NonNull Drawable resource,
                                    Transition<? super Drawable> transition) {
                                view.setBackground(resource);
                            }
                        });
    }

    public RequestBuilder<PictureDrawable> getRequestBuilder() {
        return requestBuilder;
    }

    private void createRequestBuilder(Context ctx) {
        requestBuilder =
                Glide.with(ctx)
                        .as(PictureDrawable.class)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .listener(new SvgSoftwareLayerSetter());
    }
}
