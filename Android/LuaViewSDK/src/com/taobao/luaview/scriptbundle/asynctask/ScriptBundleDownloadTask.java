/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;

import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleDownloadDelegate;
import com.taobao.luaview.util.DebugUtil;

/**
 * download lua script bundle from server and return saved path in local file system
 *
 * @author song
 */
public class ScriptBundleDownloadTask extends BaseAsyncTask<String, Integer, String> {
    private Context mContext;
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;

    public ScriptBundleDownloadTask(final Context context, LuaScriptLoader.ScriptLoaderCallback callback) {
        if (context != null) {
            mContext = context.getApplicationContext();
        }
        mScriptLoaderCallback = callback;
    }

    /**
     * 接受两个参数，第一个是下载的url，第二个是sha256
     *
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        DebugUtil.tsi("luaviewp-scriptDownloadTask");

        final String url = params[0];
        final String sha256 = params.length > 1 ? params[1] : null;

        if (new ScriptBundleDownloadDelegate(url, sha256).download()) {
            return url;
        } else {
            return null;
        }
    }

    @Override
    protected void onCancelled() {
        callLoaderCallback(null);
    }

    @Override
    protected void onCancelled(String s) {
        callLoaderCallback(s);
    }

    @Override
    protected void onPostExecute(String url) {
        DebugUtil.tei("luaviewp-scriptDownloadTask");
        callLoaderCallback(url);
    }

    private void callLoaderCallback(String url) {
        if (url != null) {//如果下载保存成功，则进行解包操作（不论是否需要请求回调 ）
            new ScriptBundleUnpackTask(mContext, mScriptLoaderCallback).executeInPool(url);
        } else if (mScriptLoaderCallback != null) {
            mScriptLoaderCallback.onScriptLoaded(null);
        }
    }
}