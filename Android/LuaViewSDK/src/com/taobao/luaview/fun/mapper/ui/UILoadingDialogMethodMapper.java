package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDLoadingDialog;
import com.taobao.luaview.util.ColorUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

@LuaViewLib
public class UILoadingDialogMethodMapper<U extends UDLoadingDialog> extends BaseMethodMapper<U> {

    /**
     * 菊花开始转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue show(U view, Varargs varargs) {
        return view.startAnimating();
    }

    public LuaValue isShow(U view, Varargs varargs) {
        return valueOf(view.isAnimating());
    }


    /**
     * 菊花开始转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue start(U view, Varargs varargs) {
        return view.startAnimating();
    }

    public LuaValue isStart(U view, Varargs varargs) {
        return valueOf(view.isAnimating());
    }

    /**
     * 菊花开始转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue startAnimating(U view, Varargs varargs) {
        return view.startAnimating();
    }

    public LuaValue isAnimating(U view, Varargs varargs) {
        return valueOf(view.isAnimating());
    }


    /**
     * 菊花停止转动
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue hide(U view, Varargs varargs) {
        return view.stopAnimating();
    }

    public LuaValue stop(U view, Varargs varargs) {
        return view.stopAnimating();
    }

    public LuaValue stopAnimating(U view, Varargs varargs) {
        return view.stopAnimating();
    }


    /**
     * 获取菊花颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs color(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setColor(view, varargs);
        } else {
            return getColor(view, varargs);
        }
    }

    public LuaValue setColor(U view, Varargs varargs) {
        final int color = ColorUtil.parse(varargs.optvalue(2, NIL));
        final int alpha = varargs.optint(3, -1);
        return view.setColorAndAlpha(color, alpha);
    }

    public Varargs getColor(U view, Varargs varargs) {
        return varargsOf(valueOf(view.getColor()), valueOf(view.getAlpha()));
    }


}