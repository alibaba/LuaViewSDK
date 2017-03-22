/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.indicator;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.indicator.UDCircleViewPagerIndicator;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;


/**
 * method mapper for PagerIndicator
 *
 * @param <U>
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UICircleViewPagerIndicatorMethodMapper<U extends UDCircleViewPagerIndicator> extends UIViewMethodMapper<U> {
    private static final String TAG = "UICircleViewPagerIndicatorMethodMapper";
    private static final String[] sMethods = new String[]{
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
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    /**
     * 设置未选中颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue unselectedColor(U view, Varargs varargs) {
        return pageColor(view, varargs);
    }

    public LuaValue setUnselectedColor(U view, Varargs varargs) {
        return setPageColor(view, varargs);
    }

    public LuaValue getUnselectedColor(U view, Varargs varargs) {
        return getPageColor(view, varargs);
    }

    /**
     * 设置选中颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue selectedColor(U view, Varargs varargs) {
        return fillColor(view, varargs);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue setSelectedColor(U view, Varargs varargs) {
        return setFillColor(view, varargs);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue getSelectedColor(U view, Varargs varargs) {
        return getFileColor(view, varargs);
    }

    /**
     * 设置未选中颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue fillColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {//set
            return setFillColor(view, varargs);
        } else {
            return getFileColor(view, varargs);
        }
    }

    public LuaValue setFillColor(U view, Varargs varargs) {
        final Integer color = ColorUtil.parse(LuaUtil.getInt(varargs, 2));
        return view.setFillColor(color);
    }

    public LuaValue getFileColor(U view, Varargs varargs) {
        return valueOf(ColorUtil.getHexColor(view.getFillColor()));
    }


    /**
     * 设置颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue pageColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setPageColor(view, varargs);
        } else {
            return getPageColor(view, varargs);
        }
    }

    public LuaValue setPageColor(U view, Varargs varargs) {
        final Integer color = ColorUtil.parse(LuaUtil.getInt(varargs, 2));
        return view.setPageColor(color);
    }

    public LuaValue getPageColor(U view, Varargs varargs) {
        return valueOf(ColorUtil.getHexColor(view.getPageColor()));
    }

    /**
     * 设置线条宽度
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue strokeWidth(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setStrokeWidth(view, varargs);
        } else {
            return getStrokeWidth(view, varargs);
        }
    }

    public LuaValue setStrokeWidth(U view, Varargs varargs) {
        final int width = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setStrokeWidth(width);
    }

    public LuaValue getStrokeWidth(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getStrokeWidth()));
    }

    /**
     * 设置线条颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue strokeColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setStrokeColor(view, varargs);
        } else {
            return getStrokeColor(view, varargs);
        }
    }

    public LuaValue setStrokeColor(U view, Varargs varargs) {
        final Integer color = ColorUtil.parse(LuaUtil.getInt(varargs, 2));
        return view.setStrokeColor(color);
    }

    public LuaValue getStrokeColor(U view, Varargs varargs) {
        return valueOf(ColorUtil.getHexColor(view.getStrokeColor()));
    }

    /**
     * 设置半径
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue radius(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setRadius(view, varargs);
        } else {
            return getRadius(view, varargs);
        }
    }

    public LuaValue setRadius(U view, Varargs varargs) {
        final int radius = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setRadius(radius);
    }

    public LuaValue getRadius(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getRadius()));
    }

    /**
     * 设置是否移动瞬间过去
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue snap(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setSnap(view, varargs);
        } else {
            return isSnap(view, varargs);
        }
    }

    public LuaValue setSnap(U view, Varargs varargs) {
        final boolean snap = varargs.optboolean(2, false);
        return view.setSnap(snap);
    }

    public LuaValue isSnap(U view, Varargs varargs) {
        return valueOf(view.isSnap());
    }

    /**
     * 设置当前第几页
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
     * 设置当前第几页
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
        final int currentItem = varargs.optint(2, -1);
        return view.setCurrentItem(currentItem);
    }

    public LuaValue getCurrentItem(U view, Varargs varargs) {
        //TODO 这里需要获取currentItem，但是PageIndicator不支持
        return view;
    }
}