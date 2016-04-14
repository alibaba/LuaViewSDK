package com.taobao.luaview.fun.mapper.indicator;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.indicator.UDCircleViewPagerIndicator;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * method mapper for PagerIndicator
 * @param <U>
 */
@LuaViewLib
public class UICircleViewPagerIndicatorMethodMapper<U extends UDCircleViewPagerIndicator> extends UIViewMethodMapper<U> {


    /**
     * 设置未选中颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue unselectedColor(U view, Varargs varargs) {
        return fillColor(view, varargs);
    }

    public LuaValue setUnselectedColor(U view, Varargs varargs) {
        return setFillColor(view, varargs);
    }

    public LuaValue getUnselectedColor(U view, Varargs varargs) {
        return getFileColor(view, varargs);
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
        return pageColor(view, varargs);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue setSelectedColor(U view, Varargs varargs) {
        return setPageColor(view, varargs);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue getSelectedColor(U view, Varargs varargs) {
        return getPageColor(view, varargs);
    }

    /**
     * 设置未选中颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue fillColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {//set
            return setFillColor(view, varargs);
        } else {
            return getFileColor(view, varargs);
        }
    }

    public LuaValue setFillColor(U view, Varargs varargs) {
        final int color = ColorUtil.parse(varargs.optvalue(2, NIL));
        return view.setFillColor(color);
    }

    public LuaValue getFileColor(U view, Varargs varargs) {
        return valueOf(view.getFillColor());
    }


    /**
     * 设置颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue pageColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setPageColor(view, varargs);
        } else {
            return getPageColor(view, varargs);
        }
    }

    public LuaValue setPageColor(U view, Varargs varargs) {
        final int color = ColorUtil.parse(varargs.optvalue(2, NIL));
        return view.setPageColor(color);
    }

    public LuaValue getPageColor(U view, Varargs varargs) {
        return valueOf(view.getPageColor());
    }

    /**
     * 设置线条宽度
     *
     * @param view
     * @param varargs
     * @return
     */
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
    public LuaValue strokeColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setStrokeColor(view, varargs);
        } else {
            return getStrokeColor(view, varargs);
        }
    }

    public LuaValue setStrokeColor(U view, Varargs varargs) {
        final int color = ColorUtil.parse(varargs.optvalue(2, NIL));
        return view.setStrokeColor(color);
    }

    public LuaValue getStrokeColor(U view, Varargs varargs) {
        return valueOf(view.getStrokeColor());
    }

    /**
     * 设置半径
     *
     * @param view
     * @param varargs
     * @return
     */
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