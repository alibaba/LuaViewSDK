/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask.delegate;

import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.util.DebugUtil;
import com.taobao.luaview.util.EncryptUtil;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.HexUtil;
import com.taobao.luaview.util.IOUtil;
import com.taobao.luaview.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ScriptBundle Download Delegate
 *
 * @author song
 * @date 17/2/8
 * 主要功能描述
 * 修改描述
 * 下午2:42 song XXX
 */

public class ScriptBundleDownloadDelegate {
    private String url;
    private String sha256;

    public ScriptBundleDownloadDelegate(String url, String sha256) {
        this.url = url;
        this.sha256 = sha256;
    }

    /**
     * download ScriptBundle
     *
     * @return
     */
    public boolean download() {
        byte[] fileData = downloadAsBytes();

        if (fileData != null) {//save file
            DebugUtil.tsi("luaviewp-saveFiles0");

            saveFile(fileData);

            DebugUtil.tei("luaviewp-saveFiles0");

            return true;
        }

        return false;
    }

    public void saveFile(byte[] fileData){
        String destFilePath = LuaScriptManager.buildScriptBundleFilePath(url);
        File destFile = FileUtil.createFile(destFilePath);
        saveFileUsingFileOutputStream(destFile, fileData);
    }

    /**
     * download as bytes
     *
     * @return
     */
    public byte[] downloadAsBytes() {
        HttpURLConnection connection = createHttpUrlConnection();
        if (connection != null) {
            InputStream input = downloadAsStream(connection);
            if (input != null) {
                DebugUtil.tsi("luaviewp-readBytes");
                final byte[] fileData = IOUtil.toBytes(input);//TODO 有性能瓶颈，可以考虑安全验证
                DebugUtil.tei("luaviewp-readBytes");

                DebugUtil.tsi("luaviewp-sha256");
                if (!checkSha256(fileData)) {
                    return null;
                }
                DebugUtil.tei("luaviewp-sha256");

                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                connection.disconnect();

                return fileData;
            }
        }
        return null;
    }

    /**
     * create HttpURLConnection
     *
     * @return
     */
    public HttpURLConnection createHttpUrlConnection() {
        try {

            final URL uri = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.connect();

            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * download as Stream
     *
     * @param connection
     * @return
     */
    public InputStream downloadAsStream(HttpURLConnection connection) {
        try {
            if (connection != null) {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    LogUtil.e("[Server Returned HTTP] ", connection.getResponseCode(), connection.getResponseMessage());
                    return null;
                }
                return new BufferedInputStream(connection.getInputStream());
            }
            return null;
        } catch (Exception e) {
            LogUtil.e("[Script Download Error] ", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * check sha256
     *
     * @param fileData
     * @return
     */
    public boolean checkSha256(byte[] fileData) {
        if (sha256 != null && !sha256.equalsIgnoreCase(HexUtil.bytesToHex(EncryptUtil.sha256(fileData)))) {//验证脚本的完整性
            return false;
        }
        return true;
    }

    /**
     * save file using output stream
     *
     * @param destFile
     * @param fileData
     */
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
}
