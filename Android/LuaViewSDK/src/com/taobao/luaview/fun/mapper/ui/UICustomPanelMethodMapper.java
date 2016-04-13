package com.taobao.luaview.fun.mapper.ui;


import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.ui.UDCustomPanel;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * 自定义面板
 *
 * @param <U>
 * @author song
 */
@LuaViewLib
public class UICustomPanelMethodMapper<U extends UDCustomPanel> extends UIViewGroupMethodMapper<U> {


    /**
     * 获取native view
     *
     * @param customPanel
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue nativeView(U customPanel, Varargs varargs) {
        return getNativeView(customPanel, varargs);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue getNativeView(U customPanel, Varargs varargs) {
        return customPanel.getNativeView();
    }
}