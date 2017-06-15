/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

import android.content.Context;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.taobao.luaview.debug.DebugConnection;
import com.taobao.luaview.fun.binder.ui.UICustomPanelBinder;
import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;
import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.receiver.ConnectionStateChangeBroadcastReceiver;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.EncryptUtil;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.NetworkUtil;
import com.taobao.luaview.view.LVCustomPanel;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.IOException;
import java.io.InputStream;

/**
 * Core of LuaView functions
 *
 * @author song
 * @date 17/2/22
 * 主要功能描述
 * 修改描述
 * 下午5:04 song XXX
 */
public class LuaViewCore implements ConnectionStateChangeBroadcastReceiver.OnConnectionChangeListener {

    //window luavalue
    private static final String LV_WINDOW = "window";

    //image provider class
    private static Class<? extends ImageProvider> mImageProviderClazz;

    //provider for loading image
    private static ImageProvider mImageProvider;

    //Context
    private Context mContext;

    //globals
    public Globals mGlobals;

    //userdata for render target
    private UDView mWindowUserdata;

    public interface CreatedCallback {
        void onCreated(LuaViewCore luaViewCore);
    }
    //---------------------------------------静态方法------------------------------------------------

    /**
     * create a LuaViewCore
     *
     * @param context
     * @return
     */
    public static LuaViewCore create(final Context context) {
        final Globals globals = LuaViewManager.createGlobals();
        return createLuaViewCore(context, globals);
    }

    /**
     * create LuaViewCore async （带返回值）
     *
     * @param context
     */
    public static LuaViewCore createAsync(final Context context) {
        final Globals globals = LuaViewManager.createGlobalsAsync();
        return createLuaViewCore(context, globals);
    }

    /**
     * create LuaViewCore async（兼容老的，没必要包装一层SimpleTask）
     *
     * @param context
     * @param createdCallback
     */
    public static void createAsync(final Context context, final LuaViewCore.CreatedCallback createdCallback) {
        new SimpleTask1<LuaViewCore>() {
            @Override
            protected LuaViewCore doInBackground(Object... params) {
                return create(context);
            }

            @Override
            protected void onPostExecute(LuaViewCore luaViewCore) {
                if (createdCallback != null) {
                    createdCallback.onCreated(luaViewCore);
                }
            }
        }.executeInPool();
    }

    /**
     * create LuaViewCore and setup everything
     *
     * @param context
     * @param globals
     * @return
     */
    private static LuaViewCore createLuaViewCore(final Context context, final Globals globals) {
        final LuaViewCore core = new LuaViewCore(context, globals);
        if (LuaViewConfig.isOpenDebugger()) {//如果是debug，支持ide调试
            core.openDebugger();
        }
        return core;
    }


    //-----------------------------------------load script------------------------------------------

    /**
     * 加载，可能是url，可能是Asset，可能是文件，也可能是脚本
     * url : http or https, http://[xxx] or https://[xxx]
     * TODO asset : folder or file, file://android_asset/[xxx]
     * TODO file : folder or file, file://[xxx]
     * TODO script: content://[xxx]
     *
     * @param urlOrFileOrScript
     * @return
     */
    public LuaViewCore load(final String urlOrFileOrScript) {
        return load(urlOrFileOrScript, null, null);
    }

    public LuaViewCore load(final String urlOrFileOrScript, final LuaScriptLoader.ScriptExecuteCallback callback) {
        return load(urlOrFileOrScript, null, callback);
    }

    public LuaViewCore load(final String urlOrFileOrScript, final String sha256) {
        return load(urlOrFileOrScript, sha256, null);
    }

    public LuaViewCore load(final String urlOrFileOrScript, final String sha256, final LuaScriptLoader.ScriptExecuteCallback callback) {
        if (!TextUtils.isEmpty(urlOrFileOrScript)) {
            if (URLUtil.isNetworkUrl(urlOrFileOrScript)) {//url, http:// or https://
                loadUrl(urlOrFileOrScript, sha256, callback);
            } else {
                loadFile(urlOrFileOrScript, callback);
            }
            //TODO other schema
        } else if (callback != null) {
            callback.onScriptExecuted(null, false);
        }
        return this;
    }

    /**
     * 直接加载网络脚本
     *
     * @param url http://[xxx] or https://[xxx]
     * @return
     */
    public LuaViewCore loadUrl(final String url, final String sha256) {
        return loadUrl(url, sha256, null);
    }

