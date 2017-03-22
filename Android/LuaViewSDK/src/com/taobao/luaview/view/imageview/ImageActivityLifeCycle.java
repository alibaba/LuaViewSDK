/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.imageview;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * activity堆叠的时候释放资源处理
 */
public class ImageActivityLifeCycle implements Application.ActivityLifecycleCallbacks {
    private static ImageActivityLifeCycle instance;
    private ArrayList<WeakReference<BaseImageView>> weakReferenceList = new ArrayList<WeakReference<BaseImageView>>();

    public static ImageActivityLifeCycle getInstance(Application application) {
        if (instance == null) {
            instance = new ImageActivityLifeCycle();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public void watch(BaseImageView imageView) {
        weakReferenceList.add(new WeakReference<BaseImageView>(imageView));
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        int size = weakReferenceList.size();
        for (int i = size - 1; i >= 0; i--) {
            BaseImageView imageView = weakReferenceList.get(i).get();
            if (imageView == null) {
                weakReferenceList.remove(i);
            } else {
                if (imageView.getContext() == activity) {
                    imageView.restoreImage();
                }
            }
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
        int size = weakReferenceList.size();
        for (int i = size - 1; i >= 0; i--) {
            BaseImageView imageView = weakReferenceList.get(i).get();
            if (imageView == null) {
                weakReferenceList.remove(i);
            } else {
                if (imageView.getContext() == activity) {
                    imageView.releaseBitmap();
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        int size = weakReferenceList.size();
        for (int i = size - 1; i >= 0; i--) {
            BaseImageView imageView = weakReferenceList.get(i).get();
            if (imageView == null) {
                weakReferenceList.remove(i);
            } else {
                if (imageView.getContext() == activity) {
                    imageView.releaseBitmap();
                }
            }
        }
    }
}