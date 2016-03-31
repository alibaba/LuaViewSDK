package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptFileNode;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 将下发的脚本bundle解压到本地文件系统
 * @author song
 * <p/>
 * 1. 将输入流根据(文件名,data)的格式解出到文件
 * 2. 将输入流保存的签名信息(文件名,sign)解出到文件
 */
public class ScriptBundleUnpackTask extends AsyncTask<Object, Integer, ArrayList<ScriptFileNode>> {
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
                                new ScriptBundleUnpackTask(context).execute(FileUtil.removePostfix(assetBundleFileName), assetManager.open(assetFolderPath + File.separator + assetBundleFileName));
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
        this.mContext = context;
        this.mScriptLoaderCallback = scriptLoaderCallback;
    }

    /**
     * 接受一个参数，加载的脚本根目录
     *
     * @param params
     * @return
     */
    @Override
    protected ArrayList<ScriptFileNode> doInBackground(Object... params) {
        if (params != null && params.length > 0) {//一个参数，指定文件路径
            final String url = (String) params[0];
            final String scriptBundleFilePath = LuaScriptManager.buildScriptBundleFilePath(url);
            final InputStream inputStream = params.length > 1 ? (InputStream) params[1] : FileUtil.open(scriptBundleFilePath);//额外参数，告知了inputstream (asset的情况)

            try {
                return unpackBundle(url, inputStream);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * unpack a bundle
     *
     * @param inputStream
     * @param url
     * @return
     */
    private static ArrayList<ScriptFileNode> unpackBundle(final String url, final InputStream inputStream) throws IOException {
        if (inputStream == null || url == null) {
            return null;
        }

        final ZipInputStream zipStream = new ZipInputStream(inputStream);
        final String scriptBundleFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);
        final Map<String, ScriptFileNode> luaScripts = new HashMap<String, ScriptFileNode>();
        final Map<String, byte[]> luaSigns = new HashMap<String, byte[]>();
        final byte[] buffer = new byte[8 * 1024];

        ZipEntry entry;
        while ((entry = zipStream.getNextEntry()) != null) {
            String fileName = entry.getName();
            String filePath = FileUtil.buildPath(scriptBundleFolderPath, fileName);

            if (entry.isDirectory()) {
                File dir = new File(filePath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
            } else {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                while ((len = zipStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                final byte[] fileData = out.toByteArray();
                FileUtil.save(filePath, fileData);
                LogUtil.i(fileName, fileData.length);

                if (LuaScriptManager.isLuaEncryptScript(fileName)) {
                    final ScriptFileNode node = new ScriptFileNode(fileName, fileData, null);
                    luaScripts.put(fileName, node);
                } else if (LuaScriptManager.isLuaSignFile(fileName)) {
                    final String scriptName = fileName.substring(0, fileName.length() - LuaScriptManager.POSTFIX_SIGN.length());
                    luaSigns.put(scriptName, fileData);
                }
            }
        }

        zipStream.close();

        final ArrayList<ScriptFileNode> result = new ArrayList<ScriptFileNode>();
        for (Map.Entry<String, ScriptFileNode> node : luaScripts.entrySet()) {
            node.getValue().signBytes = luaSigns.get(node.getValue().fileName);
            result.add(node.getValue());
        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<ScriptFileNode> files) {
        callLoaderCallback(files);
    }

    @Override
    protected void onCancelled() {
        callLoaderCallback(null);
    }

    @Override
    protected void onCancelled(ArrayList<ScriptFileNode> scriptFileNodes) {
        callLoaderCallback(scriptFileNodes);
    }

    private void callLoaderCallback(ArrayList<ScriptFileNode> files) {
        if (mScriptLoaderCallback != null) {
            if (files != null && files.size() > 0) {//启动loader task
                new ScriptBundleLoadTask(mContext, mScriptLoaderCallback).execute(files);
            } else {
                mScriptLoaderCallback.onScriptLoaded(null);
            }
        }
    }
}