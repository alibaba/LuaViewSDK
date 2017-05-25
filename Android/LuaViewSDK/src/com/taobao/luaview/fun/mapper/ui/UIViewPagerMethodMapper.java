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
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.ui.UDViewPager;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.viewpager.AutoScrollViewPager;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

@LuaViewLib(revisions = {"20170306已对标"})
public class UIViewPagerMethodMapper<U extends UDViewPager> extends UIViewGroupMethodMapper<U> {

    private static final String TAG = "UIViewPagerMethodMapper";
    private static final String[] sMethods = new String[]{
            "reload",//0
            "indicator",//1
            "currentPage",//2
            "currentItem",//3
            "autoScroll",//4
            "looping",//5
            "previewSide"//6
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
                return reload(target, varargs);
            case 1:
                return indicator(target, varargs);
            case 2:
                return currentPage(target, varargs);
            case 3:
                return currentItem(target, varargs);
            case 4:
                return autoScroll(target, varargs);
            case 5:
                return looping(target, varargs);
            case 6:
                return previewSide(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    /**
     * 支持左右透出预览
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_570)
    public LuaValue previewSide(U view, Varargs varargs) {
        Integer left = LuaUtil.getInt(varargs, 2);
        Integer right = LuaUtil.getInt(varargs, 3);
        return view.previewSide(left, right);
    }

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
    @Deprecated
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

    /**
     * 自动滚动
     */
    @LuaViewApi(since = VmVersion.V_501)
    public LuaValue autoScroll(U view, Varargs varargs) {
        Integer duration = LuaUtil.getInt(varargs, 2);
        duration = duration != null ? duration * 1000 : AutoScrollViewPager.DEFAULT_INTERVAL;
        final boolean reverseDirection = LuaUtil.getBoolean(varargs, false, 3);
        return view.setAutoScroll(duration, reverseDirection);
    }

    /**
     * 是否循环滚动
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_501)
    public LuaValue looping(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setLooping(view, varargs);
        } else {
            return isLooping(view, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_501)
    public LuaValue setLooping(U view, Varargs varargs) {
        final boolean looping = LuaUtil.getBoolean(varargs, false, 2);
        return view.setLooping(looping);
    }

    @LuaViewApi(since = VmVersion.V_501)
    public LuaValue isLooping(U view, Varargs varargs) {
        return valueOf(view.isLooping());
    }

    @Override
    public Varargs initParams(U view, Varargs varargs) {
        Varargs ret = super.initParams(view, varargs);
        this.reload(view, varargs);
        return ret;
    }
}