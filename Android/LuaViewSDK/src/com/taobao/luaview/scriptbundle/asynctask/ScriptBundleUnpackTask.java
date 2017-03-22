/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;
import android.content.res.AssetManager;

import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleUnpackDelegate;
import com.taobao.luaview.util.FileUtil;

import java.io.File;

/**
 * 将下发的脚本bundle解压到本地文件系统，下发源码的时候才使用
 *
 * @author song
 *         <p/>
 *         1. 将输入流根据(文件名,data)的格式解出到文件
 *         2. 将输入流保存的签名信息(文件名,sign)解出到文件
 */
public class ScriptBundleUnpackTask extends BaseAsyncTask<Object, Integer, ScriptBundle> {
    private Context mContext;
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;

    public ScriptBundleUnpackTask(final Context context) {
        this(context, null);
    }

    public ScriptBundleUnpackTask(final Context context, final LuaScriptLoader.ScriptLoaderCallback scriptLoaderCallback) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
        this.mScriptLoaderCallback = scriptLoaderCallback;
    }

    /**
     * 接受一个参数，加载的脚本根目录
     *
     * @param params
     * @return
     */
    @Override
    protected ScriptBundle doInBackground(Object... params) {
        if (params != null && params.length > 0) {//一个参数，指定文件路径
            final String url = (String) params[0];
            final String asset = params.length > 1 ? String.valueOf(params[1]) : null;
            return ScriptBundleUnpackDelegate.unpack(mContext, url, asset);
        }
        return null;
    }

    @Override
    protected void onPostExecute(ScriptBundle bundle) {
        callLoaderCallback(bundle);
    }

    @Override
    protected void onCancelled() {
        callLoaderCallback(null);
    }

    @Override
    protected void onCancelled(ScriptBundle bundle) {
        callLoaderCallback(bundle);
    }

    private void callLoaderCallback(ScriptBundle bundle) {
        if (mScriptLoaderCallback != null) {
            if (bundle != null && bundle.size() > 0) {//启动loader task
                new ScriptBundleLoadTask(mContext, mScriptLoaderCallback).executeInPool(bundle);
            } else {
                mScriptLoaderCallback.onScriptLoaded(null);
            }
        }
    }
}