package com.taobao.luaview.view.imageview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

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
        if(context instanceof Activity){
            ((Activity) context).getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {
                    if(activity == getContext()) {
                        restoreImage();
                    }
                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    if(activity == getContext()) {
                        releaseBitmap();
                    }
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if(activity == getContext()) {
                        ((Activity) getContext()).getApplication().unregisterActivityLifecycleCallbacks(this);
                    }
                }
            });
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

    private void restoreImage() {// 恢复被清空的image
        if (isNetworkMode) {
            loadUrl(mUrl, null);
        }
    }

    private void releaseBitmap() {// 释放图片内存
        if (isNetworkMode) {
            setImageDrawable(null);
        }
    }

}
