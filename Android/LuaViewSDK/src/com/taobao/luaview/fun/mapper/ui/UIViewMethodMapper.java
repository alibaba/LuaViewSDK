/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import android.widget.RelativeLayout;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.SdkVersion;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有view 接口封装
 *
 * @param <U>
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UIViewMethodMapper<U extends UDView> extends BaseMethodMapper<U> {
    private static final String TAG = "UIViewMethodMapper";
    private static final String[] sMethods = new String[]{
            "initParams",//0
            "invalidate",//1
            "padding",//2
            "frame",//3
            "backgroundColor",//4
            "size",//5
            "xy",//6
            "align",//7
            "alignLeft",//8
            "alignTop",//9
            "alignRight",//10
            "alignBottom",//11
            "alignLeftTop",//12
            "alignTopLeft",//13
            "alignCenterTop",//14
            "alignTopCenter",//15
            "alignRightTop",//16
            "alignTopRight",//17
            "alignLeftBottom",//18
            "alignBottomLeft",//19
            "alignCenterBottom",//20
            "alignBottomCenter",//21
            "alignRightBottom",//22
            "alignBottomRight",//23
            "alignCenter",//24
            "alignLeftCenter",//25
            "alignCenterLeft",//26
            "alignRightCenter",//27
            "alignCenterRight",//28
            "alignCenterHorizontal",//29
            "alignHorizontalCenter",//30
            "alignCenterVertical",//31
            "alignVerticalCenter",//32
            "center",//33
            "x",//34
            "y",//35
            "left",//36
            "top",//37
            "right",//38
            "bottom",//39
            "width",//40
            "minWidth",//41
            "height",//42
            "centerX",//43
            "centerY",//44
            "visible",//45
            "hidden",//46
            "show",//47
            "isShow",//48
            "hide",//49
            "isHide",//50
            "enabled",//51
            "alpha",//52
            "borderWidth",//53
            "borderColor",//54
            "clipsToBounds",//55
            "shadowPath",//56
            "masksToBounds",//57
            "shadowOffset",//58
            "shadowRadius",//59
            "shadowOpacity",//60
            "shadowColor",//61
            "sizeToFit",//62
            "addGestureRecognizer",//63
            "removeGestureRecognizer",//64
            "transform3D",//65
            "anchorPoint",//66
            "removeFromSuper",//67
            "removeFromParent",//68
            "hasFocus",//69
            "requestFocus",//70
            "clearFocus",//71
            "rotation",//72
            "rotationXY",//73
            "scale",//74
            "scaleX",//75
            "scaleY",//76
            "translation",//77
            "translationX",//78
            "translationY",//79
            "bringToFront",//80
            "scrollTo",//81
            "scrollBy",//82
            "scrollX",//83
            "offsetX",//84
            "scrollY",//85
            "offsetY",//86
            "scrollXY",//87
            "offsetXY",//88
            "offset",//89
            "showScrollIndicator",//90
            "callback",//91
            "onClick",//92
            "onLongClick",//93
            "adjustSize",//94
            "cornerRadius",//95
            "startAnimation",//96
            "stopAnimation",//97
            "isAnimating",//98
            "flexCss",//99
            "flxLayout",//100
            "effects",//101
            "nativeView",//102
            "borderDash",//103
            "margin",//104
            "onTouch"//105
//            "matrix"//106
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
                return initParams(target, varargs);
            case 1:
                return invalidate(target, varargs);
            case 2:
                return padding(target, varargs);
            case 3:
                return frame(target, varargs);
            case 4:
                return backgroundColor(target, varargs);
            case 5:
                return size(target, varargs);
            case 6:
                return xy(target, varargs);
            case 7:
                return align(target, varargs);
            case 8:
                return alignLeft(target, varargs);
            case 9:
                return alignTop(target, varargs);
            case 10:
                return alignRight(target, varargs);
            case 11:
                return alignBottom(target, varargs);
            case 12:
                return alignLeftTop(target, varargs);
            case 13:
                return alignTopLeft(target, varargs);
            case 14:
                return alignCenterTop(target, varargs);
            case 15:
                return alignTopCenter(target, varargs);
            case 16:
                return alignRightTop(target, varargs);
            case 17:
                return alignTopRight(target, varargs);
            case 18:
                return alignLeftBottom(target, varargs);
            case 19:
                return alignBottomLeft(target, varargs);
            case 20:
                return alignCenterBottom(target, varargs);
            case 21:
                return alignBottomCenter(target, varargs);
            case 22:
                return alignRightBottom(target, varargs);
            case 23:
                return alignBottomRight(target, varargs);
            case 24:
                return alignCenter(target, varargs);
            case 25:
                return alignLeftCenter(target, varargs);
            case 26:
                return alignCenterLeft(target, varargs);
            case 27:
                return alignRightCenter(target, varargs);
            case 28:
                return alignCenterRight(target, varargs);
            case 29:
                return alignCenterHorizontal(target, varargs);
            case 30:
                return alignHorizontalCenter(target, varargs);
            case 31:
                return alignCenterVertical(target, varargs);
            case 32:
                return alignVerticalCenter(target, varargs);
            case 33:
                return center(target, varargs);
            case 34:
                return x(target, varargs);
            case 35:
                return y(target, varargs);
            case 36:
                return left(target, varargs);
            case 37:
                return top(target, varargs);
            case 38:
                return right(target, varargs);
            case 39:
                return bottom(target, varargs);
            case 40:
                return width(target, varargs);
            case 41:
                return minWidth(target, varargs);
            case 42:
                return height(target, varargs);
            case 43:
                return centerX(target, varargs);
            case 44:
                return centerY(target, varargs);
            case 45:
                return visible(target, varargs);
            case 46:
                return hidden(target, varargs);
            case 47:
                return show(target, varargs);
            case 48:
                return isShow(target, varargs);
            case 49:
                return hide(target, varargs);
            case 50:
                return isHide(target, varargs);
            case 51:
                return enabled(target, varargs);
            case 52:
                return alpha(target, varargs);
            case 53:
                return borderWidth(target, varargs);
            case 54:
                return borderColor(target, varargs);
            case 55:
                return clipsToBounds(target, varargs);
            case 56:
                return shadowPath(target, varargs);
            case 57:
                return masksToBounds(target, varargs);
            case 58:
                return shadowOffset(target, varargs);
            case 59:
                return shadowRadius(target, varargs);
            case 60:
                return shadowOpacity(target, varargs);
            case 61:
                return shadowColor(target, varargs);
            case 62:
                return sizeToFit(target, varargs);
            case 63:
                return addGestureRecognizer(target, varargs);
            case 64:
                return removeGestureRecognizer(target, varargs);
            case 65:
                return transform3D(target, varargs);
            case 66:
                return anchorPoint(target, varargs);
            case 67:
                return removeFromSuper(target, varargs);
            case 68:
                return removeFromParent(target, varargs);
            case 69:
                return hasFocus(target, varargs);
            case 70:
                return requestFocus(target, varargs);
            case 71:
                return clearFocus(target, varargs);
            case 72:
                return rotation(target, varargs);
            case 73:
                return rotationXY(target, varargs);
            case 74:
                return scale(target, varargs);
            case 75:
                return scaleX(target, varargs);
            case 76:
                return scaleY(target, varargs);
            case 77:
                return translation(target, varargs);
            case 78:
                return translationX(target, varargs);
            case 79:
                return translationY(target, varargs);
            case 80:
                return bringToFront(target, varargs);
            case 81:
                return scrollTo(target, varargs);
            case 82:
                return scrollBy(target, varargs);
            case 83:
                return scrollX(target, varargs);
            case 84:
                return offsetX(target, varargs);
            case 85:
                return scrollY(target, varargs);
            case 86:
                return offsetY(target, varargs);
            case 87:
                return scrollXY(target, varargs);
            case 88:
                return offsetXY(target, varargs);
            case 89:
                return offset(target, varargs);
            case 90:
                return showScrollIndicator(target, varargs);
            case 91:
                return callback(target, varargs);
            case 92:
                return onClick(target, varargs);
            case 93:
                return onLongClick(target, varargs);
            case 94:
                return adjustSize(target, varargs);
            case 95:
                return cornerRadius(target, varargs);
            case 96:
                return startAnimation(target, varargs);
            case 97:
                return stopAnimation(target, varargs);
            case 98:
                return isAnimating(target, varargs);
            case 99:
                return flexCss(target, varargs);
            case 100:
                return flxLayout(target, varargs);
            case 101:
                return effects(target, varargs);
            case 102:
                return nativeView(target, varargs);
            case 103:
                return borderDash(target, varargs);
            case 104:
                return margin(target, varargs);
            case 105:
                return onTouch(target, varargs);
//            case 106:
//                return matrix(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    /**
     * 初始化数据
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public Varargs initParams(U view, Varargs varargs) {
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

    public Varargs getInitParams(U view, Varargs varargs) {
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
     * 设置Margin
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs margin(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setMargin(view, varargs);
        } else {
            return getMargin(view, varargs);
        }
    }

    public LuaValue setMargin(U view, Varargs varargs) {
        final Integer left = DimenUtil.dpiToPx(varargs.arg(2), null);
        final Integer top = DimenUtil.dpiToPx(varargs.arg(3), null);
        final Integer right = DimenUtil.dpiToPx(varargs.arg(4), null);
        final Integer bottom = DimenUtil.dpiToPx(varargs.arg(5), null);
        return view.setMargin(left, top, right, bottom);
    }

    public Varargs getMargin(U view, Varargs varargs) {
        return varargsOf(new LuaValue[]{valueOf(DimenUtil.pxToDpi(view.getMarginLeft())), valueOf(DimenUtil.pxToDpi(view.getMarginTop())), valueOf(DimenUtil.pxToDpi(view.getMarginRight())), valueOf(DimenUtil.pxToDpi(view.getMarginBottom()))});
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
    @LuaViewApi(revisions = {"IOS不支持图片", "待替换成background"})
    public Varargs backgroundColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setBackgroundColor(view, varargs);
        } else {
            return getBackgroundColor(view, varargs);
        }
    }

    public LuaValue setBackgroundColor(final U view, final Varargs args) {
        if (args.isnumber(2)) {//TODO 支持8位颜色设置，以及alpha设置。同时支持获取
            final Integer color = ColorUtil.parse(LuaUtil.getInt(args, 2));
            Double alpha = LuaUtil.getDouble(args, 3);
            return view.setBackgroundColorAndAlpha(color, alpha);
        } else {
            final String pic = args.optjstring(2, "");
            final Double alpha = LuaUtil.getDouble(args, 3);
            return view.setBackgroundResourceAndAlpha(pic, alpha);
        }
    }

    public Varargs getBackgroundColor(U view, Varargs varargs) {
        return varargsOf(valueOf(ColorUtil.getHexColor(view.getBackgroundColor())), valueOf(view.getBackgroundAlpha()));
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
    @Deprecated
    public LuaValue alignLeftTop(U view, Varargs args) {
        return alignTopLeft(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignCenterTop(U view, Varargs args) {
        return alignTopCenter(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignRightTop(U view, Varargs args) {
        return alignTopRight(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignLeftBottom(U view, Varargs args) {
        return alignBottomLeft(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignCenterBottom(U view, Varargs args) {
        return alignBottomCenter(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignRightBottom(U view, Varargs args) {
        return alignBottomRight(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignLeftCenter(U view, Varargs args) {
        return alignCenterLeft(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignRightCenter(U view, Varargs args) {
        return alignCenterRight(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignCenterHorizontal(U view, Varargs args) {
        return alignHorizontalCenter(view, args);
    }

    @Deprecated
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
    @Deprecated
    public LuaValue alignCenterVertical(U view, Varargs args) {
        return alignVerticalCenter(view, args);
    }

    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public LuaValue show(U view, Varargs varargs) {
        return view.show();
    }

    @Deprecated
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
    @Deprecated
    public LuaValue hide(U view, Varargs varargs) {
        return view.hide();
    }

    @Deprecated
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
        final Integer color = ColorUtil.parse(LuaUtil.getInt(varargs, 2));
        return view.setBorderColor(color);
    }

    public LuaValue getBorderColor(U view, Varargs varargs) {
        return valueOf(ColorUtil.getHexColor(view.getBorderColor()));
    }

    /**
     * 设置边框虚线，dash
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_540)
    public Varargs borderDash(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setBorderDash(view, varargs);
        } else {
            return getBorderDash(view, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_540)
    public LuaValue setBorderDash(U view, Varargs varargs) {
        final float width = DimenUtil.dpiToPxF(LuaUtil.getFloat(varargs, 2));
        final float gap = DimenUtil.dpiToPxF(LuaUtil.getFloat(varargs, 3));
        return view.setBorderDashSize(width, gap);
    }

    @LuaViewApi(since = VmVersion.V_540)
    public Varargs getBorderDash(U view, Varargs varargs) {
        return varargsOf(valueOf(DimenUtil.pxToDpi(view.getBorderDashWidth())), valueOf(DimenUtil.pxToDpi(view.getBorderDashGap())));
    }

    /**
     * 设置View边框是否剪接
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(revisions = "Only for iOS")
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @LuaViewApi(revisions = {"iOS平台特性，待废弃"})
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @LuaViewApi(revisions = {"名称有待讨论"})
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
        final float pivotY = (float) varargs.optdouble(3, 0.5f);
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
    @LuaViewApi(revisions = {"名称待讨论"})
    public LuaValue removeFromSuper(U view, Varargs varargs) {
        return view.removeFromParent();
    }

    @Deprecated
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
    @LuaViewApi(revisions = {"待优化，增加rotationX, rotationY"})
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
    @Deprecated
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
            return setScaleY(view, varargs);
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
    @LuaViewApi(revisions = "iOS待新增")
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
    @LuaViewApi(revisions = {"iOS无，待迁移到ScrollView"})
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
     * On Touch 事件
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_550)
    public LuaValue onTouch(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setOnTouch(view, varargs);
        } else {
            return getOnTouch(view, varargs);
        }
    }

    public LuaValue setOnTouch(U view, Varargs varargs) {
        final LuaFunction callback = LuaUtil.getFunction(varargs, 2);
        return view.setOnTouchCallback(callback);
    }

    public LuaValue getOnTouch(U view, Varargs varargs) {
        return view.getOnTouchCallback();
    }


    @LuaViewApi(since = SdkVersion.V_051)
    public LuaValue matrix(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            if (varargs.istable(2)) {
                LuaTable table = LuaUtil.getTable(varargs, 2);
                int n = table.length();
                if (n > 9) {
                    float[] values = new float[9];
                    for (int i = 0; i < 9; i++) {
                        values[i] = LuaUtil.getFloat(table, 0F, i + 2);
                    }
                    return view.setMatrix(values);
                } else if (n > 6){
                    float[] values = new float[9];
                    for (int i = 0; i < 6; i++) {
                        values[i] = LuaUtil.getFloat(table, 0F, i + 2);
                    }
                    values[6] = 0;
                    values[7] = 0;
                    values[8] = 1;
                    return view.setMatrix(values);

                }
            } else {
                int n = varargs.narg();
                if (n > 9) {
                    float[] values = new float[9];
                    for (int i = 0; i < 9; i++) {
                        values[i] = LuaUtil.getFloat(varargs, 0F, i + 2);
                    }
                    return view.setMatrix(values);
                } else if (n > 6){
                    float[] values = new float[9];
                    for (int i = 0; i < 6; i++) {
                        values[i] = LuaUtil.getFloat(varargs, 0F, i + 2);
                    }
                    values[6] = 0;
                    values[7] = 0;
                    values[8] = 1;
                    return view.setMatrix(values);
                }
            }
        } else {
            float[] values = view.getMatrix();
            if (values != null) {
                LuaTable table = new LuaTable();
                for (int i = 0; i < 6; i++) {
                    table.set(i + 1, valueOf(values[i]));
                }
                return table;
            }
        }
        return view;
    }

    /**
     * 调整大小以适应内容
     *
     * @return
     */
    @LuaViewApi(revisions = {"待沟通，是否需要"})
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
    @LuaViewApi(revisions = {"待废弃"})
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

    @LuaViewApi(revisions = {VmVersion.V_500, "修改了底层的停止API", "待废弃"})
    public LuaValue stopAnimation(U view, Varargs varargs) {
        view.cancelAnimation();
        return view;
    }

    @LuaViewApi(revisions = {"待废弃"})
    public LuaValue isAnimating(U view, Varargs varargs) {
        return valueOf(view.isAnimating());
    }

    /**
     * 设置flex css属性
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue flexCss(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setFlexCss(view, varargs);
        } else {
            return getFlexCss(view, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue setFlexCss(U view, Varargs varargs) {
        final String css = LuaUtil.getString(varargs, 2);
        return view.setFlexCss(css);
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue getFlexCss(U view, Varargs varargs) {
        return valueOf(view.getFlexCss());
    }

    /**
     * 设置flex layout
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_500, revisions = {"iOS有，待沟通"})
    public LuaValue flxLayout(U view, Varargs varargs) {
        // Android doing nothing here
        return view;
    }


    /**
     * 设置View的特殊效果，如果为-1，则取消所有view的特效
     *
     * @param view
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_511)
    public LuaValue effects(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setEffects(view, varargs);
        } else {
            return getEffects(view, varargs);
        }
    }

    @LuaViewApi(since = VmVersion.V_511)
    public LuaValue setEffects(U view, Varargs varargs) {
        return view.setEffects(varargs);
    }

    @LuaViewApi(since = VmVersion.V_511)
    public LuaValue getEffects(U view, Varargs varargs) {
        return valueOf(view.getEffects());
    }


    /**
     * 获取native view
     *
     * @param customPanel
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_511, revisions = {"从UICustomPanelMethodMapper移过来（V500）"})
    public LuaValue nativeView(U customPanel, Varargs varargs) {
        return getNativeView(customPanel, varargs);
    }

    @LuaViewApi(since = VmVersion.V_511, revisions = {"从UICustomPanelMethodMapper移过来（V500）"})
    public LuaValue getNativeView(U customPanel, Varargs varargs) {
        return customPanel.getNativeView();
    }
}
