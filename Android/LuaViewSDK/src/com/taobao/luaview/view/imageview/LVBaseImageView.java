package com.taobao.luaview.view.imageview;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * ImageView Impl
 *
 * @author song
 * @date 16/3/9
 */
public class LVBaseImageView extends BaseImageView {
    private String mUrl;

    public LVBaseImageView(Context context) {
        super(context);
    }

    @Override
    public void loadUrl(final String url, final LoadCallback callback) {
        this.mUrl = url;
        if (callback != null) {
            Glide.with(getContext()).load(url).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    if (callback != null) {
                        callback.onLoadResult(null);
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (callback != null) {
                        callback.onLoadResult(resource.getCurrent());
                    }
                    return false;
                }
            }).into(this);
        } else {
            Glide.with(getContext()).load(url).into(this);
        }
    }

    @Override
    public String getUrl() {
        return mUrl;
    }


}
