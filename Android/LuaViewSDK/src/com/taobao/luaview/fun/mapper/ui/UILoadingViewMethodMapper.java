package com.taobao.luaview.fun.mapper.ui;

import android.graphics.Color;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDLoadingView;
import com.taobao.luaview.util.ColorUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Loading View 接口封装
 * @author song
 * @param <U>
 */
@LuaViewLib
public class UILoadingViewMethodMapper<U extends UDLoadingView> extends UIViewGroupMethodMapper<U> {

    private static final String TAG = UILoadingViewMethodMapper.class.getSimpleName();
    private static final String[] sMethods = new String[]{
            "start",//0
            "isStart",//1
            "startAnimating",//2
            "isAnimating",//3
            "stop",//4
            "stopAnimating",//5
            "color"//6
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
                return start(target, varargs);
            case 1:
                return isStart(target, varargs);
            case 2:
                return startAnimating(target, varargs);
            case 3:
                return isAnimating(target, varargs);
            case 4:
                return stop(target, varargs);
            case 5:
                return stopAnimating(target, varargs);
            case 6:
                return color(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


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
        final Integer color = ColorUtil.parse(varargs.optvalue(2, NIL), Color.BLACK);
        return view.setColor(color);
    }

    public LuaValue getColor(U view, Varargs varargs) {
        return view;
    }

}