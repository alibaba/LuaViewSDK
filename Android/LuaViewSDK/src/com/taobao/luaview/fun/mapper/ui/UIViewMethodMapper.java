package com.taobao.luaview.fun.mapper.ui;

import android.widget.RelativeLayout;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * 所有view 接口封装
 *
 * @param <U>
 * @author song
 */
@LuaViewLib
public class UIViewMethodMapper<U extends UDView> extends BaseMethodMapper<U> {


    /**
     * 初始化数据
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue initParams(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setInitParams(view, varargs);
        } else {
            return getInitParams(view, varargs);
        }
    }

    public LuaValue setInitParams(U view, Varargs varargs) {
        final LuaValue initParams = varargs.optvalue(2, NIL);
        return view.setInitParams(initParams);
    }

    public LuaValue getInitParams(U view, Varargs varargs) {
        return view.getInitParams();
    }

    /**
     * 刷新view
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue invalidate(U view, Varargs varargs) {
        return view.invalidate();
    }


    /**
     * 设置位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs padding(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setPadding(view, varargs);
        } else {
            return getPadding(view, varargs);
        }
    }

    public LuaValue setPadding(U view, Varargs varargs) {
        final int left = DimenUtil.dpiToPx(varargs.arg(2));
        final int top = DimenUtil.dpiToPx(varargs.arg(3));
        final int right = DimenUtil.dpiToPx(varargs.arg(4));
        final int bottom = DimenUtil.dpiToPx(varargs.arg(5));
        return view.setPadding(left, top, right, bottom);
    }

    public Varargs getPadding(U view, Varargs varargs) {
        return varargsOf(new LuaValue[]{valueOf(DimenUtil.pxToDpi(view.getPaddingLeft())), valueOf(DimenUtil.pxToDpi(view.getPaddingTop())), valueOf(DimenUtil.pxToDpi(view.getPaddingRight())), valueOf(DimenUtil.pxToDpi(view.getPaddingBottom()))});
    }


    /**
     * 获取view的位置和大小
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs frame(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setFrame(view, varargs);
        } else {
            return getFrame(view, varargs);
        }
    }

    public LuaValue setFrame(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        final int width = DimenUtil.dpiToPx(varargs.arg(4));
        final int height = DimenUtil.dpiToPx(varargs.arg(5));
        return view.setFrame(x, y, width, height);
    }

    public Varargs getFrame(U view, Varargs varargs) {
        return varargsOf(new LuaValue[]{valueOf(DimenUtil.pxToDpi(view.getX())), valueOf(DimenUtil.pxToDpi(view.getY())), valueOf(DimenUtil.pxToDpi(view.getWidth())), valueOf(DimenUtil.pxToDpi(view.getHeight()))});
    }


    /**
     * 获取背景颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs backgroundColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setBackgroundColor(view, varargs);
        } else {
            return getBackgroundColor(view, varargs);
        }
    }

    public LuaValue setBackgroundColor(final U view, final Varargs args) {
        if (args.isnumber(2)) {
            final int color = ColorUtil.parse(args.optvalue(2, NIL));
            final float alpha = (float) args.optdouble(3, 1);
            return view.setBackgroundColorAndAlpha(color, alpha);
        } else {
            final String pic = args.optjstring(2, "");
            final float alpha = (float) args.optdouble(3, 1);
            return view.setBackgroundResourceAndAlpha(pic, alpha);
        }
    }

    public Varargs getBackgroundColor(U view, Varargs varargs) {
        return varargsOf(valueOf(view.getBackgroundColor()), valueOf(view.getBackgroundAlpha()));
    }


    /**
     * 获取尺寸
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public Varargs size(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setSize(view, varargs);
        } else {
            return getSize(view, varargs);
        }
    }

    public LuaValue setSize(U view, Varargs varargs) {
        final int width = DimenUtil.dpiToPx(varargs.arg(2), -1);
        final int height = DimenUtil.dpiToPx(varargs.arg(3), -1);
        if (width != -1 && height != -1) {//两个值则设置宽高
            return view.setSize(width, height);
        }
        return view.setWidth(width);//一个值设置宽
    }

    public Varargs getSize(U view, Varargs varargs) {
        final float width = DimenUtil.pxToDpi(view.getWidth());
        final float height = DimenUtil.pxToDpi(view.getHeight());
        return varargsOf(valueOf(width), valueOf(height));
    }


    /**
     * 位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs xy(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setXY(view, varargs);
        } else {
            return getXY(view, varargs);
        }
    }

    public LuaValue setXY(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        return view.setXY(x, y);
    }

    public Varargs getXY(U view, Varargs varargs) {
        return varargsOf(valueOf(DimenUtil.pxToDpi(view.getX())), valueOf(DimenUtil.pxToDpi(view.getY())));
    }



    /**
     * 设置位于
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue align(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            ArrayList list = new ArrayList<Integer>();
            for (int i = 0; i < varargs.narg(); i++) {
                int align = varargs.optint((2 + i), -1);
                if (align != -1) {
                    list.add(align);
                }
            }
            if (list.size() > 0) {
                Integer[] aligns = new Integer[list.size()];
                list.toArray(aligns);
                return view.align(aligns);
            }
        }
        return view;
    }

    /**
     * 位于左上
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignLeft(U view, Varargs args) {
        return alignTopLeft(view, args);
    }

    /**
     * 位于左上
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignTop(U view, Varargs args) {
        return alignTopLeft(view, args);
    }

    /**
     * 位于右上
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignRight(U view, Varargs args) {
        return alignTopRight(view, args);
    }

    /**
     * 位于左下
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignBottom(U view, Varargs args) {
        return alignBottomLeft(view, args);
    }


    /**
     * 位于左上
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignLeftTop(U view, Varargs args) {
        return alignTopLeft(view, args);
    }

    public LuaValue alignTopLeft(U view, Varargs args) {
        return view.align(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT);
    }

    /**
     * 位于上中
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignCenterTop(U view, Varargs args) {
        return alignTopCenter(view, args);
    }

    public LuaValue alignTopCenter(U view, Varargs args) {
        return view.align(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.CENTER_HORIZONTAL);
    }

    /**
     * 位于左下
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignRightTop(U view, Varargs args) {
        return alignTopRight(view, args);
    }

    public LuaValue alignTopRight(U view, Varargs args) {
        return view.align(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_RIGHT);
    }

    /**
     * 位于下左
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignLeftBottom(U view, Varargs args) {
        return alignBottomLeft(view, args);
    }

    public LuaValue alignBottomLeft(U view, Varargs args) {
        return view.align(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_LEFT);
    }

    /**
     * 位于下左
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignCenterBottom(U view, Varargs args) {
        return alignBottomCenter(view, args);
    }

    public LuaValue alignBottomCenter(U view, Varargs args) {
        return view.align(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.CENTER_HORIZONTAL);
    }

    /**
     * 位于下右
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignRightBottom(U view, Varargs args) {
        return alignBottomRight(view, args);
    }

    public LuaValue alignBottomRight(U view, Varargs args) {
        return view.align(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_RIGHT);
    }


    /**
     * 设置水平、竖直居中
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignCenter(U view, Varargs args) {
        return view.align(RelativeLayout.CENTER_IN_PARENT);
    }

    /**
     * 设置水平、居左
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignLeftCenter(U view, Varargs args) {
        return alignCenterLeft(view, args);
    }

    public LuaValue alignCenterLeft(U view, Varargs args) {
        return view.align(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.ALIGN_PARENT_LEFT);
    }

    /**
     * 设置水平，居右
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignRightCenter(U view, Varargs args) {
        return alignCenterRight(view, args);
    }

    public LuaValue alignCenterRight(U view, Varargs args) {
        return view.align(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.ALIGN_PARENT_RIGHT);
    }

    /**
     * 设置水平居中
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignCenterHorizontal(U view, Varargs args) {
        return alignHorizontalCenter(view, args);
    }

    public LuaValue alignHorizontalCenter(U view, Varargs args) {
        return view.align(RelativeLayout.CENTER_HORIZONTAL);
    }

    /**
     * 设置竖直居中
     *
     * @param view UIView
     * @param args
     * @return
     */
    public LuaValue alignCenterVertical(U view, Varargs args) {
        return alignVerticalCenter(view, args);
    }

