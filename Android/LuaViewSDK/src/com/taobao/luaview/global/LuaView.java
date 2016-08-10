package com.taobao.luaview.global;

import android.content.Context;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;

import com.taobao.luaview.cache.LuaCache;
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
import com.taobao.luaview.view.LVViewGroup;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * LuaView 实现类
 *
 * @author song
 * @date 15/8/20
 */
public class LuaView extends LVViewGroup implements ConnectionStateChangeBroadcastReceiver.OnConnectionChangeListener {
    //image provider clazz
    private static Class<? extends ImageProvider> mImageProviderClazz;
    private static ImageProvider mImageProvider;
    private boolean isRefreshContainerEnable = true;

    //cache
    private LuaCache mLuaCache;

    //需要渲染的Target
    private ILVViewGroup mRenderTarget;


    public interface CreatedCallback {
        void onCreated(LuaView luaView);
    }
    //---------------------------------------静态方法------------------------------------------------

    /**
     * create a luaview
     *
     * @param context
     * @return
     */
    public static LuaView create(final Context context) {
        //初始化
        init(context);

        //create globals
        final Globals globals = createGlobals(context);

        //设置metaTable
        return createLuaView(context, globals);
    }


    /**
     * create LuaView async
     *
     * @param context
     * @param createdCallback
     */
    public static void createAsync(final Context context, final CreatedCallback createdCallback) {
        new SimpleTask1<Globals>() {
            @Override
            protected Globals doInBackground(Object... params) {
                //init
                initAsync();

                //create globals
                return createGlobalsAsync();
            }

            @Override
            protected void onPostExecute(Globals globals) {
                //create luaview
                createViewAsync(globals);
            }

            //初始化
            private void initAsync() {
                Constants.init(context);
                LuaScriptManager.init(context);
            }

            //创建globals
            private Globals createGlobalsAsync() {
                return createGlobals(context);
            }

            //创建view
            private void createViewAsync(Globals globals) {
                final LuaView luaView = createLuaView(context, globals);
                if (createdCallback != null) {
                    createdCallback.onCreated(luaView);
                }
            }
        }.execute();
    }

    private static Globals createGlobals(final Context context) {
        final Globals globals = LuaViewManager.createGlobals();
        return globals;
    }

    private static LuaView createLuaView(final Context context, final Globals globals) {
        final LuaView luaView = new LuaView(context, globals, createMetaTableForLuaView());
        globals.setLuaView(luaView);
        globals.finder = new LuaResourceFinder(context);
        if (LuaViewConfig.isOpenDebugger()) {//如果是debug，支持ide调试
            luaView.openDebugger();
        }
        return luaView;
    }
    //-----------------------------------------加载函数----------------------------------------------

    /**
     * 加载，可能是url，可能是Asset，可能是文件，也可能是脚本
     * url : http or https, http://[xxx] or https://[xxx]
     * asset : folder or file, file://android_asset/[xxx]
     * file : folder or file, file://[xxx]
     * script: content://[xxx]
     *
     * @param urlOrFileOrScript
     * @return
     */
    public LuaView load(final String urlOrFileOrScript) {
        return load(urlOrFileOrScript, null, null);
    }

    /**
     * 加载，可能是url，可能是Asset，可能是文件，也可能是脚本
     * url : http or https, http://[xxx] or https://[xxx]
     * asset : folder or file, file://android_asset/[xxx]
     * file : folder or file, file://[xxx]
     * script: content://[xxx]
     *
     * @param urlOrFileOrScript
     * @return
     */
    public LuaView load(final String urlOrFileOrScript, final LuaScriptLoader.ScriptExecuteCallback callback) {
        return load(urlOrFileOrScript, null, callback);
    }

    /**
     * 加载，可能是url，可能是Asset，可能是文件，也可能是脚本
     * url : http or https, http://[xxx] or https://[xxx]
     * asset : folder or file, file://android_asset/[xxx]
     * file : folder or file, file://[xxx]
     * script: content://[xxx]
     *
     * @param urlOrFileOrScript
     * @return
     */
    public LuaView load(final String urlOrFileOrScript, final String sha256) {
        return load(urlOrFileOrScript, sha256, null);
    }

