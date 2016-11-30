package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;

/**
 * View 接口封装
 * TODO onShow, onHide, onBack只实现在了ViewGroup，后续可以考虑迁移到基础View中
 *
 * @param <U>
 * @author song
 */
@LuaViewLib
public class UIViewGroupMethodMapper<U extends UDViewGroup> extends UIViewMethodMapper<U> {

    private static final String TAG = UIViewGroupMethodMapper.class.getSimpleName();
    private static final String[] sMethods = new String[]{
            "onShow",//0
            "onHide",//1
            "onBack",//2
            "onLayout",//3
            "addView",//4
            "removeView",//5
            "removeAllViews",//6
            "children",//7
            "flexChildren",//8
            "onDraw",//9
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
                return onShow(target, varargs);
            case 1:
                return onHide(target, varargs);
            case 2:
                return onBack(target, varargs);
            case 3:
                return onLayout(target, varargs);
            case 4:
                return addView(target, varargs);
            case 5:
                return removeView(target, varargs);
            case 6:
                return removeAllViews(target, varargs);
            case 7:
                return children(target, varargs);
            case 8:
                return flexChildren(target, varargs);
            case 9:
                return onDraw(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    /**
     * onShow
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onShow(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnShow(view, varargs);
        } else {
            return getOnShow(view, varargs);
        }
    }

    public LuaValue setOnShow(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnShowCallback(callbacks);
    }

    public LuaValue getOnShow(U view, Varargs varargs) {
        return view.getOnShowCallback();
    }

    /**
     * onHide
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onHide(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnHide(view, varargs);
        } else {
            return getOnHide(view, varargs);
        }
    }

    public LuaValue setOnHide(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnHideCallback(callbacks);
    }

    public LuaValue getOnHide(U view, Varargs varargs) {
        return view.getOnHideCallback();
    }

    /**
     * onBack
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onBack(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnBack(view, varargs);
        } else {
            return getOnBack(view, varargs);
        }
    }

    public LuaValue setOnBack(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnBackCallback(callbacks);
    }

    public LuaValue getOnBack(U view, Varargs varargs) {
        return view.getOnBackCallback();
    }

    /**
     * onLayout
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onLayout(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnLayout(view, varargs);
        } else {
            return getOnLayout(view, varargs);
        }
    }

    public LuaValue setOnLayout(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnLayoutCallback(callbacks);
    }

    public LuaValue getOnLayout(U view, Varargs varargs) {
        return view.getOnLayoutCallback();
    }


    /**
     * onDraw
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_550)
    public LuaValue onDraw(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnDraw(view, varargs);
        } else {
            return getOnDraw(view, varargs);
        }
    }

    public LuaValue setOnDraw(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setOnDrawCallback(callbacks);
    }

    public LuaValue getOnDraw(U view, Varargs varargs) {
        return view.getOnDrawCallback();
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
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue children(U view, Varargs varargs) {
        final LuaFunction callback = LuaUtil.getFunction(varargs, 2);
        return view.children(callback);
    }

    /**
     * Flexbox 设置childViews
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue flexChildren(U view, Varargs varargs) {
        ArrayList<UDView> flexChildren = new ArrayList<UDView>();
        for (int i = 2; i <= varargs.narg(); i++) {
            LuaValue luaValue = varargs.optvalue(i, null);
            if (luaValue != null && luaValue instanceof UDView) {
                flexChildren.add((UDView) luaValue);
            }
        }
        view.setChildNodeViews(flexChildren);

//        final LuaTable children = LuaUtil.getTable(varargs, 2);
//        if (children != null) {
//            ArrayList<UDView> flexChildren = new ArrayList<UDView>();
//            for(int i = 0; i < children.length(); i++){
//                LuaValue child = children.get(i + 1);
//                if(child != null && child instanceof UDView){
//                    flexChildren.add((UDView)child);
//                }
//            }
//            view.setChildNodeViews(flexChildren);
//        }

        return view;
    }
}