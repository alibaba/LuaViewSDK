package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.ui.UDWebView;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Created by tuoli on 10/9/16.
 */
@LuaViewLib
public class UIWebViewMethodMapper<U extends UDWebView> extends UIViewMethodMapper<U> {

    private static final String TAG = UIWebViewMethodMapper.class.getSimpleName();
    private static final String[] sMethods = new String[]{
            "loadUrl",  //0
            "canGoBack",
            "canGoForward",
            "goBack",
            "goForward",
            "reload",
            "title",
            "isLoading",
            "stopLoading",
            "url"
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return loadUrl(target, varargs);
            case 1:
                return canGoBack(target, varargs);
            case 2:
                return canGoForward(target, varargs);
            case 3:
                return goBack(target, varargs);
            case 4:
                return goForward(target, varargs);
            case 5:
                return reload(target, varargs);
            case 6:
                return title(target, varargs);
            case 7:
                return isLoading(target, varargs);
            case 8:
                return stopLoading(target, varargs);
            case 9:
                return url(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------
    // 使用反射的方式调用的时候,需要public关键字声明

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue loadUrl(U view, Varargs varargs) {
        return view.loadUrl(LuaUtil.getString(varargs, 2));
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue canGoBack(U view, Varargs varargs) {
        return valueOf(view.canGoBack());
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue canGoForward(U view, Varargs varargs) {
        return valueOf(view.canGoForward());
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue goBack(U view, Varargs varargs) {
        return view.goBack();
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue goForward(U view, Varargs varargs) {
        return view.goForward();
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue reload(U view, Varargs varargs) {
        return view.reload();
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue title(U view, Varargs varargs) {
        return valueOf(view.title());
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue isLoading(U view, Varargs varargs) {
        return valueOf(view.isLoading());
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue stopLoading(U view, Varargs varargs) {
        return view.stopLoading();
    }

    @LuaViewApi(since = VmVersion.V_530)
    public LuaValue url(U view, Varargs varargs) {
        return valueOf(view.url());
    }
}
