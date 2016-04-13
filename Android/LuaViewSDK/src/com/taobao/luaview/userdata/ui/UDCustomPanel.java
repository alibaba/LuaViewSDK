package com.taobao.luaview.userdata.ui;

import android.view.View;

import com.taobao.luaview.view.LVCustomPanel;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * 自定义容器类
 *
 * @author song
 * @date 15/8/20
 */
public class UDCustomPanel<T extends LVCustomPanel> extends UDViewGroup<T> {
    public UDCustomPanel(T view, Globals globals, LuaValue metatable, LuaValue initParams) {
        super(view, globals, metatable, initParams);
    }

    /**
     * 获取native view
     *
     * @return
     */
    public LuaValue getNativeView() {
        if (getView() != null) {
            View view = getView().getNativeView();
            return view != null ? CoerceJavaToLua.coerce(view) : LuaValue.NIL;
        }
        return LuaValue.NIL;
    }
}
