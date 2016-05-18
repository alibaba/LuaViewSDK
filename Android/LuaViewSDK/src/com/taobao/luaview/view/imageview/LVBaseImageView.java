package com.taobao.luaview.view.imageview;

import android.app.Activity;
import android.content.Context;

import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.provider.ImageProvider;

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
            provider.load(this, url, callback);
        }
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        super.onWindowVisibilityChanged(visibility);
//        if (isNetworkMode) { // 清空内存
//            if (visibility == View.VISIBLE && mAttachedWindow) {
//                restoreImage();
//            } else {
//                releaseBitmap();
//            }
//        }
//    }

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
