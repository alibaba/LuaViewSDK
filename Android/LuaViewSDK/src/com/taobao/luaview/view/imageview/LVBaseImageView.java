/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.imageview;

import android.app.Activity;
import android.content.Context;

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
    public void loadUrl(final String url, final DrawableLoadCallback callback) {
        this.mUrl = url;
        final ImageProvider provider = LuaView.getImageProvider();
        if (provider != null) {
            provider.load(getContext(), new WeakReference<BaseImageView>(this), url, new WeakReference<DrawableLoadCallback>(callback));
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
        restoreImage();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        releaseBitmap();
        super.onDetachedFromWindow();
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