    public LuaViewCore loadUrl(final String url, final String sha256, final LuaScriptLoader.ScriptExecuteCallback callback) {
        updateUri(url);
        if (!TextUtils.isEmpty(url)) {
            new LuaScriptLoader(mContext).load(url, sha256, new LuaScriptLoader.ScriptLoaderCallback() {
                @Override
                public void onScriptLoaded(ScriptBundle bundle) {
                    if (callback == null || callback.onScriptPrepared(bundle) == false) {//脚本准备完成，且不第三方自己执行
                        loadScriptBundle(bundle, callback);
                    } else if (callback != null) {
                        callback.onScriptExecuted(url, false);
                    }
                }
            });
        } else if (callback != null) {
            callback.onScriptExecuted(null, false);
        }
        return this;
    }

    /**
     * 加载 Asset 路径下的脚本
     *
     * @param assetPath folder path or file path
     * @return
     */
    public LuaViewCore loadAsset(final String assetPath) {
        return loadAsset(assetPath, null);
    }

    public LuaViewCore loadAsset(final String assetPath, final LuaScriptLoader.ScriptExecuteCallback callback) {
        //TODO
        return this;
    }

    /**
     * 加载脚本库，必须在主进程中执行，先判断asset下是否存在，再去文件系统中查找
     *
     * @param luaFileName plain file name or file://[xxx]
     * @return
     */
    public LuaViewCore loadFile(final String luaFileName) {
        return loadFile(luaFileName, null);
    }

    public LuaViewCore loadFile(final String luaFileName, final LuaScriptLoader.ScriptExecuteCallback callback) {
        updateUri(luaFileName);
        if (!TextUtils.isEmpty(luaFileName)) {
            this.loadFileInternal(luaFileName, callback);//加载文件
        } else {
            if (callback != null) {
                callback.onScriptExecuted(getUri(), false);
            }
        }
        return this;
    }

    /**
     * 加载脚本
     *
     * @param script
     * @return
     */
    public LuaViewCore loadScript(final String script) {
        return loadScript(script, null);
    }

    public LuaViewCore loadScript(final String script, final LuaScriptLoader.ScriptExecuteCallback callback) {
        updateUri("");
        if (!TextUtils.isEmpty(script)) {
            this.loadScriptInternal(new ScriptFile(script, EncryptUtil.md5Hex(script)), callback);
        } else {
            if (callback != null) {
                callback.onScriptExecuted(getUri(), false);
            }
        }
        return this;
    }

    /**
     * 加载 Script File
     *
     * @param scriptFile
     * @return
     */
    public LuaViewCore loadScript(final ScriptFile scriptFile) {
        return loadScript(scriptFile, null);
    }

    public LuaViewCore loadScript(final ScriptFile scriptFile, final LuaScriptLoader.ScriptExecuteCallback callback) {
        if (scriptFile != null) {
            this.loadScriptInternal(scriptFile, callback);
        } else if (callback != null) {
            callback.onScriptExecuted(getUri(), false);
        }
        return this;
    }

    /**
     * 加载 Script Bundle
     *
     * @param scriptBundle
     * @return
     */
    public LuaViewCore loadScriptBundle(final ScriptBundle scriptBundle) {
        return loadScriptBundle(scriptBundle, null);
    }

    public LuaViewCore loadScriptBundle(final ScriptBundle scriptBundle, final LuaScriptLoader.ScriptExecuteCallback callback) {
        loadScriptBundle(scriptBundle, LuaResourceFinder.DEFAULT_MAIN_ENTRY, callback);
        return this;
    }

