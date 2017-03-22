/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.util.EncryptUtil;
import com.taobao.luaview.util.FileUtil;

import java.io.File;

/**
 * Lua脚本管理类
 *
 * @author song
 * @date 15/11/9
 */
public class LuaScriptManager {
    private static String PACKAGE_NAME;
    private static String BASE_FILECACHE_PATH;
    //folders
    private static final String PACKAGE_NAME_DEFAULT = "luaview";
    public static final String FOLDER_SCRIPT = "script";

    //默认缓存文件的后缀
    public static final String POSTFIX_SCRIPT_BUNDLE = ".lvraw";
    public static final String POSTFIX_JPG = ".jpg";
    public static final String POSTFIX_APK = ".apk";
    public static final String POSTFIX_PNG = ".png";
    public static final String POSTFIX_LOG = ".log";
    public static final String POSTFIX_LUA = ".lua";
    public static final String POSTFIX_B_LUA = ".blua";
    public static final String POSTFIX_LV = ".lv";//Lua加密脚本(source or bytecode)
    public static final String POSTFIX_LV_ZIP = ".zip";//lua的zip包
    public static final String POSTFIX_LV_BYTECODE_ZIP = ".bzip";//lua的二进制zip包
    public static final String POSTFIX_LV_STANDARD_SYNTAX_ZIP = ".szip";//标准语法的zip包
    public static final String POSTFIX_SIGN = ".sign";

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(final Context context) {
        if (TextUtils.isEmpty(BASE_FILECACHE_PATH) && context != null) {
            if (!LuaViewConfig.isDebug()) {//真实环境优先使用data/data目录
                initInternalFilePath(context);
            } else {//测试环境优先使用sd卡路径
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    PACKAGE_NAME = context.getPackageName();
                    BASE_FILECACHE_PATH = context.getExternalCacheDir() + File.separator;
                } else {
                    initInternalFilePath(context);
                }
            }
        }
    }

    /**
     * 初始化内部存储目录路径
     *
     * @param context
     */
    private static void initInternalFilePath(Context context) {
        final File dir = context.getDir(PACKAGE_NAME_DEFAULT, Context.MODE_PRIVATE);
        if (dir != null) {//优先存在 data/data/packagename/luaview
            PACKAGE_NAME = PACKAGE_NAME_DEFAULT;
            BASE_FILECACHE_PATH = dir.getPath() + File.separator;
        } else {
            PACKAGE_NAME = PACKAGE_NAME_DEFAULT;
            BASE_FILECACHE_PATH = context.getCacheDir() + File.separator;
        }
    }

    //--------------------------------static methods for get file path------------------------------

    /**
     * 获取基础文件路劲
     *
     * @return
     */
    public static String getBaseFilePath() {
        return BASE_FILECACHE_PATH + PACKAGE_NAME + File.separator;
    }

    /**
     * get scriptFolderPath
     *
     * @return
     */
    public static String getBaseScriptFolderPath() {
        return BASE_FILECACHE_PATH + PACKAGE_NAME + File.separator + FOLDER_SCRIPT + File.separator;
    }

    /**
     * get path of given folder
     *
     * @param subFolderName
     * @return
     */
    public static String getFolderPath(final String subFolderName) {
        return new StringBuffer()
                .append(getBaseScriptFolderPath())
                .append(subFolderName)
                .append(File.separator)
                .toString();
    }

    /**
     * get file path
     *
     * @param subFolderName
     * @param fileNameWithPostfix
     * @return
     */
    public static String getFilePath(final String subFolderName, final String fileNameWithPostfix) {
        return new StringBuffer()
                .append(getFolderPath(subFolderName))
                .append(fileNameWithPostfix)
                .toString();
    }

    /**
     * 构建文件名称
     *
     * @param nameWithoutPostfix 不带后缀的文件名称
     * @param postfixWithDot     带点的文件后缀
     * @return
     */
    public static String buildFileName(final String nameWithoutPostfix, final String postfixWithDot) {
        return new StringBuffer().append(nameWithoutPostfix).append(postfixWithDot).toString();
    }


    //------------------------------------------script function-------------------------------------

    /**
     * 根据Url构建ScriptBundle 的文件名称
     *
     * @param uri
     * @return
     */
    public static String buildScriptBundleFileName(final String uri) {
        return buildFileName(EncryptUtil.md5Hex(uri), LuaScriptManager.POSTFIX_SCRIPT_BUNDLE);
    }

    /**
     * 根据Url构建ScriptBundle的文件路径名称
     *
     * @param uri
     * @return
     */
    public static String buildScriptBundleFolderPath(final String uri) {
        final String fileNameWithoutPostfix = EncryptUtil.md5Hex(uri);
        final String folderName = fileNameWithoutPostfix;//使用文件名作为子目录的名称//new StringBuffer().append(FOLDER_SCRIPT).append(File.separator).append(fileNameWithoutPostfix).toString();
        return getFolderPath(folderName);
    }

    /**
     * 构建脚本文件文件名
     *
     * @param uri
     * @return
     */
    public static String buildScriptBundleFilePath(final String uri) {
        final String fileNameWithoutPostfix = EncryptUtil.md5Hex(uri);
        final String folderName = fileNameWithoutPostfix;//使用文件名作为子目录的名称//new StringBuffer().append(FOLDER_SCRIPT).append(File.separator).append(fileNameWithoutPostfix).toString();
        final String fileName = buildFileName(fileNameWithoutPostfix, POSTFIX_SCRIPT_BUNDLE);
        return getFilePath(folderName, fileName);
    }

    //------------------------------------------exists----------------------------------------------

    /**
     * Script Bundle 是否存在
     *
     * @param uri
     * @return
     */
    public static boolean existsScriptBundle(final String uri) {
        if (!TextUtils.isEmpty(uri)) {
            return FileUtil.exists(buildScriptBundleFilePath(uri));
        }
        return false;
    }

    //--------------------------------------------判断函数-------------------------------------------

    /**
     * 是否是lua脚本包
     *
     * @param fileName
     * @return
     */
    public static boolean isLuaScriptBundle(final String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_SCRIPT_BUNDLE);
    }

    /**
     * 是否是lua的zip包（zip，bzip，szip）
     *
     * @param fileName
     * @return
     */
    public static boolean isLuaScriptZip(final String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_LV_ZIP)
                || FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_LV_BYTECODE_ZIP)
                || FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_LV_STANDARD_SYNTAX_ZIP);
    }

    /**
     * 是否是lua 二进制zip包
     *
     * @param url
     * @return
     */
    public static boolean isLuaBytecodeUrl(final String url) {
        return FileUtil.isSuffix(url, LuaScriptManager.POSTFIX_LV_BYTECODE_ZIP);
    }

    public static boolean isLuaBytecodeFile(final String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_B_LUA);
    }

    /**
     * 是否是标准语法的 zip包
     *
     * @param url
     * @return
     */
    public static boolean isLuaStandardSyntaxUrl(final String url) {
        return FileUtil.isSuffix(url, LuaScriptManager.POSTFIX_LV_STANDARD_SYNTAX_ZIP);
    }

    /**
     * 是否是lua加密脚本
     *
     * @param fileName
     * @return
     */
    public static boolean isLuaEncryptScript(final String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_LV);
    }

    /**
     * 是否是普通lua文件
     *
     * @param fileName
     * @return
     */
    public static boolean isLuaScript(final String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_LUA);
    }

    /**
     * 签名文件
     *
     * @param fileName
     * @return
     */
    public static boolean isLuaSignFile(final String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_SIGN);
    }

    /**
     * 改变filename的名称
     *
     * @param fileName
     * @param newSuffix
     * @return
     */
    public static String changeSuffix(final String fileName, final String newSuffix) {
        if (fileName != null && fileName.lastIndexOf('.') != -1) {
            return fileName.substring(0, fileName.lastIndexOf('.')) + newSuffix;
        }
        return fileName;
    }

}
