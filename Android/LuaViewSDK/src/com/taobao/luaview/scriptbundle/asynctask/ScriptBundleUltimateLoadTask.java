/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;
import android.support.annotation.Nullable;

import com.taobao.luaview.cache.AppCache;
import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleDownloadDelegate;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleLoadDelegate;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleUnpackDelegate;
import com.taobao.luaview.util.DebugUtil;
import com.taobao.luaview.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

/**
 * download lua script bundle from server and load as a ScriptBundle
 *
 * @author song
 */
public class ScriptBundleUltimateLoadTask extends BaseAsyncTask<String, Integer, ScriptBundle> {
    private Context mContext;
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;
    private String mPackageName;

    public ScriptBundleUltimateLoadTask(Context context, LuaScriptLoader.ScriptLoaderCallback scriptLoaderCallback) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
        this.mScriptLoaderCallback = scriptLoaderCallback;
    }

    public void load(String... params) {
        super.executeInPool(params);
    }

    public void loadAsset(String... params) {
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

        if (LuaScriptManager.existsScriptBundle(url)) {//读取并加载，之前的脚本
            scriptBundle = AppCache.getCache(AppCache.CACHE_SCRIPTS).getLru(url);
            if (scriptBundle != null) {
                return scriptBundle;
            } else {
                DebugUtil.tsi("luaviewp-loadBundle");

                scriptBundle = ScriptBundleUnpackDelegate.loadBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, destFolderPath);//TODO 性能瓶颈

                DebugUtil.tei("luaviewp-loadBundle");
            }

        } else if (LuaScriptManager.existsPredownloadBundle(url)) {//预先加载的地址有脚本，则尝试解压并加载 xxx.zip
            scriptBundle = AppCache.getCache(AppCache.CACHE_SCRIPTS).getLru(url);
            if (scriptBundle != null) {
                return scriptBundle;
            } else {
                DebugUtil.tsi("luaviewp-loadPredownloadBundle");

                String uri = LuaScriptManager.buildPredownloadScriptBundleFilePath(url);
                InputStream inputStream = FileUtil.open(uri);
                scriptBundle = ScriptBundleUnpackDelegate.unpack(url, inputStream);

                DebugUtil.tei("luaviewp-loadPredownloadBundle");
            }
        } else {//下载解压加载

            // Assert 预置包
            // loadAssertScriptBundle();

            // 下载包
            callLoaderDownloadStart();

            // download
            ScriptBundleDownloadDelegate downloadDelegate = new ScriptBundleDownloadDelegate(url, sha256);
            HttpURLConnection connection = downloadDelegate.createHttpUrlConnection();
            InputStream inputStream = downloadDelegate.downloadAsStream(connection);

            if (inputStream != null) {
                scriptBundle = ScriptBundleUnpackDelegate.unpack(url, inputStream);//unpack
            }

            if (connection != null) {
                connection.disconnect();
            }

            //下载结束
            callLoaderDownloadEnd(scriptBundle);
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


    @Nullable
    private void loadAssertScriptBundle() {
        if (mPackageName != null && mScriptLoaderCallback instanceof LuaScriptLoader.ScriptLoaderCallback2) {//有预置包则先加载预置的
            String assetPackageName = LuaScriptManager.buildFileName(mPackageName, LuaScriptManager.POSTFIX_LV_STANDARD_SYNTAX_ZIP);//szip，只处理szip，TODO处理其他类型zip
            if (LuaScriptManager.isLuaScriptZip(assetPackageName)) {//asset下的包加载
                ScriptBundle scriptBundle = ScriptBundleUnpackDelegate.unpack(mContext, FileUtil.removePostfix(assetPackageName), "luaview" + File.separator + assetPackageName);

                if (scriptBundle != null) {

                    scriptBundle = new ScriptBundleLoadDelegate().load(mContext, scriptBundle);

                    //call callback when asset's script loaded
                    callLoaderCallbackWhenAssetLoaded((LuaScriptLoader.ScriptLoaderCallback2) mScriptLoaderCallback, scriptBundle);
                }
            }
        }
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

    /**
     * 开始下载
     */
    private void callLoaderDownloadStart() {
        if (mScriptLoaderCallback instanceof LuaScriptLoader.ScriptLoaderCallback2) {
            ((LuaScriptLoader.ScriptLoaderCallback2) mScriptLoaderCallback).onScriptDownloadStart();
        }
    }

    /**
     * 下载结束
     *
     * @param bundle
     */
    private void callLoaderDownloadEnd(ScriptBundle bundle) {
        if (mScriptLoaderCallback instanceof LuaScriptLoader.ScriptLoaderCallback2) {
            ((LuaScriptLoader.ScriptLoaderCallback2) mScriptLoaderCallback).onScriptDownloadEnd(bundle);
        }
    }

    /**
     * 加载结束
     *
     * @param unzippedScripts
     */
    private void callLoaderCallback(ScriptBundle unzippedScripts) {
        if (mScriptLoaderCallback != null) {
            mScriptLoaderCallback.onScriptLoaded(unzippedScripts);
        }
    }

    private void callLoaderCallbackWhenAssetLoaded(final LuaScriptLoader.ScriptLoaderCallback2 callback, final ScriptBundle bundle) {
//        LuaViewUtil.runOnUiThread(mContext, new Runnable() {
//            @Override
//            public void run() {
//                if (callback != null) {
//                    callback.onAssetScriptLoaded(bundle);
//                }
//            }
//        });
    }
}