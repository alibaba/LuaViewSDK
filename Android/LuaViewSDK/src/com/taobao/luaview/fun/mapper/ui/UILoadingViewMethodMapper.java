package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDLoadingView;
import com.taobao.luaview.util.ColorUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Loading View 接口封装
 * @author song
 * @param <U>
 */
@LuaViewLib
public class UILoadingViewMethodMapper<U extends UDLoadingView> extends UIViewGroupMethodMapper<U> {

    /**
     * 菊花开始转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue start(U view, Varargs varargs) {
        return view.show();
    }

    public LuaValue isStart(U view, Varargs varargs) {
        return valueOf(view.isShow());
    }

    /**
     * 菊花开始转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue startAnimating(U view, Varargs varargs) {
        return view.show();
    }

    public LuaValue isAnimating(U view, Varargs varargs) {
        return valueOf(view.isShow());
    }


    /**
     * 菊花停止转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue stop(U view, Varargs varargs) {
        return view.hide();
    }

    public LuaValue stopAnimating(U view, Varargs varargs) {
        return view.hide();
    }

    /**
     * 颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue color(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setColor(view, varargs);
        } else {
            return getColor(view, varargs);
        }
    }

    public LuaValue setColor(U view, Varargs varargs) {
        final int color = ColorUtil.parse(varargs.optvalue(2, NIL));
        return view.setColor(color);
    }

    public LuaValue getColor(U view, Varargs varargs) {
        return view;
    }

}