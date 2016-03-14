package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDViewPager;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

@LuaViewLib
public class UIViewPagerMethodMapper<U extends UDViewPager> extends UIViewGroupMethodMapper<U> {

    /**
     * 重新更新数据
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue reload(U view, Varargs varargs) {
        return view.reload();
    }


    /**
     * 指示器
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue indicator(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setIndicator(view, varargs);
        } else {
            return getIndicator(view, varargs);
        }
    }

    public LuaValue setIndicator(U view, Varargs varargs) {
        final LuaValue indicator = varargs.arg(2);
        return view.setViewPagerIndicator(indicator);
    }

    public LuaValue getIndicator(U view, Varargs varargs) {
        return view.getViewPagerIndicator();
    }


    /**
     * 设置第几页面
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue currentPage(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCurrentPage(view, varargs);
        } else {
            return getCurrentPage(view, varargs);
        }
    }

    public LuaValue setCurrentPage(U view, Varargs varargs) {
        return setCurrentItem(view, varargs);
    }

    public LuaValue getCurrentPage(U view, Varargs varargs) {
        return getCurrentItem(view, varargs);
    }

    /**
     * 当前是第几页
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue currentItem(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCurrentItem(view, varargs);
        } else {
            return getCurrentItem(view, varargs);
        }
    }

    public LuaValue setCurrentItem(U view, Varargs varargs) {
        final int currentItem = LuaUtil.toJavaInt(varargs.arg(2));
        final boolean smoothScroll = varargs.optboolean(3, true);
        return view.setCurrentItem(currentItem, smoothScroll);
    }

    public LuaValue getCurrentItem(U view, Varargs varargs) {
        return LuaUtil.toLuaInt(view.getCurrentItem());
    }

}