    public LuaValue alignVerticalCenter(U view, Varargs args) {
        return view.align(RelativeLayout.CENTER_VERTICAL);
    }


    /**
     * 设置中心点位置
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public Varargs center(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCenter(view, varargs);
        } else {
            return getCenter(view, varargs);
        }
    }

    public LuaValue setCenter(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        return view.setCenter(x, y);
    }

    public Varargs getCenter(U view, Varargs varargs) {
        float x = 0.0f, y = 0.0f;
        if (view != null && view.getView() != null) {
            x = view.getX() + view.getWidth() / 2.0f;
            y = view.getY() + view.getHeight() / 2.0f;
        }
        return varargsOf(valueOf(DimenUtil.pxToDpi(x)), valueOf(DimenUtil.pxToDpi(y)));
    }

    /**
     * 获取X坐标
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public LuaValue x(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setX(view, varargs);
        } else {
            return getX(view, varargs);
        }
    }

    public LuaValue setX(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setX(x);
    }

    public LuaValue getX(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getX()));
    }


    /**
     * 设置Y坐标
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public LuaValue y(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setY(view, varargs);
        } else {
            return getY(view, varargs);
        }
    }

    public LuaValue setY(U view, Varargs varargs) {
        final int y = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setY(y);
    }

    public LuaValue getY(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getY()));
    }

    /**
     * 设置left坐标
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public LuaValue left(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setLeft(view, varargs);
        } else {
            return getLeft(view, varargs);
        }
    }

    public LuaValue setLeft(U view, Varargs varargs) {
        final int left = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setX(left);
    }

    public LuaValue getLeft(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getX()));
    }

    /**
     * 设置top坐标
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public LuaValue top(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTop(view, varargs);
        } else {
            return getTop(view, varargs);
        }
    }

    public LuaValue setTop(U view, Varargs varargs) {
        final int top = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setY(top);
    }

    public LuaValue getTop(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getY()));
    }


    /**
     * 设置right坐标
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public LuaValue right(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setRight(view, varargs);
        } else {
            return getRight(view, varargs);
        }
    }

    public LuaValue setRight(U view, Varargs varargs) {
        final int right = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setX(right - view.getWidth());
    }

    public LuaValue getRight(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getX() + view.getWidth()));
    }


    /**
     * 设置bottom坐标
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public LuaValue bottom(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setBottom(view, varargs);
        } else {
            return getBottom(view, varargs);
        }
    }

    public LuaValue setBottom(U view, Varargs varargs) {
        final int bottom = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setY(bottom - view.getHeight());
    }

    public LuaValue getBottom(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getY() + view.getHeight()));
    }

    /**
     * 设置宽度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue width(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setWidth(view, varargs);
        } else {
            return getWidth(view, varargs);
        }
    }

    public LuaValue setWidth(U view, Varargs varargs) {
        final int width = DimenUtil.dpiToPx(varargs.arg(2), -1);
        return view.setWidth(width);
    }

    public LuaValue getWidth(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getWidth()));
    }

    /**
     * 设置最小宽度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue minWidth(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setMinWidth(view, varargs);
        } else {
            return getMinWidth(view, varargs);
        }
    }

    public LuaValue setMinWidth(U view, Varargs varargs) {
        final int width = DimenUtil.dpiToPx(varargs.arg(2), -1);
        return view.setMinWidth(width);
    }

    public LuaValue getMinWidth(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getMinWidth()));
    }

    /**
     * 设置高度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue height(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setHeight(view, varargs);
        } else {
            return getHeight(view, varargs);
        }
    }

    public LuaValue setHeight(U view, Varargs varargs) {
        final int height = DimenUtil.dpiToPx(varargs.arg(2), -1);
        return view.setHeight(height);
    }

    public LuaValue getHeight(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getHeight()));
    }


    /**
     * 设置中心X位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue centerX(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCenterX(view, varargs);
        } else {
            return getCenterX(view, varargs);
        }
    }

    public LuaValue setCenterX(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setCenterX(x);
    }

    public LuaValue getCenterX(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getX() + view.getWidth() / 2.0f));
    }

    /**
     * 设置中心Y位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue centerY(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCenterY(view, varargs);
        } else {
            return getCenterY(view, varargs);
        }
    }

    public LuaValue setCenterY(U view, Varargs varargs) {
        final int y = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setCenterY(y);
    }


    public LuaValue getCenterY(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getY() + view.getHeight() / 2.0f));
    }

    /**
     * 显示
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue visible(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            boolean isShow = varargs.optboolean(2, true);
            return isShow ? show(view, varargs) : hide(view, varargs);
        } else {
            return isShow(view, varargs);
        }
    }

    /**
     * 显示
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue hidden(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            boolean isHide = varargs.optboolean(2, true);
            return isHide ? hide(view, varargs) : show(view, varargs);
        } else {
            return isHide(view, varargs);
        }
    }

    /**
     * 显示
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue show(U view, Varargs varargs) {
        return view.show();
    }

    public LuaValue isShow(U view, Varargs varargs) {
        return valueOf(view.isShow());
    }

    /**
     * 隐藏
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue hide(U view, Varargs varargs) {
        return view.hide();
    }

    public LuaValue isHide(U view, Varargs varargs) {
        return valueOf(view.isHide());
    }


    /**
     * 设置是否有效
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public LuaValue enabled(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setEnabled(view, varargs);
        } else {
            return isEnabled(view, varargs);
        }
    }

    public LuaValue setEnabled(U view, Varargs args) {
        final boolean enable = args.optboolean(2, true);
        return view.setEnabled(enable);
    }

    public LuaValue isEnabled(U view, Varargs varargs) {
        return valueOf(view.isEnabled());
    }

    /**
     * 设置alpha
     *
     * @param view    UIView
     * @param varargs
     * @return
     */
    public LuaValue alpha(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setAlpha(view, varargs);
        } else {
            return getAlpha(view, varargs);
        }
    }

    public LuaValue setAlpha(U view, Varargs varargs) {
        final float alpha = (float) varargs.optdouble(2, 1.0f);
        return view.setAlpha(alpha);
    }

    public LuaValue getAlpha(U view, Varargs varargs) {
        return valueOf(view.getAlpha());
    }

    /**
     * 设置边框粗细
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue borderWidth(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setBorderWidth(view, varargs);
        } else {
            return getBorderWidth(view, varargs);
        }
    }

    public LuaValue setBorderWidth(U view, Varargs varargs) {
        final int width = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setBorderWidth(width);
    }

    public LuaValue getBorderWidth(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getBorderWidth()));
    }

    /**
     * 设置边框颜色，alpha
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue borderColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setBorderColor(view, varargs);
        } else {
            return getBorderColor(view, varargs);
        }
    }

    public LuaValue setBorderColor(U view, Varargs varargs) {
        final int color = ColorUtil.parse(varargs.optvalue(2, NIL));
        return view.setBorderColor(color);
    }

    public LuaValue getBorderColor(U view, Varargs varargs) {
        return valueOf(view.getBorderColor());
    }

    /**
     * 设置View边框是否剪接
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue clipsToBounds(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setClipsToBounds(view, varargs);
        } else {
            return isClipsToBounds(view, varargs);
        }
    }

    public LuaValue setClipsToBounds(U view, Varargs varargs) {
        final boolean clipsToBounds = varargs.optboolean(2, false);
        //TODO
        return view;
    }

    public LuaValue isClipsToBounds(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 设置 只对边框外部加阴影
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue shadowPath(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setShadowPath(view, varargs);
        } else {
            return getShadowPath(view, varargs);
        }
    }

    public LuaValue setShadowPath(U view, Varargs varargs) {
        //TODO
        return view;
    }

    public LuaValue getShadowPath(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 设置边框是否裁剪
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue masksToBounds(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setMasksToBounds(view, varargs);
        } else {
            return getMasksToBounds(view, varargs);
        }
    }

    public LuaValue setMasksToBounds(U view, Varargs varargs) {
        //TODO
        return view;
    }

    public LuaValue getMasksToBounds(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 设置View阴影偏移位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue shadowOffset(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setShadowOffset(view, varargs);
        } else {
            return getShadowOffset(view, varargs);
        }
    }

    public LuaValue setShadowOffset(U view, Varargs varargs) {
        //TODO
        //DimenUtil.dpiToPx
        return view;
    }

    public LuaValue getShadowOffset(U view, Varargs varargs) {
        //TODO
        //DimenUtil.pxToDpi
        return view;
    }

    /**
     * 设置View阴影高斯模糊半径
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue shadowRadius(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setShadowRadius(view, varargs);
        } else {
            return getShadowRadius(view, varargs);
        }
    }

    public LuaValue setShadowRadius(U view, Varargs varargs) {
        //TODO
        //DimenUtil.dpiToPx
        return view;
    }

    public LuaValue getShadowRadius(U view, Varargs varargs) {
        //TODO
        //DimenUtil.pxToDpi
        return view;
    }

    /**
     * 设置View阴影透明度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue shadowOpacity(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setShadowOpacity(view, varargs);
        } else {
            return getShadowOpacity(view, varargs);
        }
    }

    public LuaValue setShadowOpacity(U view, Varargs varargs) {
        //TODO
        return view;
    }

    public LuaValue getShadowOpacity(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 设置View阴影颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue shadowColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setShadowColor(view, varargs);
        } else {
            return getShadowColor(view, varargs);
        }
    }

    public LuaValue setShadowColor(U view, Varargs varargs) {
        //TODO
        return view;
    }

    public LuaValue getShadowColor(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 适应View内容的大小
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue sizeToFit(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 添加手势
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue addGestureRecognizer(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 移除手势
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue removeGestureRecognizer(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 设置3D变换矩阵
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue transform3D(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTransform3D(view, varargs);
        } else {
            return getTransform3D(view, varargs);
        }
    }

    public LuaValue setTransform3D(U view, Varargs varargs) {
        //TODO
        return view;
    }

    public LuaValue getTransform3D(U view, Varargs varargs) {
        //TODO
        return view;
    }

    /**
     * 设置锚点
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs anchorPoint(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setAnchorPoint(view, varargs);
        } else {
            return getAnchorPoint(view, varargs);
        }
    }

    public LuaValue setAnchorPoint(U view, Varargs varargs) {
        //TODO oc实现跟这个不一样，oc会移动一下
        final float pivotX = (float) varargs.optdouble(2, 0.5f);
        final float pivotY = (float) varargs.optdouble(2, 0.5f);
        return view.setPivot(pivotX, pivotY);
    }

    public Varargs getAnchorPoint(U view, Varargs varargs) {
        return varargsOf(valueOf(view.getPivotX()), valueOf(view.getPivotY()));
    }

    /**
     * 从父容器中移除
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue removeFromSuper(U view, Varargs varargs) {
        return view.removeFromParent();
    }

    public LuaValue removeFromParent(U view, Varargs varargs) {
        return view.removeFromParent();
    }

    /**
     * 是否有焦点
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue hasFocus(U view, Varargs varargs) {
        return valueOf(view.hasFocus());
    }

    /**
     * 请求焦点
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue requestFocus(U view, Varargs varargs) {
        return view.requestFocus();
    }

    /**
     * 取消焦点
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue clearFocus(U view, Varargs varargs) {
        return view.clearFocus();
    }

    /**
     * 旋转
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue rotation(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setRotation(view, varargs);
        } else {
            return getRotation(view, varargs);
        }
    }

    public LuaValue setRotation(U view, Varargs varargs) {
        final float rotation = (float) varargs.optdouble(2, 0.0);
        return view.setRotation(rotation);
    }

    public LuaValue getRotation(U view, Varargs varargs) {
        return valueOf(view.getRotation());
    }


    /**
     * 旋转
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs rotationXY(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setRotationXY(view, varargs);
        } else {
            return getRotationXY(view, varargs);
        }
    }

    public LuaValue setRotationXY(U view, Varargs varargs) {
        final int rotationX = DimenUtil.dpiToPx(varargs.arg(2));
        final int rotationY = DimenUtil.dpiToPx(varargs.arg(3));
        return view.setRotationXY(rotationX, rotationY);
    }

    public Varargs getRotationXY(U view, Varargs varargs) {
        return varargsOf(valueOf(DimenUtil.pxToDpi(view.getRotationX())), valueOf(DimenUtil.pxToDpi(view.getRotationY())));
    }

    /**
     * 旋转
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs scale(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setScale(view, varargs);
        } else {
            return getScale(view, varargs);
        }
    }

    public LuaValue setScale(U view, Varargs varargs) {
        final float scaleX = LuaUtil.getFloat(varargs, 0f, 2);
        final float scaleY = LuaUtil.getFloat(varargs, 0f, 3, 2);
        return view.setScale(scaleX, scaleY);
    }

    public Varargs getScale(U view, Varargs varargs) {
        return varargsOf(valueOf(view.getScaleX()), valueOf(view.getScaleY()));
    }

    public Varargs scaleX(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setScaleX(view, varargs);
        } else {
            return getScaleX(view, varargs);
        }
    }

    public LuaValue setScaleX(U view, Varargs varargs) {
        final float scaleX = LuaUtil.getFloat(varargs, 0f, 2);
        return view.setScaleX(scaleX);
    }

    public Varargs getScaleX(U view, Varargs varargs) {
        return valueOf(view.getScaleX());
    }

    public Varargs scaleY(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setScaleY(view, varargs) ;
        } else {
            return getScaleY(view, varargs);
        }
    }

    public LuaValue setScaleY(U view, Varargs varargs) {
        final float scaleY = LuaUtil.getFloat(varargs, 0f, 2);
        return view.setScaleY(scaleY);
    }

    public Varargs getScaleY(U view, Varargs varargs) {
        return valueOf(view.getScaleY());
    }


    /**
     * 获取translation
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs translation(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTranslation(view, varargs);
        } else {
            return getTranslation(view, varargs);
        }
    }

    public Varargs setTranslation(U view, Varargs varargs) {
        final float translationX = DimenUtil.dpiToPx(LuaUtil.getFloat(varargs, 2));
        final float translationY = DimenUtil.dpiToPx(LuaUtil.getFloat(varargs, 3));
        return view.setTranslation(translationX, translationY);
    }

    public Varargs getTranslation(U view, Varargs varargs) {
        return varargsOf(valueOf(DimenUtil.pxToDpi(view.getTranslationX())), valueOf(DimenUtil.pxToDpi(view.getTranslationY())));
    }

    /**
     * 获取translationX
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue translationX(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTranslationX(view, varargs);
        } else {
            return getTranslationX(view, varargs);
        }
    }

    public LuaValue setTranslationX(U view, Varargs varargs) {
        final float translationX = DimenUtil.dpiToPx(LuaUtil.getFloat(varargs, 2));
        return view.setTranslation(translationX, null);
    }

    public LuaValue getTranslationX(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getTranslationX()));
    }

    /**
     * 获取translationX
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue translationY(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTranslationY(view, varargs);
        } else {
            return getTranslationY(view, varargs);
        }
    }

    public LuaValue setTranslationY(U view, Varargs varargs) {
        final float translationY = DimenUtil.dpiToPx(LuaUtil.getFloat(varargs, 2));
        return view.setTranslation(null, translationY);
    }

    public LuaValue getTranslationY(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getTranslationX()));
    }


    /**
     * 将view设置到前台
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue bringToFront(U view, Varargs varargs) {
        return view.bringToFront();
    }


    /**
     * 滚动到某个位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue scrollTo(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        return view.scrollTo(x, y);
    }

    /**
     * 滚动一段距离
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue scrollBy(U view, Varargs varargs) {
        final int dx = DimenUtil.dpiToPx(varargs.arg(2));
        final int dy = DimenUtil.dpiToPx(varargs.arg(3));
        return view.scrollBy(dx, dy);
    }

    /**
     * 滚动x
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue scrollX(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setScrollX(view, varargs);
        } else {
            return getScrollX(view, varargs);
        }
    }

    public LuaValue setScrollX(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = (int) view.getY();
        return view.scrollTo(x, y);
    }


    public LuaValue getScrollX(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getScrollX()));
    }

    /**
     * 滚动x
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue offsetX(U view, Varargs varargs) {
        return scrollX(view, varargs);
    }

    public LuaValue setOffsetX(U view, Varargs varargs) {
        return setScrollX(view, varargs);
    }

    public LuaValue getOffsetX(U view, Varargs varargs) {
        return getScrollX(view, varargs);
    }

    /**
     * 滚动y
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue scrollY(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setScrollY(view, varargs);
        } else {
            return getScrollY(view, varargs);
        }
    }

    public LuaValue setScrollY(U view, Varargs varargs) {
        final int x = (int) view.getX();
        final int y = DimenUtil.dpiToPx(varargs.arg(2));
        return view.scrollTo(x, y);
    }

    public LuaValue getScrollY(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getScrollY()));
    }

    /**
     * 滚动y
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue offsetY(U view, Varargs varargs) {
        return scrollY(view, varargs);
    }

    public LuaValue setOffsetY(U view, Varargs varargs) {
        return setScrollY(view, varargs);
    }

    public LuaValue getOffsetY(U view, Varargs varargs) {
        return getScrollY(view, varargs);
    }

    /**
     * 获取滚动的x，y
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs scrollXY(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setScrollXY(view, varargs);
        } else {
            return getScrollXY(view, varargs);
        }
    }

    public LuaValue setScrollXY(U view, Varargs varargs) {
        final int x = DimenUtil.dpiToPx(varargs.arg(2));
        final int y = DimenUtil.dpiToPx(varargs.arg(3));
        return view.scrollTo(x, y);
    }

    public Varargs getScrollXY(U view, Varargs varargs) {
        return varargsOf(valueOf(DimenUtil.pxToDpi(view.getScrollX())), valueOf(DimenUtil.pxToDpi(view.getScrollY())));
    }

    /**
     * 获取滚动的x，y
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs offsetXY(U view, Varargs varargs) {
        return scrollXY(view, varargs);
    }

    public Varargs setOffsetXY(U view, Varargs varargs) {
        return setScrollXY(view, varargs);
    }

    public Varargs getOffsetXY(U view, Varargs varargs) {
        return getScrollXY(view, varargs);
    }

    /**
     * 滚动到某个位置
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs offset(U view, Varargs varargs) {
        return scrollXY(view, varargs);
    }

    public LuaValue setOffset(U view, Varargs varargs) {
        return setScrollXY(view, varargs);
    }

    public Varargs getOffset(U view, Varargs varargs) {
        return getScrollXY(view, varargs);
    }


    /**
     * 设置滚动条是否显示（横向、纵向）
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs showScrollIndicator(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setShowScrollIndicator(view, varargs);
        } else {
            return isShowScrollIndicator(view, varargs);
        }
    }

    public LuaValue setShowScrollIndicator(U view, Varargs varargs) {
        final boolean horizontalIndicator = varargs.optboolean(2, false);
        final boolean verticalIndicator = varargs.optboolean(3, false);
        view.setHorizontalScrollBarEnabled(horizontalIndicator);
        view.setVerticalScrollBarEnabled(verticalIndicator);
        return view;
    }

    public Varargs isShowScrollIndicator(U view, Varargs varargs) {
        return varargsOf(valueOf(view.isHorizontalScrollBarEnabled()), valueOf(view.isVerticalScrollBarEnabled()));
    }


    /**
     * 设置回调
     * BeginEditing --开始编辑
     * EndEditing -- 结束编辑
     * Clear -- 删除
     * Return --返回
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue callback(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCallback(view, varargs);
        } else {
            return getCallback(view, varargs);
        }
    }

    public LuaValue setCallback(U view, Varargs varargs) {
        final LuaValue callbacks = varargs.optvalue(2, NIL);
        return view.setCallback(callbacks);
    }

    public LuaValue getCallback(U view, Varargs varargs) {
        return view.getCallback();
    }

    /**
     * 点击
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onClick(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnClick(view, varargs);
        } else {
            return getOnClick(view, varargs);
        }
    }

    public LuaValue setOnClick(U view, Varargs varargs) {
        final LuaFunction callback = LuaUtil.getFunction(varargs, 2);
        return view.setOnClickCallback(callback);
    }

    public LuaValue getOnClick(U view, Varargs varargs) {
        return view.getOnClickCallback();
    }

    /**
     * 长按
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue onLongClick(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnLongClick(view, varargs);
        } else {
            return getOnLongClick(view, varargs);
        }
    }

    public LuaValue setOnLongClick(U view, Varargs varargs) {
        final LuaFunction callback = LuaUtil.getFunction(varargs, 2);
        return view.setOnLongClickCallback(callback);
    }

    public LuaValue getOnLongClick(U view, Varargs varargs) {
        return view.getOnLongClickCallback();
    }

    /**
     * 调整大小以适应内容
     *
     * @return
     */
    public LuaValue adjustSize(U view, Varargs varargs) {
        return view.adjustSize();
    }


    /**
     * 设置边框圆角半径
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue cornerRadius(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCornerRadius(view, varargs);
        } else {
            return getCornerRadius(view, varargs);
        }
    }

    public LuaValue setCornerRadius(U view, Varargs varargs) {
        final int radius = DimenUtil.dpiToPx(varargs.arg(2));
        return view.setCornerRadius(radius);
    }

    public LuaValue getCornerRadius(U view, Varargs varargs) {
        return valueOf(DimenUtil.pxToDpi(view.getCornerRadius()));
    }

    /**
     * 开始动画
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue startAnimation(U view, Varargs varargs) {
        LuaValue[] animators = null;
        if (varargs.narg() > 1) {
            animators = new LuaValue[varargs.narg() - 1];
            for (int i = 2; i <= varargs.narg(); i++) {
                animators[i - 2] = varargs.arg(i);
            }
        }
        return view.startAnimation(animators);
    }

    @LuaViewApi(revisions = {VmVersion.V_500, "修改了底层的停止API"})
    public LuaValue stopAnimation(U view, Varargs varargs) {
        view.cancelAnimation();
        return view;
    }

    public LuaValue isAnimating(U view, Varargs varargs) {
        return valueOf(view.isAnimating());
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue flexCss(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setFlexCss(view, varargs);
        } else {
            return getFlexCss(view, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue flxLayout(U view, Varargs varargs) {
        // Android doing nothing here

        return view;
    }

    @LuaViewApi(since = VmVersion.V_500)
    private LuaValue setFlexCss(U view, Varargs varargs) {
        final String css = LuaUtil.getString(varargs, 2);
        return view.setFlexCss(css);
    }

    @LuaViewApi(since = VmVersion.V_500)
    private LuaValue getFlexCss(U view, Varargs varargs) {
        return valueOf(view.getFlexCss());
    }
}