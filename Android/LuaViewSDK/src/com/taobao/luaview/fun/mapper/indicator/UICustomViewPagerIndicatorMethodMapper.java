/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.indicator;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;
import com.taobao.luaview.userdata.indicator.UDCustomViewPagerIndicator;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Method Mapper for Custom View Pager
 *
 * @param <U>
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标", "iOS无"})
@Deprecated
public class UICustomViewPagerIndicatorMethodMapper<U extends UDCustomViewPagerIndicator> extends UIViewMethodMapper<U> {
    private static final String TAG = "UICustomViewPagerIndicatorMethodMapper";
    private static final String[] sMethods = new String[]{
            "currentPage",//0
            "currentItem"//1
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int opcode = code - super.getAllFunctionNames().size();
        switch (opcode) {
            case 0:
                return currentPage(target, varargs);
            case 1:
                return currentItem(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

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