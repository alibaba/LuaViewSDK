/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask.delegate;

import android.content.Context;
import android.content.res.AssetManager;

import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.util.AssetUtil;
import com.taobao.luaview.util.DebugUtil;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.IOUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ScriptBundle Unpack Delegate
 *
 * @author song
 * @date 17/2/8
 * 主要功能描述
 * 修改描述
 * 下午2:43 song XXX
 */

public class ScriptBundleUnpackDelegate {

    /**
     * 将所有assert下面的脚本解压缩到文件系统
     *
     * @param assetFolderPath
     */
    public static void unpackAllAssetScripts(final Context context, final String assetFolderPath) {
        if (context != null && assetFolderPath != null) {
            new SimpleTask1<Object>() {//起simple task 来解压包
                @Override
                protected Object doInBackground(Object[] params) {
                    final AssetManager assetManager = context.getAssets();
                    if (assetManager != null) {
                        try {
                            final String[] assetBundles = assetManager.list(assetFolderPath);//list 耗时
                            if (assetBundles != null) {
                                for (final String assetBundleFileName : assetBundles) {
                                    if (LuaScriptManager.isLuaScriptZip(assetBundleFileName)) {//如果是luaview zip，则解包
                                        unpack(context, FileUtil.removePostfix(assetBundleFileName), assetFolderPath + File.separator + assetBundleFileName);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            }.executeInPool();
        }
    }

    /**
     * unpack asset
     *
     * @param url
     * @param assetFilePath
     * @return
     */
    public static ScriptBundle unpack(Context context, String url, String assetFilePath) {
        final String scriptBundleFilePath = LuaScriptManager.buildScriptBundleFilePath(url);
        final InputStream inputStream = assetFilePath != null ? AssetUtil.open(context, assetFilePath) : FileUtil.open(scriptBundleFilePath);//额外参数，告知了inputstream (asset的情况)
        try {
            return unpackBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, inputStream);//TODO 性能瓶颈
        } catch (IOException e) {
            return null;
        } finally {
            if (assetFilePath != null) {//asset，copy原始文件到文件夹下
                FileUtil.copy(AssetUtil.open(context, assetFilePath), scriptBundleFilePath);
            }
        }
    }

    /**
     * unpack asset
     *
     * @param url
     * @return
     */
    public static ScriptBundle unpack(String url) {
        final String scriptBundleFilePath = LuaScriptManager.buildScriptBundleFilePath(url);
        final InputStream inputStream = FileUtil.open(scriptBundleFilePath);//额外参数，告知了inputstream (asset的情况)
        try {
            return unpackBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, inputStream);//TODO 性能瓶颈
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * unpack asset
     *
     * @param url
     * @return
     */
    public static ScriptBundle unpack(String url, byte[] fileData) {
        try {
            return unpackBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, new ByteArrayInputStream(fileData));//TODO 性能瓶颈
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * unpack asset
     *
     * @param url
     * @return
     */
    public static ScriptBundle unpack(String url, InputStream inputStream) {
        try {
            return unpackBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, inputStream);//TODO 性能瓶颈
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 加载指定目录下的所有lua文件
     *
     * @param destFilePath path or file
     * @return
     */
    public static ScriptBundle loadBundle(boolean isBytecode, final String url, String destFilePath) {
        String rawFilePath = LuaScriptManager.buildScriptBundleFilePath(url);
        File file = new File(rawFilePath);
        if (!file.exists()) {
            return null;
        }

        if (file.isFile()) {
            try {
                return unpackBundleRaw(isBytecode, url, file);
            } catch (Exception e) {
                e.printStackTrace();
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
    public static ScriptBundle unpackBundle(final boolean isBytecode, final String url, final InputStream inputStream) throws IOException {
        if (inputStream == null || url == null) {
            return null;
        }

        final ScriptBundle scriptBundle = new ScriptBundle();

        final ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(inputStream));
        final String scriptBundleFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);
        final Map<String, byte[]> luaSigns = new HashMap<String, byte[]>();
        final Map<String, byte[]> luaRes = new HashMap<String, byte[]>();
        final Map<String, byte[]> luaScripts = new HashMap<String, byte[]>();

        scriptBundle.setUrl(url);
        scriptBundle.setBytecode(isBytecode);
        scriptBundle.setBaseFilePath(scriptBundleFolderPath);

        ZipEntry entry;
        String rawName = null;
        String fileName = null;
        String filePath = null;

        DebugUtil.tsi("luaviewp-unpackBundle-zip");
        while ((entry = zipStream.getNextEntry()) != null) {
            // 处理../ 这种方式只能使用单层路径，不能处理子目录，在这里可以添加公用path
            rawName = entry.getName();
            if (rawName == null || rawName.indexOf("../") != -1) {
                zipStream.close();
                return null;
            }

            fileName = FileUtil.getSecurityFileName(rawName);

            if (entry.isDirectory()) {
                filePath = FileUtil.buildPath(scriptBundleFolderPath, fileName);
                File dir = new File(filePath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
            } else {

                byte[] fileData = IOUtil.toBytes(zipStream);//TODO 性能瓶颈

                if (LuaScriptManager.isLuaEncryptScript(fileName)) {//lua file (source or prototype)
                    scriptBundle.addScript(new ScriptFile(url, scriptBundleFolderPath, fileName, fileData, null));
                    luaScripts.put(fileName, fileData);
                } else if (LuaScriptManager.isLuaSignFile(fileName)) {//签名文件，不解压到文件系统
                    luaSigns.put(fileName, fileData);
                } else {//其他文件解压到文件系统(图片、资源)
                    filePath = FileUtil.buildPath(scriptBundleFolderPath, fileName);
                    luaRes.put(filePath, fileData);
                }
            }

            //close entry
            zipStream.closeEntry();
        }
        DebugUtil.tei("luaviewp-unpackBundle-zip");

        zipStream.close();

        ScriptFile scriptFile = null;
        Map<String, ScriptFile> fileMap = scriptBundle.getScriptFileMap();
        for (String key : fileMap.keySet()) {
            scriptFile = fileMap.get(key);
            scriptFile.signData = luaSigns.get(scriptFile.signFileName);
        }

        if (luaRes.size() > 0) {
            new SimpleTask<Map<String, byte[]>>() {//写资源文件
                @Override
                public void doTask(Map<String, byte[]>... params) {
                    if (params != null && params.length > 0) {
                        Map<String, byte[]> fileToBeSave = params[0];
                        for (Map.Entry<String, byte[]> file : fileToBeSave.entrySet()) {//save all res files
                            FileUtil.save(file.getKey(), file.getValue());
                        }
                    }
                }
            }.executeInPool(luaRes);
        }

        if (luaScripts.size() > 0) {
            new SimpleTask<Object>() {//写xxx.lvbundle文件
                @Override
                public void doTask(Object... params) {
                    boolean isBytecode = params != null && params.length >= 0 ? (Boolean) params[0] : false;
                    Map<String, byte[]> fileData = params != null && params.length >= 1 ? (Map<String, byte[]>) params[1] : null;
                    Map<String, byte[]> signData = params != null && params.length >= 2 ? (Map<String, byte[]>) params[2] : null;
                    saveFilesUsingOutputStream(isBytecode, url, fileData, signData);
                }
            }.executeInPool(isBytecode, luaScripts, luaSigns);
        }

        return scriptBundle;
    }

    /**
     * save files as xxx.lvbundle
     *
     * @param url
     * @param fileDatas
     */
    private static void saveFilesUsingOutputStream(boolean isBytecode, String url, Map<String, byte[]> fileDatas, Map<String, byte[]> signDatas) {
        String filePath = LuaScriptManager.buildScriptBundleFilePath(url);
        File tmpFile = FileUtil.createFile(filePath);
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(tmpFile));
            output.writeInt(fileDatas.size());
            String fileNameStr = null;
            for (Map.Entry<String, byte[]> file : fileDatas.entrySet()) {
                fileNameStr = file.getKey();
                byte[] fileName = fileNameStr.getBytes();

                output.writeInt(fileName.length);
                output.write(fileName);

                byte[] fileData = file.getValue();
                output.writeInt(fileData.length);
                output.write(fileData);

                if (!isBytecode) {//非bytecode还写sign
                    byte[] signData = signDatas.get(LuaScriptManager.changeSuffix(fileNameStr, LuaScriptManager.POSTFIX_LV) + LuaScriptManager.POSTFIX_SIGN);//sign file name, TODO 这里可以优化一下，名称使用统一的名称
                    output.writeInt(signData.length);
                    output.write(signData);
                }
            }
        } catch (Exception e) {
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
     * unpack a xxx.lvbundle
     *
     * @param file
     * @param url
     * @return
     */
    public static ScriptBundle unpackBundleRaw(boolean isBytecode, final String url, final File file) throws IOException {
        DebugUtil.tsi("luaviewp-unpackBundle-raw");

        if (file == null || url == null) {
            return null;
        }

        final ScriptBundle scriptBundle = new ScriptBundle();
        final FileInputStream inputStream = new FileInputStream(file);
        final MappedByteBuffer buffer = inputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        final String scriptBundleFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);

        scriptBundle.setUrl(url);
        scriptBundle.setBytecode(isBytecode);
        scriptBundle.setBaseFilePath(scriptBundleFolderPath);

        int fileNameLen = 0;
        int fileLen = 0;
        String fileNameStr = null;

        byte[] fileName, fileData, signData;
        int count = buffer.getInt();
        for (int i = 0; i < count; i++) {//all files
            fileNameLen = buffer.getInt();// get file name
            fileName = new byte[fileNameLen];
            buffer.get(fileName);

            fileLen = buffer.getInt();// get file data
            fileData = new byte[fileLen];
            buffer.get(fileData);

            if (!isBytecode) {//非二进制的还有sign的data
                fileLen = buffer.getInt();
                signData = new byte[fileLen];
                buffer.get(signData);
            } else {
                signData = null;
            }

            fileNameStr = new String(fileName);
            if (fileNameStr == null || fileNameStr.indexOf("../") != -1) {
                return null;
            }

            scriptBundle.addScript(new ScriptFile(url, scriptBundleFolderPath, fileNameStr, fileData, signData));
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DebugUtil.tei("luaviewp-unpackBundle-raw");

        return scriptBundle;
    }
}
