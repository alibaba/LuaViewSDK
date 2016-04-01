package com.taobao.luaview.global;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.scriptbundle.asynctask.ScriptBundleLoadTask;
import com.taobao.luaview.util.AssetUtil;
import com.taobao.luaview.util.DrawableUtil;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.util.ParamUtil;
import com.taobao.luaview.util.TypefaceUtil;

import org.luaj.vm2.lib.ResourceFinder;

import java.io.InputStream;


/**
 * 给定相对路径，找对应的资源（脚本、图片、字体等)
 *
 * @author song
 * @date 16/1/5
 */
public class LuaResourceFinder implements ResourceFinder {
    public static final String DEFAULT_MAIN_ENTRY = "main.lua";//默认的脚本入口，在加载folder或者bundle的时候会默认加载该名称的脚本
    private static final String FILE_PATH_ANDROID_ASSET = "file:///android_asset/";

    private ScriptBundle mScriptBundle;
    //内存脚本
    private Context mContext;
    //加载的uri（可以是url，也可以是包名称）
    private String mUri;
    //基础scriptPath
    private String mBasePath;//文件系统路径
    private String mBaseAssetPath;//asset下路径

    public LuaResourceFinder(Context context) {
        this.mContext = context;
    }

    public void setScriptBundle(ScriptBundle scriptBundle) {
        mScriptBundle = scriptBundle;
    }

    public void setUri(String uri) {
        mUri = uri;
        mBasePath = LuaScriptManager.buildScriptBundleFolderPath(uri);
        mBaseAssetPath = FileUtil.getAssetFolderPath(uri);//脚本默认放在asset目录下
    }

    /**
     * find Script in given name or path
     *
     * @param nameOrPath
     * @return
     */
    @Override
    public InputStream findResource(final String nameOrPath) {
        if (mScriptBundle != null && mScriptBundle.containsKey(nameOrPath)) {//from cache, 这里直接使用文件名称来处理
            LogUtil.d("[findResource-ScriptBundle]", nameOrPath);
            final ScriptFile scriptFile = mScriptBundle.getScriptFile(nameOrPath);
            return scriptFile.getInputStream();
        }

        if (LuaScriptManager.isLuaEncryptScript(nameOrPath)) {//.lv
            return ScriptBundleLoadTask.loadEncryptScript(mContext, findFile(nameOrPath));
        } else {//.lua 或者 输入folder名字（其实是lvbundle的名字，如ppt440), 则加载 main.lua
            final String newName = LuaScriptManager.isLuaScript(nameOrPath) ? nameOrPath : DEFAULT_MAIN_ENTRY;//如果是脚本则加载，否则加载main.lua
            InputStream inputStream = ScriptBundleLoadTask.loadEncryptScript(mContext, findFile(LuaScriptManager.changeSuffix(newName, LuaScriptManager.POSTFIX_LV)));
            if (inputStream == null) {//如果.lv不存在，则尝试读取.lua
                inputStream = findFile(newName);
            }
            return inputStream;
        }
    }

    /**
     * 在 res 或者 asset 或者 文件系统 找drawable
     * TODO 异步
     *
     * @param nameOrPath
     * @return
     */
    public Drawable findDrawable(final String nameOrPath) {
        Drawable drawable = null;
        if (!TextUtils.isEmpty(nameOrPath)) {
            final String drawableName = FileUtil.hasPostfix(nameOrPath) ? nameOrPath : ParamUtil.getFileNameWithPostfix(nameOrPath, "png");//如果没有后缀，则处理成.png

            if (drawable == null && !TextUtils.isEmpty(mBasePath)) {//尝试从文件系统获取
                if (!FileUtil.isContainsFolderPath(drawableName, mBasePath)) {//不带基础路径的情况
                    final String filePath = FileUtil.buildPath(mBasePath, drawableName);
                    if (FileUtil.exists(filePath)) {
                        LogUtil.d("[findDrawable-FileSystem]", filePath);
                        drawable = DrawableUtil.getByPath(filePath);
                    }
                } else if (FileUtil.exists(drawableName)) {
                    LogUtil.d("[findDrawable-FileSystem]", drawableName);
                    drawable = DrawableUtil.getByPath(drawableName);
                }
            }

            if (drawable == null && !TextUtils.isEmpty(mBaseAssetPath)) {//尝试从asset下获取
                if (!FileUtil.isContainsFolderPath(drawableName, mBaseAssetPath)) {//不带基础路径的情况
                    final String assetFilePath = FileUtil.buildPath(mBaseAssetPath, drawableName);//asset路径
                    LogUtil.d("[findDrawable-Assets]", assetFilePath);
                    drawable = DrawableUtil.getAssetByPath(mContext, assetFilePath);
                } else {
                    LogUtil.d("[findDrawable-Assets]", drawableName);
                    drawable = DrawableUtil.getAssetByPath(mContext, drawableName);
                }
            }

            if (drawable == null) {//尝试从res下获取
                LogUtil.d("[findDrawable-Res]", drawableName);
                drawable = DrawableUtil.getByName(mContext, drawableName);
            }

        }
        return drawable;
    }

