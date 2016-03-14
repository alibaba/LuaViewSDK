package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * View 接口封装
 * TODO onShow, onHide, onBack只实现在了ViewGroup，后续可以考虑迁移到基础View中
 * @author song
 * @param <U>
 */
@LuaViewLib
public class UIViewGroupMethodMapper<U extends UDViewGroup> extends UIViewMethodMapper<U> {

    /**
     * onShow
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onShow(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnShowCallback(callbacks);
    }

    /**
     * onHide
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onHide(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnHideCallback(callbacks);
    }

    /**
     * onBack
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onBack(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnBackCallback(callbacks);
    }

    /**
     * 添加子类
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue addView(U view, Varargs varargs) {
        final LuaValue luaValue = varargs.optvalue(2, null);
        if (luaValue instanceof UDView) {
            return view.addView((UDView) luaValue);
        }
        return view;
    }

    /**
     * 移除子类
     *
     * @param view
     * @return
     */
    public LuaValue removeView(U view, Varargs varargs) {
        final LuaValue luaValue = varargs.optvalue(2, null);
        if (luaValue instanceof UDView) {
            return view.removeView((UDView) luaValue);
        }
        return view;
    }

    /**
     * 移除所有子类
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue removeAllViews(U view, Varargs varargs) {
        return view.removeAllViews();
    }


    /**
     * 设置子View的构造环境，可以方便地构造子view，不用显式remove & add了
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue children(U view, Varargs varargs){
        final LuaFunction callback = LuaUtil.getFunction(varargs, 2);
        return view.children(callback);
    }
}