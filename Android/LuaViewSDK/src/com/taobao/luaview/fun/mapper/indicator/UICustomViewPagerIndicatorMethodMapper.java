package com.taobao.luaview.fun.mapper.indicator;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;
import com.taobao.luaview.userdata.indicator.UDCustomViewPagerIndicator;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Arrays;
import java.util.List;

/**
 * Method Mapper for Custom View Pager
 *
 * @param <U>
 * @author song
 */
@LuaViewLib
public class UICustomViewPagerIndicatorMethodMapper<U extends UDCustomViewPagerIndicator> extends UIViewMethodMapper<U> {

    @Override
    public List<String> getFunctionNames() {
        return Arrays.asList(new String[]{
                "unselectedColor",//0
                "selectedColor",//1
                "fillColor",//2
                "pageColor",//3
                "strokeWidth",//4
                "strokeColor",//5
                "radius",//6
                "snap",//7
                "currentPage",//8
                "currentItem"//9
        });
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        switch (code) {
            case 0:
                return unselectedColor(target, varargs);
            case 1:
                return selectedColor(target, varargs);
            case 2:
                return fillColor(target, varargs);
            case 3:
                return pageColor(target, varargs);
            case 4:
                return strokeWidth(target, varargs);
            case 5:
                return strokeColor(target, varargs);
            case 6:
                return radius(target, varargs);
            case 7:
                return snap(target, varargs);
            case 8:
                return currentPage(target, varargs);
            case 9:
                return currentItem(target, varargs);
        }
        return null;
    }


    /**
     * 滚动到第几页面
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue currentPage(U view, Varargs varargs) {
        return currentItem(view, varargs);
    }

    public LuaValue setCurrentPage(U view, Varargs varargs) {
        return setCurrentItem(view, varargs);
    }

    public LuaValue getCurrentPage(U view, Varargs varargs) {
        return getCurrentItem(view, varargs);
    }

    /**
     * 设置滚动到第几页
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
        final int item = varargs.optint(2, -1);
        return view.setCurrentItem(item);
    }

    public LuaValue getCurrentItem(U view, Varargs varargs) {
        return LuaUtil.toLuaInt(view.getCurrentItem());
    }


}