    public LuaView load(final String urlOrFileOrScript, final String sha256, final LuaScriptLoader.ScriptExecuteCallback callback) {
        if (!TextUtils.isEmpty(urlOrFileOrScript)) {
            if (URLUtil.isNetworkUrl(urlOrFileOrScript)) {//url, http:// or https://
                loadUrl(urlOrFileOrScript, sha256, callback);
            } else {
                loadFile(urlOrFileOrScript, callback);
            }
            /*if (URLUtil.isHttpUrl(urlOrFileOrScript) || URLUtil.isHttpsUrl(urlOrFileOrScript)) {//url, http:// or https://
                loadUrl(urlOrFileOrScript, sha256, callback);
            } else if (URLUtil.isAssetUrl(urlOrFileOrScript)) {//加载asset, file:///android_asset/
                loadAsset(urlOrFileOrScript, callback);
            } else if (URLUtil.isFileUrl(urlOrFileOrScript)) {//加载文件, file://
                loadFile(urlOrFileOrScript);
            } else if (URLUtil.isContentUrl(urlOrFileOrScript)) {//纯脚本, content://
                loadScript(urlOrFileOrScript);
            } else {//默认尝试加载文件，有可能是文件系统的，也有可能是asset的，也有可能是其他的
                if (AssetUtil.exists(getContext(), urlOrFileOrScript)) {//是asset
                    loadAsset(urlOrFileOrScript, callback);
                } else if (FileUtil.exists(urlOrFileOrScript) || LuaScriptManager.exists(urlOrFileOrScript)) {//在文件系统或者脚本目录
                    loadFile(urlOrFileOrScript);
                } else {
                    loadScript(urlOrFileOrScript);
                }
            }*/
        }
        return this;
    }

    /**
     * 直接加载网络脚本
     *
     * @param url http://[xxx] or https://[xxx]
     * @return
     */
    public LuaView loadUrl(final String url, final String sha256) {
        return loadUrl(url, sha256, null);
    }

    public LuaView loadUrl(final String url, final String sha256, final LuaScriptLoader.ScriptExecuteCallback callback) {
        updateUri(url);
        if (!TextUtils.isEmpty(url)) {
            new LuaScriptLoader(getContext()).load(url, sha256, new LuaScriptLoader.ScriptLoaderCallback() {
                @Override
                public void onScriptLoaded(ScriptBundle bundle) {
                    if(callback == null || callback.onScriptPrepared(bundle) == false){//脚本准备失败
                        loadScriptBundle(bundle, callback);
                    } else if (callback != null){
                        callback.onScriptExecuted(false);
                    }
                }
            });
        }
        return this;
    }

    /**
     * 加载 Asset路径下的脚本
     *
     * @param assetPath folder path or file path
     * @return
     */
    private LuaView loadAsset(final String assetPath) {
        return loadAsset(assetPath, null);
    }

    private LuaView loadAsset(final String assetPath, final LuaScriptLoader.ScriptExecuteCallback callback) {
        //TODO
        return this;
    }

    /**
     * 加载脚本库，必须在主进程中执行，先判断asset下是否存在，再去文件系统中查找
     *
     * @param luaFileName plain file name or file://[xxx]
     * @return
     */
    private LuaView loadFile(final String luaFileName) {
        return loadFile(luaFileName, null);
    }

    private LuaView loadFile(final String luaFileName, final LuaScriptLoader.ScriptExecuteCallback callback) {
        updateUri(luaFileName);
        if (!TextUtils.isEmpty(luaFileName)) {
            this.loadFileInternal(luaFileName, callback);//加载文件
        }
        return this;
    }

    /**
     * 加载脚本
     *
     * @param script
     * @return
     */
    public LuaView loadScript(final String script) {
        return loadScript(script, null);
    }

    public LuaView loadScript(final String script, final LuaScriptLoader.ScriptExecuteCallback callback) {
        if (!TextUtils.isEmpty(script)) {
            this.loadScriptInternal(EncryptUtil.md5Hex(script), script, callback);
        }
        return this;
    }

    /**
     * 加载script bundle
     *
     * @param scriptFile
     * @return
     */
    private LuaView loadScript(final ScriptFile scriptFile) {
        return loadScript(scriptFile, null);
    }

    private LuaView loadScript(final ScriptFile scriptFile, final LuaScriptLoader.ScriptExecuteCallback callback) {
        if (scriptFile != null) {
            this.loadScriptInternal(scriptFile.getFilePath(), scriptFile.script, callback);
        } else {
            if (callback != null) {
                callback.onScriptExecuted(false);
            }
        }
        return this;
    }

    /**
     * 加载 Script Bundle
     *
     * @param scriptBundle
     * @return
     */
    public LuaView loadScriptBundle(final ScriptBundle scriptBundle) {
        return loadScriptBundle(scriptBundle, null);
    }

    public LuaView loadScriptBundle(final ScriptBundle scriptBundle, final LuaScriptLoader.ScriptExecuteCallback callback) {
        loadScriptBundle(scriptBundle, LuaResourceFinder.DEFAULT_MAIN_ENTRY, callback);
        return this;
    }

