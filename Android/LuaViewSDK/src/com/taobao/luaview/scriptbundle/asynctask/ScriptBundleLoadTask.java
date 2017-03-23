/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;

import com.taobao.luaview.cache.AppCache;
import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleLoadDelegate;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleUnpackDelegate;
import com.taobao.luaview.util.DebugUtil;
import com.taobao.luaview.util.DecryptUtil;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.IOUtil;
import com.taobao.luaview.util.VerifyUtil;
import com.taobao.luaview.util.ZipUtil;

import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Prototype;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * 从文件系统中读取lua byte code文件，做签名验证，验证成功后，返回prototype
 *
 * @author song
 * @date 15/11/9
 * <p/>
 * 1. 从文件读取给定目录下的prototype，读取的同时做签名校验(AES256)
 * 2. 签名验证成功的文件，做unzip操作，获取需要返回的数据
 */
public class ScriptBundleLoadTask extends BaseAsyncTask<Object, Integer, ScriptBundle> {
    private static final String CACHE_SCRIPTS = AppCache.CACHE_SCRIPTS;
    private Context mContext;

    //加载的脚本
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;

    public ScriptBundleLoadTask(Context context, LuaScriptLoader.ScriptLoaderCallback scriptLoaderCallback) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
        this.mScriptLoaderCallback = scriptLoaderCallback;
    }


    /**
     * 接受两个参数，第一个是存储的地址，第二个是脚本解析的列表
     *
     * @param params
     * @return
     */
    @Override
    protected ScriptBundle doInBackground(Object... params) {
        final Object param0 = (params != null && params.length > 0) ? params[0] : null;
        ScriptBundle scriptBundle = (param0 instanceof ScriptBundle) ? (ScriptBundle) param0 : null;//首先判断脚本解析的列表是否存在（下载即加载的情形）
        final String url = (param0 instanceof String) ? (String) param0 : null;
        String destFolderPath = null;
        if (scriptBundle == null) {//不是下载即加载的情况
            destFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);

            scriptBundle = AppCache.getCache(CACHE_SCRIPTS).getLru(url);
            if (scriptBundle != null) {
                return scriptBundle;
            } else {
                DebugUtil.tsi("luaviewp-loadBundle");

                scriptBundle = ScriptBundleUnpackDelegate.loadBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, destFolderPath);//TODO 性能瓶颈

                DebugUtil.tei("luaviewp-loadBundle");
            }
        }

        scriptBundle = new ScriptBundleLoadDelegate().load(mContext, scriptBundle);//解密脚本或者加载Prototype

        if(scriptBundle != null) {
            if (url != null) {
                scriptBundle.setUrl(url);
            }
            if (destFolderPath != null) {
                scriptBundle.setBaseFilePath(destFolderPath);
            }

            //cache
            AppCache.getCache(CACHE_SCRIPTS).putLru(url != null ? url : scriptBundle.getUrl(), scriptBundle);
        }
        return scriptBundle;
    }


    @Override
    protected void onCancelled() {
        callLoaderCallback(null);

    }

    @Override
    protected void onCancelled(ScriptBundle scriptBundle) {
        callLoaderCallback(scriptBundle);
    }

    @Override
    protected void onPostExecute(ScriptBundle unzippedScripts) {
        callLoaderCallback(unzippedScripts);
    }

    private void callLoaderCallback(ScriptBundle unzippedScripts) {
        if (mScriptLoaderCallback != null) {
            mScriptLoaderCallback.onScriptLoaded(unzippedScripts);
        }
    }
}
