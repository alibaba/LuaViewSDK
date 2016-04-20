package com.taobao.luaview.global;

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;

import com.taobao.luaview.debug.DebugConnection;
import com.taobao.luaview.exception.LuaViewException;
import com.taobao.luaview.extend.LuaCache;
import com.taobao.luaview.fun.binder.ui.UICustomPanelBinder;
import com.taobao.luaview.fun.mapper.ui.NewIndexFunction;
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
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

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
            @NonNull
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

    @NonNull
    private static Globals createGlobals(final Context context) {
        final Globals globals = LuaViewConfig.isOpenDebugger() ? JsePlatform.debugGlobals() : JsePlatform.standardGlobals();//加载系统libs
        globals.context = context;
        LuaViewManager.loadLuaViewLibs(globals);//加载用户lib
        return globals;
    }

    @NonNull
    private static LuaView createLuaView(final Context context, final Globals globals) {
        final LuaView luaView = new LuaView(globals, createMetaTableForLuaView());
        globals.finder = new LuaResourceFinder(context);
        globals.luaView = luaView;
        if (LuaViewConfig.isOpenDebugger()) {//如果是debug，支持ide调试
            luaView.turnDebug();
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
    public LuaView load(final String urlOrFileOrScript, final LuaScriptLoader.ScriptLoaderCallback callback) {
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

    public LuaView load(final String urlOrFileOrScript, final String sha256, final LuaScriptLoader.ScriptLoaderCallback callback) {
        if (!TextUtils.isEmpty(urlOrFileOrScript)) {
            if (URLUtil.isNetworkUrl(urlOrFileOrScript)) {//url, http:// or https://
                loadUrl(urlOrFileOrScript, sha256, callback);
            } else {
                loadFile(urlOrFileOrScript);
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

    public LuaView loadUrl(final String url, final String sha256, final LuaScriptLoader.ScriptLoaderCallback callback) {
        updateUri(url);
        if (!TextUtils.isEmpty(url)) {
            if (callback != null) {
                new LuaScriptLoader(getContext()).load(url, sha256, callback);
            } else {
                new LuaScriptLoader(getContext()).load(url, sha256, new LuaScriptLoader.ScriptLoaderCallback() {
                    @Override
                    public void onScriptLoaded(final ScriptBundle bundle) {
                        loadScriptBundle(bundle);
                    }
                });
            }
        }
        return this;
    }

    /**
     * 加载 Asset路径下的脚本
     *
     * @param assetPath folder path or file path
     * @param callback
     * @return
     */
    private LuaView loadAsset(final String assetPath, final LuaScriptLoader.ScriptLoaderCallback callback) {
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
        updateUri(luaFileName);
        if (!TextUtils.isEmpty(luaFileName)) {
            mGlobals.saveContainer(getRenderTarget());
            mGlobals.set("window", this.getUserdata());//TODO 优化到其他地方?，设置window对象
            this.loadFileInternal(luaFileName);//加载文件
            mGlobals.restoreContainer();
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
        if (!TextUtils.isEmpty(script)) {
            mGlobals.saveContainer(getRenderTarget());
            mGlobals.set("window", this.getUserdata());//TODO 优化到其他地方?，设置window对象
            this.loadScriptInternal(EncryptUtil.md5Hex(script), script);
            mGlobals.restoreContainer();
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
        if (scriptFile != null) {
            mGlobals.saveContainer(getRenderTarget());
            mGlobals.set("window", this.getUserdata());//TODO 优化到其他地方?，设置window对象
            this.loadScriptInternal(scriptFile.fileName, scriptFile.script);
            mGlobals.restoreContainer();
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
        return loadScriptBundle(scriptBundle, LuaResourceFinder.DEFAULT_MAIN_ENTRY);
    }

    /**
     * 加载 Script Bundle
     *
     * @param scriptBundle
     * @return
     */
    public LuaView loadScriptBundle(final ScriptBundle scriptBundle, final String mainScriptFileName) {
        if (scriptBundle != null) {
            if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
                mGlobals.getLuaResourceFinder().setScriptBundle(scriptBundle);
            }
            if (scriptBundle.containsKey(mainScriptFileName)) {
                final ScriptFile scriptFile = scriptBundle.getScriptFile(mainScriptFileName);
                loadScript(scriptFile);
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
                mGlobals.load(binder);
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
            LogUtil.d("name " + luaName + " is invalid!");
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
                mGlobals.load(new UICustomPanelBinder(clazz, luaName));
            } else {
                LogUtil.d("panel name " + luaName + " is already registered!");
            }
        } else {
            LogUtil.d("name " + luaName + " is invalid or Class " + clazz + " is not subclass of " + LVCustomPanel.class.getSimpleName());
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
        final LuaTable libOfLuaViews = LuaViewManager.bind(UIViewGroupMethodMapper.class, UIViewGroupMethodMapper.class.getMethods());
        return LuaValue.tableOf(new LuaValue[]{LuaValue.INDEX, libOfLuaViews, LuaValue.NEWINDEX, new NewIndexFunction(libOfLuaViews)});
    }

    private LuaView(Globals globals, LuaValue metaTable) {
        super(globals, metaTable, LuaValue.NIL);
        this.mLuaCache = new LuaCache();
    }

    /**
     * 开启debug
     */
    private void turnDebug() {
        loadFile("debug.lua");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectNetwork()   // or .detectAll() for all detectable problems，主线程执行socket
                .build());
        mGlobals.debugConnection = DebugConnection.create();
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
    private LuaView loadFileInternal(final String luaFileName) {
        try {
            final LuaValue activity = CoerceJavaToLua.coerce(getContext());
            final LuaValue viewObj = CoerceJavaToLua.coerce(this);
            mGlobals.loadfile(luaFileName).call(activity, viewObj);
        } catch (Exception e) {
            LogUtil.e("[Load File Failed]", luaFileName, e);
            e.printStackTrace();
            throw new LuaViewException(e);
        } finally {
            return this;
        }
    }

    /**
     * 初始化
     *
     * @param luaScript
     */
    private LuaView loadScriptInternal(final String fileName, final String luaScript) {
        try {
            final LuaValue activity = CoerceJavaToLua.coerce(getContext());
            final LuaValue viewObj = CoerceJavaToLua.coerce(this);
            mGlobals.load(luaScript, fileName).call(activity, viewObj);
        } catch (Exception e) {
            LogUtil.e("[Load Script Failed]", fileName, e);
            e.printStackTrace();
            throw new LuaViewException(e);
        } finally {
            return this;
        }
    }

    //-----------------------------------------网络回调----------------------------------------------

    /**
     * 当显示的时候调用
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == View.VISIBLE) {//onShow
            NetworkUtil.registerConnectionChangeListener(getContext(), this);//show之前注册
        }
        super.onWindowVisibilityChanged(visibility);
        if (visibility != View.VISIBLE) {//onHide
            NetworkUtil.unregisterConnectionChangeListener(getContext(), this);//hide之后调用
            if (mLuaCache != null) {
                mLuaCache.clearCachedObjects();//从window中移除的时候清理数据(临时的数据)
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onAttached();
    }

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

    //----------------------------------------cached object 管理-------------------------------------

    private void onAttached() {

    }

    /**
     * 在onDetached的时候清空cache
     */
    private void onDetached() {
        LuaCache.clear();
    }

    public void cacheObject(Class type, LuaCache.CacheableObject obj) {
        if (mLuaCache != null) {
            mLuaCache.cacheObject(type, obj);
        }
    }
}
