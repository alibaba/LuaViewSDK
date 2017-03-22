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
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleDownloadDelegate;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleLoadDelegate;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleUnpackDelegate;
import com.taobao.luaview.util.DebugUtil;
import com.taobao.luaview.util.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Random;

/**
 * download lua script bundle from server and load as a ScriptBundle
 *
 * @author song
 */
public class ScriptBundleUltimateLoadTask extends BaseAsyncTask<String, Integer, ScriptBundle> {
    private Context mContext;
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;
    private boolean isAsset = false;

    public ScriptBundleUltimateLoadTask(Context context, LuaScriptLoader.ScriptLoaderCallback scriptLoaderCallback) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
        this.mScriptLoaderCallback = scriptLoaderCallback;
    }

    public void load(String... params) {
        this.isAsset = false;
        super.executeInPool(params);
    }

    public void loadAsset(String... params) {
        isAsset = true;
        super.executeInPool(params);
    }

    /**
     * 接受两个参数，第一个是下载的url，第二个是存储的地址
     *
     * @param params
     * @return
     */
    @Override
    public ScriptBundle doInBackground(String... params) {
        DebugUtil.tsi("luaviewp-ScriptBundleUltimateLoadTask");

        final String url = params[0];
        final String destFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);
        final String sha256 = params.length > 1 ? params[1] : null;

        ScriptBundle scriptBundle = null;

        if (LuaScriptManager.existsScriptBundle(url)) {//读取并加载
            scriptBundle = AppCache.getCache(AppCache.CACHE_SCRIPTS).getLru(url);
            if (scriptBundle != null) {
                return scriptBundle;
            } else {
                DebugUtil.tsi("luaviewp-loadBundle");

                scriptBundle = ScriptBundleUnpackDelegate.loadBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, destFolderPath);//TODO 性能瓶颈

                DebugUtil.tei("luaviewp-loadBundle");
            }

        } else {//下载解压加载
            ScriptBundleDownloadDelegate downloadDelegate = new ScriptBundleDownloadDelegate(url, sha256);
            HttpURLConnection connection = downloadDelegate.createHttpUrlConnection();
            InputStream inputStream = downloadDelegate.downloadAsStream(connection);

            if (inputStream != null) {
                scriptBundle = ScriptBundleUnpackDelegate.unpack(url, inputStream);//unpack
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        scriptBundle = new ScriptBundleLoadDelegate().load(mContext, scriptBundle);//解密脚本或者加载Prototype

        if (scriptBundle != null) {

            if (url != null) {
                scriptBundle.setUrl(url);
                scriptBundle.setBaseFilePath(destFolderPath);
            }

            //cache
            AppCache.getCache(AppCache.CACHE_SCRIPTS).putLru(url != null ? url : scriptBundle.getUrl(), scriptBundle);
        }

        return scriptBundle;
    }

    private void saveFileUsingFileOutputStream(File destFile, byte[] fileData) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(destFile);
            output.write(fileData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * save file
     *
     * @param destFile
     * @param fileData
     */
    private void saveFileUsingRandomAccessFile(File destFile, byte[] fileData) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(destFile, "rw");
            randomAccessFile.write(fileData);
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        DebugUtil.tei("luaviewp-ScriptBundleUltimateLoadTask");
        callLoaderCallback(unzippedScripts);
    }

    private void callLoaderCallback(ScriptBundle unzippedScripts) {
        if (mScriptLoaderCallback != null) {
            mScriptLoaderCallback.onScriptLoaded(unzippedScripts);
        }
    }
}