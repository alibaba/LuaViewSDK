/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;
import com.taobao.luaview.userdata.list.UDBaseListOrRecyclerView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Recycler View or List View
 *
 * @author song
 * @date 15/11/30
 */
@LuaViewLib(revisions = {"20170306已对标"})
public abstract class UIBaseListOrRecyclerViewMethodMapper<U extends UDViewGroup> extends UIViewGroupMethodMapper<U> {
    private static final String TAG = "UIBaseListOrRecyclerViewMethodMapper";
    private static final String[] sMethods = new String[]{
            "reload",//0
            "contentSize",//1
            "contentOffset",//2
            "contentInset",//3
            "showScrollIndicator",//4
            "scrollToTop",//5
            "scrollToCell",//6
            "miniSpacing",//7
            "lazyLoad"//8
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode){
            case 0:
                return reload(target, varargs);
            case 1:
                return contentSize(target, varargs);
            case 2:
                return contentOffset(target, varargs);
            case 3:
                return contentInset(target, varargs);
            case 4:
                return showScrollIndicator(target, varargs);
            case 5:
                return scrollToTop(target, varargs);
            case 6:
                return scrollToCell(target, varargs);
            case 7:
                return miniSpacing(target, varargs);
            case 8:
                return lazyLoad(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    public abstract UDBaseListOrRecyclerView getUDBaseListOrRecyclerView(Varargs varargs);

    /**
     * 重新刷新加载TableView
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue reload(U view, Varargs varargs) {
        final Integer section = LuaUtil.toJavaInt(LuaUtil.getInt(varargs, 2));//get section
        final Integer row = LuaUtil.toJavaInt(LuaUtil.getInt(varargs, 3));//get row
        return getUDBaseListOrRecyclerView(varargs).reload(section, row);
    }


    /**
     * ()	获取ContentSize大小
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue contentSize(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setContentSize(view, varargs);
        } else {
            return getContentSize(view, varargs);
        }
    }

    public LuaValue setContentSize(U view, Varargs varargs) {
        //TODO
        //DimenUtil.dpiToPx(varargs.arg(2));
        return view;
    }

    public LuaValue getContentSize(U view, Varargs varargs) {
        //TODO
        //DimenUtil.pxToDpi(px);
        return view;
    }

    /**
     * 获取contentOffset大小
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue contentOffset(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setContentOffset(view, varargs);
        } else {
            return getContentOffset(view, varargs);
        }
    }

    public LuaValue setContentOffset(U view, Varargs varargs) {
        //TODO
        //DimenUtil.dpiToPx(varargs.arg(2));
        return view;
    }

    public LuaValue getContentOffset(U view, Varargs varargs) {
        //TODO
        //DimenUtil.pxToDpi(px);
        return view;
    }

    /**
     * ()	获取contentInset
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue contentInset(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setContentInset(view, varargs);
        } else {
            return getContentInset(view, varargs);
        }
    }

    public LuaValue setContentInset(U view, Varargs varargs) {
        //TODO
        //DimenUtil.dpiToPx(varargs.arg(2));
        return view;
    }

    public LuaValue getContentInset(U view, Varargs varargs) {
        //TODO
        //DimenUtil.pxToDpi(px);
        return view;
    }


    /**
     * ()	获取是否显示滚动条信息
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue showScrollIndicator(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setShowScrollIndicator(view, varargs);
        } else {
            return isShowScrollIndicator(view, varargs);
        }
    }

    public LuaValue setShowScrollIndicator(U view, Varargs varargs) {
        final boolean isShow = varargs.optboolean(2, true);
        return view.setVerticalScrollBarEnabled(isShow);
    }

    public LuaValue isShowScrollIndicator(U view, Varargs varargs) {
        return valueOf(view.isVerticalScrollBarEnabled());
    }


    /**
     * (x, y, w, h, animate)	滚动到指定位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue scrollToTop(U view, Varargs varargs) {
        final int offset = varargs.isnumber(2) ? DimenUtil.dpiToPx(varargs.arg(2)) : 0;
        final boolean animate = varargs.isnumber(2) ? varargs.optboolean(3, true) : varargs.optboolean(2, true);
        return getUDBaseListOrRecyclerView(varargs).scrollToTop(offset, animate);
    }

    /**
     * (x, y, w, h, animate)	滚动到指定位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue scrollToCell(U view, Varargs varargs) {
        final int section = LuaUtil.toJavaInt(varargs.arg(2));//lua从1开始
        final int rowInSection = LuaUtil.toJavaInt(varargs.arg(3));//lua从1开始
        final int offset = DimenUtil.dpiToPx(varargs.arg(4));
        final boolean animate = varargs.optboolean(5, true);
        return getUDBaseListOrRecyclerView(varargs).scrollToItem(section, rowInSection, offset, animate);
    }


    /**
     * 分隔线高度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue miniSpacing(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setMiniSpacing(view, varargs);
        } else {
            return getMiniSpacing(view, varargs);
        }
    }

    public LuaValue setMiniSpacing(U view, Varargs varargs) {
        final UDBaseListOrRecyclerView udBaseListOrRecyclerView = getUDBaseListOrRecyclerView(varargs);
        final int spacing = DimenUtil.dpiToPx(varargs.arg(2));
        return udBaseListOrRecyclerView.setMiniSpacing(spacing);
    }

    public LuaValue getMiniSpacing(U view, Varargs varargs) {
        final UDBaseListOrRecyclerView udBaseListOrRecyclerView = getUDBaseListOrRecyclerView(varargs);
        return valueOf(DimenUtil.pxToDpi(udBaseListOrRecyclerView.getMiniSpacing()));
    }

    /**
     * 是否使用列表ImageView延迟加载，默认为true，如果列表简单的话，可以关闭
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue lazyLoad(U view, Varargs varargs) {
        final UDBaseListOrRecyclerView udBaseListOrRecyclerView = getUDBaseListOrRecyclerView(varargs);
        Boolean lazyLoad = LuaUtil.getBoolean(varargs, 2);
        return udBaseListOrRecyclerView.setLazyLoad(lazyLoad != null ? lazyLoad : true);
    }
}