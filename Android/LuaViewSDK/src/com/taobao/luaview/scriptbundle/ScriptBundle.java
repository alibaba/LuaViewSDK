package com.taobao.luaview.scriptbundle;

import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 脚本文件包，每一个加载的脚本包，加载成功后返回该bundle
 *
 * @author song
 * @date 15/11/10
 */
public class ScriptBundle {
    //脚本网络地址
    private String mUrl;

    //脚本本地地址
    private String mBaseFilePath;

    //脚本文件
    private HashMap<String, ScriptFile> mScriptFileMap;

    //是否是bytecode
    private boolean isBytecode;

    //config，TODO 可以根据脚本包里的配置来控制虚拟机的运行
    private Properties mProps;

    public ScriptBundle() {
        mScriptFileMap = new HashMap<String, ScriptFile>();
    }

    public ScriptBundle addScript(ScriptFile scriptFile) {
        if (mScriptFileMap != null) {
            mScriptFileMap.put(scriptFile.fileName, scriptFile);
        }
        return this;
    }

    public int size() {
        return mScriptFileMap != null ? mScriptFileMap.size() : 0;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setBytecode(boolean isBytecode) {
        this.isBytecode = isBytecode;
    }

    public void setBaseFilePath(String mBaseFilePath) {
        this.mBaseFilePath = mBaseFilePath;
    }

    public String getBaseFilePath() {
        return mBaseFilePath;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isBytecode() {
        return isBytecode;
    }

    public Map<String, ScriptFile> getScriptFileMap() {
        return mScriptFileMap;
    }

    public boolean containsKey(final String key) {
        return mScriptFileMap != null && mScriptFileMap.containsKey(key);
    }

    public ScriptFile getScriptFile(final String key) {
        return mScriptFileMap != null ? mScriptFileMap.get(key) : null;
    }

    public void saveToFile(final String path) {
        if (path != null) {
            if (mScriptFileMap != null) {
                new SimpleTask1<Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        ScriptFile file = null;
                        for (String key : mScriptFileMap.keySet()) {
                            file = mScriptFileMap.get(key);
                            if (file != null) {
                                FileUtil.save(path + "/" + file.fileName, file.scriptData);
                            }
                        }
                        return null;
                    }
                }.execute();

            }
        }
    }

    /**
     * 加载指定目录下的所有lua文件
     *
     * @param destFilePath path or file
     * @return
     */
    public static ScriptBundle loadBundle(boolean isBytecode, final String url, final String destFilePath) {
        final File file = destFilePath != null ? new File(destFilePath) : null;
        if (file == null || !file.exists()) {
            return null;
        }

        if (isBytecode) {//bytecode，直接从.lvbundle中加载
            if (file.isFile()) {
                try {
                    return unpackBundle(true, false, url, new FileInputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } else {//源码文件，从文件夹下加载
            if (file.isDirectory()) {
                final File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    HashMap<String, byte[]> dataFiles = new HashMap<String, byte[]>();
                    HashMap<String, byte[]> signFiles = new HashMap<String, byte[]>();

                    String fileName;//file name
                    for (final File f : files) {
                        fileName = f.getName();
                        if (LuaScriptManager.isLuaEncryptScript(fileName)) {//lua加密脚本
                            dataFiles.put(fileName, FileUtil.readBytes(f));
                        } else if (LuaScriptManager.isLuaSignFile(fileName)) {//sign文件
                            signFiles.put(fileName, FileUtil.readBytes(f));
                        }
                    }

                    //根据读取的数据构建出文件node
                    ScriptBundle result = new ScriptBundle();

                    result.setUrl(url);
                    result.setBytecode(false);
                    result.setBaseFilePath(destFilePath);

                    String signFileName;//sign name
                    for (Map.Entry<String, byte[]> entry : dataFiles.entrySet()) {
                        fileName = entry.getKey();
                        signFileName = fileName + LuaScriptManager.POSTFIX_SIGN;
                        result.addScript(new ScriptFile(url, destFilePath, fileName, entry.getValue(), signFiles.get(signFileName)));
                    }
                    return result;
                }
            } else if (file.isFile()) {
                return loadBundle(false, url, file.getParent());
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
    public static ScriptBundle unpackBundle(boolean isBytecode, boolean saveFile, final String url, final InputStream inputStream) throws IOException {
        if (inputStream == null || url == null) {
            return null;
        }

        final ScriptBundle scriptBundle = new ScriptBundle();
        final ZipInputStream zipStream = new ZipInputStream(inputStream);
        final String scriptBundleFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);
        final Map<String, byte[]> luaSigns = new HashMap<String, byte[]>();
        final byte[] buffer = new byte[8 * 1024];

        scriptBundle.setUrl(url);
        scriptBundle.setBytecode(isBytecode);
        scriptBundle.setBaseFilePath(scriptBundleFolderPath);

        ZipEntry entry;
        String fileName = null;
        String filePath = null;
        while ((entry = zipStream.getNextEntry()) != null) {
            // 处理../ 这种方式只能使用单层路径，不能处理子目录，在这里可以添加公用path
            String szName = entry.getName();
            if(szName == null || szName.indexOf("../") != -1){
                zipStream.close();
                return null;
            }

            fileName = FileUtil.getSecurityFileName(szName);

            if (saveFile && entry.isDirectory()) {
                filePath = FileUtil.buildPath(scriptBundleFolderPath, fileName);
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

                boolean shouldSaveFile = false;
                if (LuaScriptManager.isLuaEncryptScript(fileName)){//lua file (source or prototype)
                    scriptBundle.addScript(new ScriptFile(url, scriptBundleFolderPath, fileName, fileData, null));
                    shouldSaveFile = !isBytecode;
                } else if (LuaScriptManager.isLuaSignFile(fileName)) {//签名文件，不解压到文件系统
                    luaSigns.put(fileName, fileData);
                    shouldSaveFile = !isBytecode;//非bytecode模式下签名文件存起来
                } else {//其他文件解压到文件系统(图片、资源)
                    shouldSaveFile = true;
                }

                if (shouldSaveFile && saveFile) {//存且应该存
                    filePath = FileUtil.buildPath(scriptBundleFolderPath, fileName);
                    FileUtil.save(filePath, fileData);
                }
            }
        }

        zipStream.close();


        ScriptFile scriptFile = null;
        for (String key : scriptBundle.mScriptFileMap.keySet()) {
            scriptFile = scriptBundle.mScriptFileMap.get(key);
            scriptFile.signData = luaSigns.get(scriptFile.signFileName);
        }
        return scriptBundle;
    }
}
