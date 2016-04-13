package com.taobao.luaview.view;

import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.userdata.ui.UDCustomPanel;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * LuaView-Container
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public abstract class LVCustomPanel extends LVViewGroup implements ILVViewGroup {
    private UDView mLuaUserdata;

    public LVCustomPanel(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
        this.mLuaUserdata = new UDCustomPanel(this, globals, metaTable, (varargs != null ? varargs.arg1() : null));
        initPanel();
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void addLVView(final View view, Varargs a) {
        final ViewGroup.LayoutParams layoutParams = LuaViewUtil.getOrCreateLayoutParams(view);
        super.addView(LuaViewUtil.removeFromParent(view), layoutParams);
    }

    public void show() {
        LVCustomPanel.this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        LVCustomPanel.this.setVisibility(View.GONE);
    }

    /**
     * 初始化Panel
     */
    public abstract void initPanel();

    /**
     * 子类实现该方法，用于Lua回调该方法
     */
    public void callLuaCallback(Object... objs) {
        final LuaValue callback = this.mLuaUserdata.getCallback();
        if (callback != null && callback.isfunction()) {
            LuaValue[] args = null;
            if (objs != null && objs.length > 0) {
                args = new LuaValue[objs.length];

                for (int i = 0; i < objs.length; i++) {
                    args[i] = CoerceJavaToLua.coerce(objs[i]);
                }
            }
            if (args != null) {
                callback.invoke(LuaValue.varargsOf(args));
            } else {
                callback.call();
            }
        }
    }

    //获取native view
    public View getNativeView() {
        if (getChildCount() > 0 && getChildAt(0) != null) {
            return getChildAt(0);
        }
        return null;
    }
}