    public LuaView loadScriptBundle(final ScriptBundle scriptBundle, final String mainScriptFileName, final LuaScriptLoader.ScriptExecuteCallback callback) {
        if (scriptBundle != null) {
            if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
                mGlobals.getLuaResourceFinder().setScriptBundle(scriptBundle);
            }
            if (scriptBundle.containsKey(mainScriptFileName)) {
                final ScriptFile scriptFile = scriptBundle.getScriptFile(mainScriptFileName);
                loadScript(scriptFile, callback);
            }
        } else {
            if (callback != null) {
                callback.onScriptExecuted(false);
            }
        }
        return this;
    }
    //---------------------------------------注册函数------------------------------------------------

    /**
     * 加载一个binder，可以用作覆盖老功能
     * Lib 必须注解上 LuaViewLib
     *
     * @param binders
     * @return
     */
    public LuaView registerLibs(LuaValue... binders) {
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
    public LuaView register(final String luaName, final Object obj) {
        if (!TextUtils.isEmpty(luaName)) {
            final LuaValue value = mGlobals.get(luaName);
            if (obj != value) {
                mGlobals.set(luaName, CoerceJavaToLua.coerce(obj));
            }
            /* -- 判断是否为null
            if (value == null || value.isnil()) {
                mGlobals.set(luaName, CoerceJavaToLua.coerce(obj));
            } else {
                LogUtil.d("name " + luaName + " is already registered!");
            }*/
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
    public LuaView registerPanel(final Class<? extends LVCustomPanel> clazz) {
        return registerPanel(clazz != null ? clazz.getSimpleName() : null, clazz);
    }

    /**
     * 注册一个名称到该lua对象的命名空间中
     *
     * @param luaName
     * @param clazz
     * @return
     */
    public LuaView registerPanel(final String luaName, final Class<? extends LVCustomPanel> clazz) {
        if (!TextUtils.isEmpty(luaName) && (clazz != null && clazz.getSuperclass() == LVCustomPanel.class)) {
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
    public LuaView unregister(final String luaName) {
        if (!TextUtils.isEmpty(luaName)) {
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

    /**
     * 调用window.callback下的某个函数
     *
     * @param funName
     * @param objs
     * @return
     */
    public Varargs callWindowFunction(String funName, Object... objs) {
        if (funName != null) {
            final UDView userdata = getUserdata();
            if (userdata != null) {
                final LuaValue callbacks = userdata.getCallback();
                if (LuaUtil.isValid(callbacks)) {
                    return LuaUtil.callFunction(callbacks.get(funName), objs);
                }
            }
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
     * 刷新容器是否可以刷新(用在RefreshCollectionView初始化的地方)
     *
     * @param enable
     */
    public void setRefreshContainerEnable(boolean enable) {
        this.isRefreshContainerEnable = enable;
    }

    /**
     * 刷新容器是否可以刷新(用在RefreshCollectionView初始化的地方)
     */
    public boolean isRefreshContainerEnable() {
        return this.isRefreshContainerEnable;
    }

    //-------------------------------------------私有------------------------------------------------

    /**
     * 初始化
     *
     * @param context
     */
    private static void init(final Context context) {
        Constants.init(context);
        LuaScriptManager.init(context);
    }

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
     * @param context   View级别的Context
     * @param globals
     * @param metaTable
     */
    private LuaView(Context context, Globals globals, LuaValue metaTable) {
        super(context, globals, metaTable, LuaValue.NIL);
        this.mLuaCache = new LuaCache();
    }

    /**
     * 开启debugger
     */
    private void openDebugger() {
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
            public void onScriptExecuted(boolean executedSuccess) {
                if (executedSuccess) {
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
    private LuaView loadFileInternal(final String luaFileName, final LuaScriptLoader.ScriptExecuteCallback callback) {
        final LuaValue activity = CoerceJavaToLua.coerce(getContext());
        final LuaValue viewObj = CoerceJavaToLua.coerce(this);
        new SimpleTask1<LuaValue>() {
            @Override
            protected LuaValue doInBackground(Object... params) {
                try {
                    return mGlobals.loadfile(luaFileName);
                } catch (Exception e){
                    e.printStackTrace();
                    LogUtil.e("[Load Script Failed]", luaFileName, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(LuaValue value) {
                if(callback == null || callback.onScriptCompiled(value, activity, viewObj) == false){
                    //执行脚本，在主线程
                    executeScript(value, activity, viewObj, callback);
                }
            }
        }.execute();
        return this;
    }

    /**
     * 加载纯脚本
     *
     * @param luaScript
     */
    private LuaView loadScriptInternal(final String filePath, final String luaScript, final LuaScriptLoader.ScriptExecuteCallback callback) {
        final LuaValue activity = CoerceJavaToLua.coerce(getContext());
        final LuaValue viewObj = CoerceJavaToLua.coerce(this);
        new SimpleTask1<LuaValue>() {//load async
            @Override
            protected LuaValue doInBackground(Object... params) {
                try {
                    return mGlobals.load(luaScript, filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("[Load Script Failed]", filePath, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(LuaValue value) {
                if(callback == null || callback.onScriptCompiled(value, activity, viewObj) == false){
                    //执行脚本，在主线程
                    executeScript(value, activity, viewObj, callback);
                }
            }
        }.execute();
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
    private void executeScript(LuaValue value, LuaValue activity, LuaValue viewObj, final LuaScriptLoader.ScriptExecuteCallback callback) {
        try {
            if (value != null) {
                mGlobals.saveContainer(getRenderTarget());
                mGlobals.set("window", getUserdata());//TODO 优化到其他地方?，设置window对象
                value.call(activity, viewObj);
                mGlobals.restoreContainer();
                if (callback != null) {
                    callback.onScriptExecuted(true);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("[Executed Script Failed]", e);
        }
        if (callback != null) {
            callback.onScriptExecuted(false);
        }
    }

    //-----------------------------------------网络回调----------------------------------------------

    /**
     * 当显示的时候调用
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        onShow(visibility);
        super.onWindowVisibilityChanged(visibility);
        onHide(visibility);
    }

    /**
     * 创建的时候调用
     */
    @Override
    protected void onAttachedToWindow() {
        onAttached();
        super.onAttachedToWindow();
    }

    /**
     * 离开的时候调用
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDetached();
    }

    @Override
    public void onConnectionClosed() {
        UDView userdata = getUserdata();
        if (userdata != null) {
            if (userdata.getCallback() != null && userdata.getCallback().istable()) {
                LuaUtil.callFunction(LuaUtil.getFunction(userdata.getCallback(), "onConnectionClosed", "OnConnectionClosed"));
            }
        }
    }

    @Override
    public void onMobileConnected() {
        UDView userdata = getUserdata();
        if (userdata != null) {
            if (userdata.getCallback() != null && userdata.getCallback().istable()) {
                LuaUtil.callFunction(LuaUtil.getFunction(userdata.getCallback(), "onMobileConnected", "OnMobileConnected"));
            }
        }
    }

    @Override
    public void onWifiConnected() {
        UDView userdata = getUserdata();
        if (userdata != null) {
            if (userdata.getCallback() != null && userdata.getCallback().istable()) {
                LuaUtil.callFunction(LuaUtil.getFunction(userdata.getCallback(), "onWifiConnected", "OnWifiConnected"));
            }
        }
    }

    //----------------------------------------getter and setter-------------------------------------

    /**
     * create a default render target (viewgroup)
     *
     * @return
     */
    public ILVViewGroup createDefaultRenderTarget() {
        return new LVViewGroup(mGlobals, createMetaTableForLuaView(), null);
    }

    /**
     * set render target
     *
     * @param viewGroup
     * @return
     */
    public LuaView setRenderTarget(ILVViewGroup viewGroup) {
        this.mRenderTarget = viewGroup;
        return this;
    }

    /**
     * get render target
     *
     * @return
     */
    private ILVViewGroup getRenderTarget() {
        return mRenderTarget != null ? mRenderTarget : this;
    }

    //----------------------------------------显示的生命周期 管理-------------------------------------

    /**
     * View初始化的时候注册监听
     */
    private void onAttached() {
    }

    /**
     * 显示
     *
     * @param visibility
     */
    private void onShow(int visibility) {
        if (visibility == View.VISIBLE) {//onShow
            NetworkUtil.registerConnectionChangeListener(getContext(), this);//show之前注册
        }
    }

    /**
     * 隐藏
     *
     * @param visibility
     */
    private void onHide(int visibility) {
        if (visibility != View.VISIBLE) {//onHide
            NetworkUtil.unregisterConnectionChangeListener(getContext(), this);//hide之后调用
        }
    }

    /**
     * 在onDetached的时候清空cache
     */
    private void onDetached() {
        if (mLuaCache != null) {//清空cache数据
            mLuaCache.clearCachedObjects();//从window中移除的时候清理数据(临时的数据)
        }
        LuaCache.clear();
    }


    /**
     * 销毁的时候从外部调用，清空所有外部引用
     */
    public void onDestroy() {
        super.onDestroy();
    }

    //----------------------------------------cached object 管理-------------------------------------
    public void cacheObject(Class type, LuaCache.CacheableObject obj) {
        if (mLuaCache != null) {
            mLuaCache.cacheObject(type, obj);
        }
    }
}
