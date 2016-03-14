package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.util.EncryptUtil;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.HexUtil;
import com.taobao.luaview.util.IOUtil;
import com.taobao.luaview.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * download lua script bundle from server and return saved path in local file system
 * @author song
 */
public class ScriptBundleDownloadTask extends AsyncTask<String, Integer, String> {
    private Context mContext;
    private LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;

    public ScriptBundleDownloadTask(final Context context, LuaScriptLoader.ScriptLoaderCallback callback) {
        mContext = context;
        mScriptLoaderCallback = callback;
    }

    /**
     * 接受两个参数，第一个是下载的url，第二个是存储的地址
     *
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            final String url = params[0];
            final URL uri = new URL(url);
            connection = (HttpURLConnection) uri.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LogUtil.e("[Server Returned HTTP] ", connection.getResponseCode(), connection.getResponseMessage());
                return null;
            }

            // download the file
            input = connection.getInputStream();
            String destFilePath = LuaScriptManager.buildScriptBundleFilePath(url);

            //check sha256
            final String sha256 = params.length > 2 ? params[2] : null;
            final byte[] fileData = IOUtil.toBytes(input);
            if (sha256 != null && !sha256.equalsIgnoreCase(HexUtil.bytesToHex(EncryptUtil.sha256(fileData)))) {//验证脚本的完整性
                return null;
            }

            File destFile = FileUtil.createFile(destFilePath);
            output = new FileOutputStream(destFile);
            output.write(fileData);

            return url;
        } catch (Exception e) {
            LogUtil.e("[Script Download Error] ", e);
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
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
        callLoaderCallback(url);
    }

    private void callLoaderCallback(String url) {
        if (url != null) {//如果下载保存成功，则进行解包操作（不论是否需要请求回调 ）
            new ScriptBundleUnpackTask(mContext, mScriptLoaderCallback).execute(url);
        } else if (mScriptLoaderCallback != null) {
            mScriptLoaderCallback.onScriptLoaded(null);
        }
    }
}