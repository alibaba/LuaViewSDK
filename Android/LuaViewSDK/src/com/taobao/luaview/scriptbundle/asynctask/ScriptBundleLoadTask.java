package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.scriptbundle.ScriptFileNode;
import com.taobao.luaview.util.DecryptUtil;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.IOUtil;
import com.taobao.luaview.util.VerifyUtil;
import com.taobao.luaview.util.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 从文件系统中读取lua 脚本文件和签名文件，并做签名验证，验证成功后，返回unzip的脚本数据
 *
 * @author song
 * @date 15/11/9
 * <p/>
 * 1. 从文件读取给定目录下的所有脚本文件以及对应的签名文件，读取的同时做签名校验(AES256)
 * 2. 签名验证成功的文件，做unzip操作，获取需要返回的数据
 */
public class ScriptBundleLoadTask extends AsyncTask<Object, Integer, ScriptBundle> {
    private Context mContext;
    //加载的脚本
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;
    private String mDestFolderPath;

    //------------------------------------------加载脚本函数------------------------------------------

    /**
     * 加载一个脚本
     *
     * @param context
     * @param scriptFilePath
     * @return
     */
    public static InputStream loadEncryptScript(final Context context, final String scriptFilePath) {
        final InputStream inputStream = FileUtil.open(scriptFilePath);
        if (inputStream != null) {
            final InputStream result = new ByteArrayInputStream(ZipUtil.unzip(DecryptUtil.aes(context, IOUtil.toBytes(inputStream))));
            try {
                inputStream.close();
            } catch (Exception e) {//close input
            }
            return result;
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
     * @param script
     * @return
     */
    public static byte[] loadEncryptScript(final Context context, final byte[] script) {
        return ZipUtil.unzip(DecryptUtil.aes(context, script));
    }

    /**
     * 验证并加载一个脚本
     *
     * @param context
     * @param script
     * @param signBytes
     * @return
     */
    public static byte[] verifyAndLoadEncryptScript(final Context context, byte[] script, byte[] signBytes) {
        if (!verifyScript(context, script, signBytes)) {//有任意一个文件验证失败，返回null
            return null;
        } else {
            return ZipUtil.unzip(DecryptUtil.aes(context, script));
        }
    }

    /**
     * 验证所有脚本，有任何一个失败则返回失败
     *
     * @param scripts
     * @return
     */
    private static boolean verifyAllScripts(Context context, ArrayList<ScriptFileNode> scripts) {
        if (scripts != null) {
            for (final ScriptFileNode script : scripts) {
                if (verifyScript(context, script) == false) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证一个脚本
     *
     * @param script
     * @return
     */
    private static boolean verifyScript(Context context, ScriptFileNode script) {
        return script != null && VerifyUtil.rsa(context, script.bytes, script.signBytes);
    }

    /**
     * 验证一个脚本
     *
     * @param script
     * @return
     */
    private static boolean verifyScript(Context context, byte[] script, byte[] signBytes) {
        return VerifyUtil.rsa(context, script, signBytes);
    }


    public ScriptBundleLoadTask(Context context, LuaScriptLoader.ScriptLoaderCallback scriptLoaderCallback) {
        this.mContext = context;
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
        ArrayList<ScriptFileNode> luaScripts = (params != null && params.length > 0 && params[0] instanceof ArrayList) ? (ArrayList<ScriptFileNode>) params[0] : null;//首先判断脚本解析的列表是否存在（下载即加载的情形）

        if (luaScripts == null || luaScripts.size() == 0) {//下载不存在则读取文件再加载
            final String url = (params != null && params.length > 0 && params[0] instanceof String) ? (String) params[0] : null;
            mDestFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);
            luaScripts = loadScriptsOfPath(mDestFolderPath);
        }

        if (luaScripts != null && luaScripts.size() > 0 && verifyAllScripts(mContext, luaScripts)) {//强校验，如果脚本存在并且校验成功才返回
            for (final ScriptFileNode script : luaScripts) {
                script.bytes = loadEncryptScript(mContext, script.bytes);
            }

            final ScriptBundle result = new ScriptBundle();
            result.setBaseFilePath(mDestFolderPath);
            for (final ScriptFileNode script : luaScripts) {
                if (script.bytes != null) {
                    result.addScript(new ScriptFile(script.fileName, new String(script.bytes)));
                }
            }
            return result;
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

    /**
     * 加载指定目录下的所有lua文件
     *
     * @param destFilePath path or file
     * @return
     */
    private ArrayList<ScriptFileNode> loadScriptsOfPath(final String destFilePath) {
        ArrayList<ScriptFileNode> result = new ArrayList<ScriptFileNode>();
        if (destFilePath != null) {
            final File file = new File(destFilePath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    final File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        HashMap<String, byte[]> dataFiles = new HashMap<String, byte[]>();
                        HashMap<String, byte[]> signFiles = new HashMap<String, byte[]>();
                        for (final File f : files) {
                            String fileName = f.getName();
                            if (LuaScriptManager.isLuaEncryptScript(fileName)) {//lua加密脚本
                                dataFiles.put(fileName, FileUtil.readBytes(f));
                            } else if (LuaScriptManager.isLuaSignFile(fileName)) {//sign文件
                                signFiles.put(fileName, FileUtil.readBytes(f));
                            }
                        }

                        //根据读取的数据构建出文件node
                        for (Map.Entry<String, byte[]> entry : dataFiles.entrySet()) {
                            final String fileName = entry.getKey();
                            final String signFileName = fileName + LuaScriptManager.POSTFIX_SIGN;
                            byte[] dataBytes = entry.getValue();
                            byte[] signBytes = signFiles.get(signFileName);
                            result.add(new ScriptFileNode(fileName, dataBytes, signBytes));
                        }
                    }
                } else if (file.isFile()) {
                    return loadScriptsOfPath(file.getParent());
                }
            }
        }
        return result;
    }
}
