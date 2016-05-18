package com.taobao.luaview.view.imageview;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * activity堆叠的时候释放资源处理
 */
public class ImageActivityLifeCycle implements Application.ActivityLifecycleCallbacks {
    private WeakReference<LVBaseImageView> weakReference;

    public ImageActivityLifeCycle(LVBaseImageView imageView) {
        weakReference = new WeakReference<LVBaseImageView>(imageView);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        LVBaseImageView imageView = weakReference.get();
        if (imageView != null && imageView.getContext() == activity) {
            imageView.restoreImage();
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
        LVBaseImageView imageView = weakReference.get();
        if (imageView != null && imageView.getContext() == activity) {
            imageView.releaseBitmap();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LVBaseImageView imageView = weakReference.get();
        if (imageView != null && imageView.getContext() == activity) {
            ((Activity) imageView.getContext()).getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }
}