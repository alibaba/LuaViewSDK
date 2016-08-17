package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.taobao.luaview.cache.AppCache;
import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.util.DecryptUtil;
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
public class ScriptBundleLoadTask extends AsyncTask<Object, Integer, ScriptBundle> {
    private static final String TAG = ScriptBundleLoadTask.class.getSimpleName();
    private Context mContext;

    //加载的脚本
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;
    private String mDestFolderPath;

    //------------------------------------------加载脚本函数------------------------------------------

    /**
     * load a prototype
     *
     * @param context
     * @param scriptFile
     * @return
     */
    private Prototype loadPrototype(final Context context, final ScriptFile scriptFile) {
        if (LoadState.instance != null && scriptFile != null) {
            try {
                return LoadState.instance.undump(new ByteArrayInputStream(scriptFile.scriptData), scriptFile.getFilePath());
            } catch (LuaError error) {
                error.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 加载一个脚本
     *
     * @param context
     * @return
     */
    public static InputStream loadEncryptScript(final Context context, final InputStream inputStream) {
        if (inputStream != null) {
            InputStream result = new ByteArrayInputStream(ZipUtil.unzip(DecryptUtil.aes(context, IOUtil.toBytes(inputStream))));
            try {
                inputStream.close();
            } catch (Exception e) {//close input, 这里需要注意，外面不能使用该inputStream
            }
            return result;
        }
        return inputStream;
    }

    /**
     * 加载加密过的脚本
     *
     * @param context
     * @param scriptFile
     * @return
     */
    public static byte[] loadEncryptScript(final Context context, final boolean isBytecode, final ScriptFile scriptFile) {
        if (scriptFile != null) {
            if (isBytecode) {//bytecode不进行unzip
                if (scriptFile.signData != null && scriptFile.signData.length > 0) {//加密过则进行解密并unzip
                    return DecryptUtil.aes(context, scriptFile.scriptData);
                } else {
                    return scriptFile.scriptData;
                }
            } else {
                if (scriptFile.signData != null && scriptFile.signData.length > 0) {//加密过则进行解密并unzip
                    return ZipUtil.unzip(DecryptUtil.aes(context, scriptFile.scriptData));
                } else {
                    return ZipUtil.unzip(scriptFile.scriptData);
                }
            }
        }
        return null;
    }

    /**
     * 加载加密过的脚本
     *
     * @param context
     * @param script
     * @return
     */
    public static byte[] loadEncryptScript(final Context context, final byte[] script) {
        return ZipUtil.unzip(DecryptUtil.aes(context, script));
    }

    /**
     * 验证所有脚本，有任何一个失败则返回失败
     *
     * @param bundle
     * @return
     */
    private static boolean verifyAllScripts(Context context, ScriptBundle bundle) {
        Map<String, ScriptFile> files = bundle != null ? bundle.getScriptFileMap() : null;
        if (files != null) {
            for (final String key : files.keySet()) {
                if (verifyScript(context, bundle.isBytecode(), files.get(key)) == false) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证一个脚本
     *
     * @param isBytecode
     * @param script
     * @return
     */
    private static boolean verifyScript(Context context, boolean isBytecode, ScriptFile script) {
        if (script != null) {
            if (isBytecode) {//bytecode 模式下，如果没有signdata也算验证通过
                if (script.signData != null && script.signData.length > 0) {
                    return VerifyUtil.rsa(context, script.scriptData, script.signData);
                }
                return true;
            } else {
                return VerifyUtil.rsa(context, script.scriptData, script.signData);
            }
        }
        return false;
    }


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
        if (scriptBundle == null) {//不是下载即加载的情况
            mDestFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);
            String scriptBundleFilePath = LuaScriptManager.buildScriptBundleFilePath(url);

            scriptBundle = AppCache.getCache(TAG).get(url);
            if (scriptBundle != null) {
                return scriptBundle;
            } else {
                scriptBundle = ScriptBundle.loadBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, scriptBundleFilePath);
            }
        }

        if (scriptBundle != null && scriptBundle.size() > 0 && verifyAllScripts(mContext, scriptBundle)) {//强校验，如果脚本存在并且校验成功才返回

            Map<String, ScriptFile> files = scriptBundle.getScriptFileMap();
            ScriptFile scriptFile = null;
            for (String key : files.keySet()) {
                scriptFile = files.get(key);
                scriptFile.scriptData = loadEncryptScript(mContext, scriptBundle.isBytecode(), scriptFile);
                if (scriptBundle.isBytecode()) {//如果是bytecode，则加载prototype
                    scriptFile.prototype = loadPrototype(mContext, scriptFile);
                }
            }

            scriptBundle.setUrl(url);
            scriptBundle.setBaseFilePath(mDestFolderPath);

            //cache
            AppCache.getCache(TAG).put(url, scriptBundle);
            return scriptBundle;
        } else {
            return null;
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
        callLoaderCallback(unzippedScripts);
    }

    private void callLoaderCallback(ScriptBundle unzippedScripts) {
        if (mScriptLoaderCallback != null) {
            mScriptLoaderCallback.onScriptLoaded(unzippedScripts);
        }
    }
}
