package com.taobao.luaview.global;

import android.content.Context;

import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.asynctask.ScriptBundleDownloadTask;
import com.taobao.luaview.scriptbundle.asynctask.ScriptBundleLoadTask;
import com.taobao.luaview.scriptbundle.asynctask.ScriptBundleUnpackTask;

/**
 * script loader
 *
 * @author song
 * @date 15/11/10
 */
public class LuaScriptLoader {
    private Context mContext;

    public LuaScriptLoader(final Context context) {
        this.mContext = context;
        LuaScriptManager.init(context);
    }

    /**
     * 脚本下载回调
     */
    public interface ScriptLoaderCallback {
        void onScriptLoaded(final ScriptBundle bundle);
    }

    //-----------------------------------------static methods---------------------------------------

    /**
     * 将asset目录下所有bundle包解压缩出来，一般在初始化的时候调用
     *
     * @param basePath
     */
    public void unpackAllAssetBundle(final String basePath) {
        if (basePath != null) {
            ScriptBundleUnpackTask.unpackAllAssetScripts(mContext, basePath);
        }
    }

    //--------------------------------lua script get and decode-------------------------------------

    /**
     * fetch a script from network (if needed) or from local file system
     *
     * @param url
     * @param callback
     */
    public void load(final String url, final ScriptLoaderCallback callback) {
        load(url, null, callback);
    }

    /**
     * fetch a script from network (if needed) or from local file system
     *
     * @param url
     * @param sha256
     * @param callback
     */
    public void load(final String url, final String sha256, final ScriptLoaderCallback callback) {
        if (LuaScriptManager.existsScriptBundle(url)) {//load local
            new ScriptBundleLoadTask(mContext, callback).execute(url);
        } else {
            new ScriptBundleDownloadTask(mContext, callback).execute(url, sha256);
        }
    }

}
