package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.util.AssetUtil;
import com.taobao.luaview.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 将下发的脚本bundle解压到本地文件系统，下发源码的时候才使用
 *
 * @author song
 *         <p/>
 *         1. 将输入流根据(文件名,data)的格式解出到文件
 *         2. 将输入流保存的签名信息(文件名,sign)解出到文件
 */
public class ScriptBundleUnpackTask extends AsyncTask<Object, Integer, ScriptBundle> {
    private Context mContext;
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;

    /**
     * 将所有assert下面的脚本解压缩到文件系统
     *
     * @param assetFolderPath
     */
    public static void unpackAllAssetScripts(final Context context, final String assetFolderPath) {
        if (context != null && assetFolderPath != null) {
            final AssetManager assetManager = context.getAssets();
            if (assetManager != null) {
                try {
                    final String[] assetBundles = assetManager.list(assetFolderPath);
                    if (assetBundles != null) {
                        for (final String assetBundleFileName : assetBundles) {
                            if (LuaScriptManager.isLuaScriptBundle(assetBundleFileName)) {//如果是luaview bundle，则解包
                                new ScriptBundleUnpackTask(context).execute(FileUtil.removePostfix(assetBundleFileName), assetFolderPath + File.separator + assetBundleFileName);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
//        DebugUtil.tsi("lvperformance-unpackScripts");
        if (params != null && params.length > 0) {//一个参数，指定文件路径
            final String url = (String) params[0];
            final String scriptBundleFilePath = LuaScriptManager.buildScriptBundleFilePath(url);
            final InputStream inputStream = params.length > 1 ? AssetUtil.open(mContext, (String) params[1]) : FileUtil.open(scriptBundleFilePath);//额外参数，告知了inputstream (asset的情况)

            try {
                ScriptBundle result = ScriptBundle.unpackBundle(LuaScriptManager.isLuaBytecodeUrl(url), true, url, inputStream);
//                DebugUtil.tei("lvperformance-unpackScripts");
                return result;
            } catch (IOException e) {
//                DebugUtil.tei("lvperformance-unpackScripts");
                return null;
            } finally {
                if (params.length > 1) {//asset，copy原始文件到文件夹下
                    FileUtil.copy(AssetUtil.open(mContext, (String) params[1]), scriptBundleFilePath);
                }
            }
        }
//        DebugUtil.tei("lvperformance-unpackScripts");
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
                new ScriptBundleLoadTask(mContext, mScriptLoaderCallback).execute(bundle);
            } else {
                mScriptLoaderCallback.onScriptLoaded(null);
            }
        }
    }
}