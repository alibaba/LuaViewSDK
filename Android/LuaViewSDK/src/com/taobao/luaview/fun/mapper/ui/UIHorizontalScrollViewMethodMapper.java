/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDHorizontalScrollView;
import com.taobao.luaview.util.DimenUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * HorizontalScrollView的方法映射
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UIHorizontalScrollViewMethodMapper<U extends UDHorizontalScrollView> extends UIViewGroupMethodMapper<U> {

    private static final String TAG = "UIHorizontalScrollViewMethodMapper";
    private static final String[] sMethods = new String[]{
            "offset",//0
            "scrollTo",//1
            "offsetBy",//2
            "scrollBy",//3
            "smoothScrollTo",//4
            "smoothScrollBy",//5
            "pageScroll",//6
            "fullScroll",//7
            "contentSize"//8
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
                return offset(target, varargs);
            case 1:
                return scrollTo(target, varargs);
            case 2:
                return offsetBy(target, varargs);
            case 3:
                return scrollBy(target, varargs);
            case 4:
                return smoothScrollTo(target, varargs);
            case 5:
                return smoothScrollBy(target, varargs);
            case 6:
                return pageScroll(target, varargs);
            case 7:
                return fullScroll(target, varargs);
            case 8:
                return contentSize(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    /**
     * 滚动到某个位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs offset(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOffset(view, varargs);
        } else {
            return getOffset(view, varargs);
        }
    }

    public LuaValue setOffset(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        final boolean smooth = varargs.optboolean(4, false);
        if (smooth) {
            return view.smoothScrollTo(x, y);
        } else {
            return view.scrollTo(x, y);
        }
    }

    public Varargs getOffset(U view, Varargs varargs) {
        return getOffsetXY(view, varargs);
    }


    /**
     * 滚动到某个位置
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue scrollTo(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        final boolean smooth = varargs.optboolean(4, false);
        if (smooth) {
            return view.smoothScrollTo(x, y);
        } else {
            return view.scrollTo(x, y);
        }
    }

    /**
     * 滚动到某个位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs offsetBy(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOffsetBy(view, varargs);
        } else {
            return getOffsetBy(view, varargs);
        }
    }

    public LuaValue setOffsetBy(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        final boolean smooth = varargs.optboolean(4, false);
        if (smooth) {
            return view.smoothScrollBy(x, y);
        } else {
            return view.scrollBy(x, y);
        }
    }

    public Varargs getOffsetBy(U view, Varargs varargs) {
        return getOffsetXY(view, varargs);
    }

    /**
     * 滚动一段距离
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue scrollBy(U view, Varargs varargs) {
        final int dx = DimenUtil.dpiToPx(varargs.arg(2));
        ;
        final int dy = DimenUtil.dpiToPx(varargs.arg(3));
        final boolean smooth = varargs.optboolean(4, false);
        if (smooth) {
            return view.smoothScrollBy(dx, dy);
        } else {
            return view.scrollBy(dx, dy);
        }
    }

    /**
     * 滚动到某个位置
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue smoothScrollTo(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        return view.smoothScrollTo(x, y);
    }

    /**
     * 滚动一段距离
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue smoothScrollBy(U view, Varargs varargs) {
        final int dx = DimenUtil.dpiToPx(varargs.arg(2));
        final int dy = DimenUtil.dpiToPx(varargs.arg(3));
        return view.smoothScrollBy(dx, dy);
    }

    /**
     * scroll one page
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue pageScroll(U view, Varargs varargs) {
        final int direction = varargs.optint(2, 0);
        return view.pageScroll(direction);
    }

    /**
     * scroll whole page
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue fullScroll(U view, Varargs varargs) {
        final int direction = varargs.optint(2, 0);
        return view.fullScroll(direction);
    }

    /**
     * 空实现
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(revisions = {"iOS特有，待沟通"})
    public LuaValue contentSize(U view, Varargs varargs) {
        return view;
    }

}