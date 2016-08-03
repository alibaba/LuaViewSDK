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
    private static final String TAG = UICustomViewPagerIndicatorMethodMapper.class.getSimpleName();

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), new String[]{
                "currentPage",//0
                "currentItem"//1
        });
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int opcode = code - getFirstFunctionOpcode();
        switch (opcode) {
            case 0:
                return currentPage(target, varargs);
            case 1:
                return currentItem(target, varargs);
            default:
                return super.invoke(code, target, varargs);
        }
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