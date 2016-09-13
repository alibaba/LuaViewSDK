package com.taobao.luaview.view.imageview;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.provider.ImageProvider;

import java.lang.ref.WeakReference;

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
        initRecycler(context);
    }

    private void initRecycler(Context context) {
        if (context instanceof Activity) {
            ImageActivityLifeCycle.getInstance(((Activity) context).getApplication()).watch(this);
        }
    }

    @Override
    public void loadUrl(final String url, final LoadCallback callback) {
        this.mUrl = url;
        final ImageProvider provider = LuaView.getImageProvider();
        if (provider != null) {
            provider.load(getContext(), new WeakReference<ImageView>(this), url, new WeakReference<LoadCallback>(callback));
        }
    }

    @Override
    public String getUrl() {//TODO 本地图片的时候，获取的是空的
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        restoreImage();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseBitmap();
    }

    public void restoreImage() {// 恢复被清空的image
        if (isNetworkMode) {
            loadUrl(mUrl, null);
        }
    }

    public void releaseBitmap() {// 释放图片内存
        if (isNetworkMode) {
            setImageDrawable(null);
        }
    }

}