    public LuaViewCore loadScriptBundle(final ScriptBundle scriptBundle, final String mainScriptFileName, final LuaScriptLoader.ScriptExecuteCallback callback) {
        if (scriptBundle != null) {
            if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
                mGlobals.getLuaResourceFinder().setScriptBundle(scriptBundle);
            }
            if (scriptBundle.containsKey(mainScriptFileName)) {
                final ScriptFile scriptFile = scriptBundle.getScriptFile(mainScriptFileName);
                loadScript(scriptFile, callback);
                return this;
            }
        }
        if (callback != null) {
            callback.onScriptExecuted(getUri(), false);
        }
        return this;
    }

    /**
     * load prototype (lua bytecode or sourcecode)
     *
     * @param inputStream
     * @return
     */
    public LuaViewCore loadPrototype(final InputStream inputStream, final String name, final LuaScriptLoader.ScriptExecuteCallback callback) {
        new SimpleTask1<LuaValue>() {
            @Override
            protected LuaValue doInBackground(Object... params) {
                try {
                    if (mGlobals != null) {
                        Prototype prototype = mGlobals.loadPrototype(inputStream, name, "bt");
                        if (prototype != null) {
                            LuaValue result = mGlobals.load(prototype, name);
                            return result;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(LuaValue value) {
                final LuaValue activity = CoerceJavaToLua.coerce(mContext);
                final LuaValue viewObj = CoerceJavaToLua.coerce(LuaViewCore.this);
                if (callback == null || callback.onScriptCompiled(value, activity, viewObj) == false) {
                    executeScript(value, activity, viewObj, callback);
                }
            }
        }.executeInPool();
        return this;
    }
    //---------------------------------------注册函数----------------------------------------------

    /**
     * 加载一个binder，可以用作覆盖老功能
     * Lib 必须注解上 LuaViewLib
     *
     * @param binders
     * @return
     */
    public synchronized LuaViewCore registerLibs(LuaValue... binders) {
        if (mGlobals != null && binders != null) {
            for (LuaValue binder : binders) {
                mGlobals.tryLazyLoad(binder);
            }
        }
        return this;
    }

    /**
     * 注册一个名称到该lua对象的命名空间中
     *
     * @param luaName
     * @param obj
     * @return
     */
    public synchronized LuaViewCore register(final String luaName, final Object obj) {
        if (mGlobals != null && !TextUtils.isEmpty(luaName)) {
            final LuaValue value = mGlobals.get(luaName);
            if (obj != value) {
                mGlobals.set(luaName, CoerceJavaToLua.coerce(obj));
            }
        } else {
            LogUtil.e("name " + luaName + " is invalid!");
        }
        return this;
    }

    /**
     * 注册一个名称到该lua对象的命名空间中
     *
     * @param clazz
     * @return
     */
    public synchronized LuaViewCore registerPanel(final Class<? extends LVCustomPanel> clazz) {
        return registerPanel(clazz != null ? clazz.getSimpleName() : null, clazz);
    }

    /**
     * 注册一个名称到该lua对象的命名空间中
     *
     * @param luaName
     * @param clazz
     * @return
     */
    public synchronized LuaViewCore registerPanel(final String luaName, final Class<? extends LVCustomPanel> clazz) {
        if (mGlobals != null && !TextUtils.isEmpty(luaName) && (clazz != null && clazz.getSuperclass() == LVCustomPanel.class)) {
            final LuaValue value = mGlobals.get(luaName);
            if (value == null || value.isnil()) {
                mGlobals.tryLazyLoad(new UICustomPanelBinder(clazz, luaName));
            } else {
                LogUtil.e("panel name " + luaName + " is already registered!");
            }
        } else {
            LogUtil.e("name " + luaName + " is invalid or Class " + clazz + " is not subclass of " + LVCustomPanel.class.getSimpleName());
        }
        return this;
    }

    /**
     * 解注册一个命名空间中的名字
     *
     * @param luaName
     * @return
     */
    public synchronized LuaViewCore unregister(final String luaName) {
        if (mGlobals != null && !TextUtils.isEmpty(luaName)) {
            mGlobals.set(luaName, LuaValue.NIL);
        }
        return this;
    }

    //----------------------------------------call lua function-------------------------------------

    /**
     * 调用lua的某个全局函数
     *
     * @param funName
     * @param objs
     * @return
     */
    public Object callLuaFunction(String funName, Object... objs) {
        if (mGlobals != null && funName != null) {
            final LuaValue callback = mGlobals.get(funName);
            return LuaUtil.callFunction(callback, objs);
        }
        return LuaValue.NIL;
    }

    //----------------------------------------Image Provider----------------------------------------

    /**
     * 注册ImageProvider
     */

    public static void registerImageProvider(final Class<? extends ImageProvider> clazz) {
        mImageProviderClazz = clazz;
    }

    /**
     * 获取ImageProvider
     *
     * @return
     */
    public static ImageProvider getImageProvider() {
        if (mImageProvider == null && mImageProviderClazz != null) {
            try {
                mImageProvider = mImageProviderClazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return mImageProvider;
    }

    //----------------------------------------setup functions---------------------------------------

    /**
     * 设置使用标准语法
     *
     * @param standardSyntax
     */
    public void setUseStandardSyntax(boolean standardSyntax) {
        if (mGlobals != null) {
            mGlobals.setUseStandardSyntax(standardSyntax);
        }
    }

    /**
     * 刷新容器是否可以刷新(用在RefreshCollectionView初始化的地方)
     *
     * @param enable
     */
    public void setRefreshContainerEnable(boolean enable) {
        if (this.mGlobals != null) {
            this.mGlobals.isRefreshContainerEnable = enable;
        }
    }

    /**
     * 刷新容器是否可以刷新(用在RefreshCollectionView初始化的地方)
     */
    public boolean isRefreshContainerEnable() {
        return this.mGlobals != null ? this.mGlobals.isRefreshContainerEnable : true;
    }


    public String getUri() {
        if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
            return mGlobals.getLuaResourceFinder().getUri();
        }
        return null;
    }

    public void setUri(String uri) {
        if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
            mGlobals.getLuaResourceFinder().setUri(uri);
        }
    }

    public Globals getGlobals() {
        return mGlobals;
    }
    //-------------------------------------------私有------------------------------------------------

    /**
     * TODO 优化
     * 创建LuaView的methods，这里可以优化，实现更加优雅，其实就是将window注册成一个userdata，并且userdata是UDViewGroup
     *
     * @return
     */
    private static LuaTable createMetaTableForLuaView() {
        return LuaViewManager.createMetatable(UIViewGroupMethodMapper.class);
    }

    /**
     * @param globals
     */
    private LuaViewCore(Context context, Globals globals) {
        init(context);
        this.mContext = context;
        this.mGlobals = globals;
    }

    private void init(Context context) {
        //常量初始化
        Constants.init(context);
        //初始化脚本管理
        LuaScriptManager.init(context);
    }

    /**
     * 开启debugger
     */
    public void openDebugger() {
        loadFile("debug.lua", new LuaScriptLoader.ScriptExecuteCallback() {
            @Override
            public boolean onScriptPrepared(ScriptBundle bundle) {
                return false;
            }

            @Override
            public boolean onScriptCompiled(LuaValue value, LuaValue activity, LuaValue obj) {
                return false;
            }

            @Override
            public void onScriptExecuted(String uri, boolean executedSuccess) {
                if (executedSuccess && mGlobals != null) {
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                            .detectNetwork()   // or .detectAll() for all detectable problems，主线程执行socket
                            .build());
                    mGlobals.debugConnection = DebugConnection.create();
                }
            }
        });
    }

    //-----------------------------------------私有load函数------------------------------------------
    private void updateUri(String uri) {
        if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
            mGlobals.getLuaResourceFinder().setUri(uri);
        }
    }

    /**
     * 初始化
     *
     * @param luaFileName
     */
    private LuaViewCore loadFileInternal(final String luaFileName, final LuaScriptLoader.ScriptExecuteCallback callback) {
        new SimpleTask1<LuaValue>() {
            @Override
            protected LuaValue doInBackground(Object... params) {
                if (mGlobals != null) {
                    if (mGlobals.isInited) {
                        try {
                            return mGlobals.loadfile(luaFileName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e("[Load Script Failed]", luaFileName, e);
                        }
                    } else {
                        try {
                            Thread.sleep(16);
                            return doInBackground(params);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(LuaValue value) {
                final LuaValue activity = CoerceJavaToLua.coerce(mContext);
                final LuaValue viewObj = CoerceJavaToLua.coerce(LuaViewCore.this);
                if (callback == null || callback.onScriptCompiled(value, activity, viewObj) == false) {
                    //执行脚本，在主线程
                    executeScript(value, activity, viewObj, callback);
                }
            }
        }.executeInPool();//TODO 这里使用execute，而不是executeInPoll，与createGlobalAsync保持一致

        return this;
    }

    /**
     * 加载纯脚本
     *
     * @param scriptFile
     */

    private LuaViewCore loadScriptInternal(final ScriptFile scriptFile, final LuaScriptLoader.ScriptExecuteCallback callback) {
        new SimpleTask1<LuaValue>() {//load async
            @Override
            protected LuaValue doInBackground(Object... params) {
                if (mGlobals != null) {
                    if (mGlobals.isInited) {
                        if (scriptFile != null) {//prototype
                            String filePath = scriptFile.getFilePath();
                            if (scriptFile.prototype != null) {//prototype
                                return mGlobals.load(scriptFile.prototype, filePath);
                            } else {//source code
                                try {
                                    return mGlobals.load(scriptFile.getScriptString(), filePath);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtil.e("[Load Script Failed]", filePath, e);
                                }
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(16);
                            return doInBackground(params);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(LuaValue value) {
                final LuaValue activity = CoerceJavaToLua.coerce(mContext);
                final LuaValue viewObj = CoerceJavaToLua.coerce(LuaViewCore.this);
                if (callback == null || callback.onScriptCompiled(value, activity, viewObj) == false) {
                    //执行脚本，在主线程
                    executeScript(value, activity, viewObj, callback);
                }
            }
        }.executeInPool();//TODO 这里使用execute，而不是executeInPoll，与createGlobalAsync保持一致
        return this;
    }

    /**
     * 执行脚本
     *
     * @param value
     * @param activity
     * @param viewObj
     * @param callback
     */
    public boolean executeScript(LuaValue value, LuaValue activity, LuaValue viewObj, final LuaScriptLoader.ScriptExecuteCallback callback) {
        try {
            if (mGlobals != null && value != null) {
                mGlobals.saveContainer(getRenderTarget());
                mGlobals.set(LV_WINDOW, mWindowUserdata);//TODO 优化到其他地方?，设置window对象
                value.call(activity, viewObj);
                mGlobals.restoreContainer();
                if (callback != null) {
                    callback.onScriptExecuted(getUri(), true);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("[Executed Script Failed]", e);
        }
        if (callback != null) {
            callback.onScriptExecuted(getUri(), false);
        }
        return false;
    }

    //-----------------------------------------网络回调----------------------------------------------

    @Override
    public void onConnectionClosed() {
        if (mWindowUserdata != null) {
            if (mWindowUserdata.getCallback() != null && mWindowUserdata.getCallback().istable()) {
                LuaUtil.callFunction(LuaUtil.getFunction(mWindowUserdata.getCallback(), "onConnectionClosed", "OnConnectionClosed"));
            }
        }
    }

    @Override
    public void onMobileConnected() {
        if (mWindowUserdata != null) {
            if (mWindowUserdata.getCallback() != null && mWindowUserdata.getCallback().istable()) {
                LuaUtil.callFunction(LuaUtil.getFunction(mWindowUserdata.getCallback(), "onMobileConnected", "OnMobileConnected"));
            }
        }
    }

    @Override
    public void onWifiConnected() {
        if (mWindowUserdata != null) {
            if (mWindowUserdata.getCallback() != null && mWindowUserdata.getCallback().istable()) {
                LuaUtil.callFunction(LuaUtil.getFunction(mWindowUserdata.getCallback(), "onWifiConnected", "OnWifiConnected"));
            }
        }
    }

    //----------------------------------------getter and setter-------------------------------------

    /**
     * set window userdata
     *
     * @param userdata
     * @return
     */
    public LuaViewCore setWindowUserdata(UDView userdata) {
        this.mWindowUserdata = userdata;
        return this;
    }

    /**
     * get userdata for window
     *
     * @return
     */
    public LuaValue getWindowUserdata() {
        return this.mWindowUserdata;
    }

    /**
     * set render target
     *
     * @param viewGroup
     * @return
     */
    public LuaViewCore setRenderTarget(ViewGroup viewGroup) {
        if (mGlobals != null) {
            mGlobals.setRenderTarget(viewGroup);
        }
        return this;
    }

    /**
     * get render target
     *
     * @return
     */
    public ViewGroup getRenderTarget() {
        return mGlobals != null ? mGlobals.getRenderTarget() : null;
    }

    //----------------------------------------显示的生命周期 管理-------------------------------------

    /**
     * View初始化的时候注册监听
     */
    public void onAttached() {
    }

    /**
     * 显示
     *
     * @param visibility
     */
    public void onShow(int visibility) {
        if (visibility == View.VISIBLE) {//onShow
            NetworkUtil.registerConnectionChangeListener(mContext, this);//show之前注册
        }
    }

    /**
     * 隐藏
     *
     * @param visibility
     */
    public void onHide(int visibility) {
        if (visibility != View.VISIBLE) {//onHide
            NetworkUtil.unregisterConnectionChangeListener(mContext, this);//hide之后调用
        }
    }

    /**
     * 在onDetached的时候清空cache
     */
    public void onDetached() {
        clearCache();
    }

    /**
     * 销毁的时候从外部调用，清空所有外部引用
     */
    public synchronized void onDestroy() {
        clearCache();
        if (mGlobals != null) {
            mGlobals.onDestroy();
            mGlobals = null;
        }
        mContext = null;
        mWindowUserdata = null;
    }

    /**
     * 清空cache
     */
    private void clearCache() {
        if (mGlobals != null) {
            mGlobals.clearCache();
        }
        NetworkUtil.unregisterConnectionChangeListener(mContext, this);
    }
}