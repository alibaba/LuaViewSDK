package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptFileNode;
import com.taobao.luaview.util.FileUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
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
            return unpackBundle(url, inputStream);
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
    private static ArrayList<ScriptFileNode> unpackBundle(final String url, final InputStream inputStream) {
        final ArrayList<ScriptFileNode> result = new ArrayList<ScriptFileNode>();
        if (inputStream != null) {
            //read and save to multiple file
            final DataInputStream dataInputStream = new DataInputStream(inputStream);
            try {
                dataInputStream.readInt();//tag
                dataInputStream.readInt();//version
                dataInputStream.readUTF();//时间

                //1. 读取文件信息
                final Map<String, ScriptFileNode> luaScripts = new HashMap<String, ScriptFileNode>();
                final int fileCount = dataInputStream.readInt();//file count
                final String scriptBundleFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);
                for (int i = 0; i < fileCount; i++) {
                    //1.1 读取文件
                    String fileName = dataInputStream.readUTF();
                    int fileLength = dataInputStream.readInt();
                    byte[] fileData = new byte[fileLength];
                    dataInputStream.read(fileData);//file data, lua script (is encrypted) or resource (raw data)

                    //1.2 保存文件
                    final ScriptFileNode fileNode = new ScriptFileNode(fileName, fileData, null);
                    final String filePath = FileUtil.buildPath(scriptBundleFolderPath, fileName);//save file
                    FileUtil.save(filePath, fileData);

                    //1.3 添加到返回数据中(有需要处理返回值的情况才处理)
                    if (fileNode.isLuaScript) {
                        luaScripts.put(fileName, fileNode);
                    }
                }

                //2. 读取签名信息
                dataInputStream.readInt();//签名信息数组长度
                int signCount = dataInputStream.readInt();//签名信息个数
                for (int i = 0; i < signCount; i++) {
                    //2.1 读取签名文件
                    String fileName = dataInputStream.readUTF();
                    int signLength = dataInputStream.readInt();
                    byte[] signData = new byte[signLength];
                    dataInputStream.read(signData);

                    //2.2 保存签名文件
                    final String signFilePath = FileUtil.buildPath(scriptBundleFolderPath, fileName + LuaScriptManager.POSTFIX_SIGN).toString();//sign file
                    FileUtil.save(signFilePath, signData);

                    //2.3 将签名信息存到返回值里
                    if (LuaScriptManager.isLuaEncryptScript(fileName)) {
                        ScriptFileNode fileNode = luaScripts.get(fileName);
                        fileNode.signBytes = signData;
                    }
                }

                dataInputStream.readInt();// tag

                //3. 将结果保存到result中
                for (Map.Entry<String, ScriptFileNode> entry : luaScripts.entrySet()) {
                    result.add(entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    dataInputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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