    /**
     * 在 asset 或者 文件系统 找字体文件
     * TODO 优化
     *
     * @param nameOrPath
     * @return
     */
    public Typeface findTypeface(final String nameOrPath) {
        Typeface typeface = null;
        if (!TextUtils.isEmpty(nameOrPath)) {
            final String typefaceNameOrPath = FileUtil.hasPostfix(nameOrPath) ? nameOrPath : ParamUtil.getFileNameWithPostfix(nameOrPath, "ttf");//如果没有后缀，则处理成.ttf

            if (typeface == null && !TextUtils.isEmpty(mBasePath)) {//尝试从文件系统获取
                if (!FileUtil.isContainsFolderPath(typefaceNameOrPath, mBasePath)) {
                    final String filePath = FileUtil.buildPath(mBasePath, typefaceNameOrPath);
                    if (FileUtil.exists(filePath)) {
                        LogUtil.d("[findTypeface-FileSystem]", filePath);
                        typeface = TypefaceUtil.create(filePath);
                    }
                } else if (FileUtil.exists(typefaceNameOrPath)) {
                    LogUtil.d("[findTypeface-FileSystem]", typefaceNameOrPath);
                    typeface = TypefaceUtil.create(typefaceNameOrPath);
                }
            }

            if (typeface == null && !TextUtils.isEmpty(mBaseAssetPath)) {//尝试从Asset下获取
                if (!FileUtil.isContainsFolderPath(typefaceNameOrPath, mBaseAssetPath)) {
                    final String assetFilePath = FileUtil.buildPath(mBaseAssetPath, typefaceNameOrPath);
                    LogUtil.d("[findTypeface-Assets]", assetFilePath);
                    typeface = TypefaceUtil.create(mContext, assetFilePath);
                } else {
                    LogUtil.d("[findTypeface-Assets]", typefaceNameOrPath);
                    typeface = TypefaceUtil.create(mContext, typefaceNameOrPath);
                }
            }

            if (typeface == null) {//优先加载本地字体
                LogUtil.d("[findTypeface-Assets]", typefaceNameOrPath);
                typeface = TypefaceUtil.create(mContext, typefaceNameOrPath);
            }
        }

        return typeface != null ? typeface : Typeface.DEFAULT;
    }


    /**
     * 在 文件系统 或者 asset下 找资源
     * TODO 异步
     *
     * @param nameOrPath
     * @return
     */
    public InputStream findFile(final String nameOrPath) {
        InputStream inputStream = null;

        if (!TextUtils.isEmpty(nameOrPath)) {

            if (inputStream == null && !TextUtils.isEmpty(mBasePath)) {//尝试文件系统路径
                if (!FileUtil.isContainsFolderPath(nameOrPath, mBasePath)) {//不带基础路径的情况
                    final String filePath = FileUtil.buildPath(mBasePath, nameOrPath);
                    if (FileUtil.exists(filePath)) {
                        LogUtil.d("[findFile-FileSystem]", filePath);
                        inputStream = FileUtil.open(filePath);
                    }
                } else if (FileUtil.exists(nameOrPath)) {
                    LogUtil.d("[findFile-FileSystem]", nameOrPath);
                    inputStream = FileUtil.open(nameOrPath);
                }
            }

            if (inputStream == null && !TextUtils.isEmpty(mBaseAssetPath)) {//尝试从asset下获取
                if (!FileUtil.isContainsFolderPath(nameOrPath, mBaseAssetPath)) {//不带基础路径的情况
                    final String assetFilePath = FileUtil.buildPath(mBaseAssetPath, nameOrPath);
                    LogUtil.d("[findFile-Assets]", assetFilePath);
                    inputStream = AssetUtil.open(mContext, assetFilePath);
                } else {
                    LogUtil.d("[findFile-Assets]", nameOrPath);
                    inputStream = AssetUtil.open(mContext, nameOrPath);
                }
            }

            if (inputStream == null) {//直接从asset下加载
                LogUtil.d("[findFile-Assets]", nameOrPath);
                inputStream = AssetUtil.open(mContext, nameOrPath);
            }
        }
        return inputStream;
    }

    /**
     * 获取文件的完整路径
     *
     * @param nameOrPath
     * @return
     */
    public String findFullPath(final String nameOrPath) {
        String fullPath = null;
        if (!TextUtils.isEmpty(nameOrPath)) {
            if (fullPath == null && !TextUtils.isEmpty(mBasePath)) {//尝试文件系统路径
                if (!FileUtil.isContainsFolderPath(nameOrPath, mBasePath)) {//不带基础路径的情况
                    final String filePath = FileUtil.buildPath(mBasePath, nameOrPath);
                    if (FileUtil.exists(filePath)) {
                        LogUtil.d("[findFullPath-FileSystem]", filePath);
                        fullPath = filePath;
                    }
                } else if (FileUtil.exists(nameOrPath)) {
                    LogUtil.d("[findFullPath-FileSystem]", nameOrPath);
                    fullPath = nameOrPath;
                }
            }

            if (fullPath == null && !TextUtils.isEmpty(mBaseAssetPath)) {//尝试从asset下获取
                if (!FileUtil.isContainsFolderPath(nameOrPath, mBaseAssetPath)) {//不带基础路径的情况
                    final String assetFilePath = FileUtil.buildPath(mBaseAssetPath, nameOrPath);
                    LogUtil.d("[findFullPath-Assets]", assetFilePath);
                    fullPath = assetFilePath;
                } else {
                    LogUtil.d("[findFullPath-Assets]", nameOrPath);
                    fullPath = nameOrPath;
                }
            }

            if (fullPath == null) {//直接从asset下加载
                LogUtil.d("[findFullPath-Assets]", nameOrPath);
                fullPath = nameOrPath;
            }
        }
        return fullPath;
    }
}
