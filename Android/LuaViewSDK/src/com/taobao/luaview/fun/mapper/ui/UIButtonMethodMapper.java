package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDButton;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * Button 接口封装
 * @author song
 * @param <U>
 */
@LuaViewLib
public class UIButtonMethodMapper<U extends UDButton> extends UITextViewMethodMapper<U> {

    /**
     * setText
     *
     * @param view    UIButton
     * @param varargs Varargs
     * @return LuaValue
     */
    public LuaValue title(U view, Varargs varargs) {
        return text(view, varargs);
    }

    public LuaValue setTitle(U view, Varargs varargs) {
        return setText(view, varargs);
    }

    public LuaValue getTitle(U view, Varargs varargs) {
        return getText(view, varargs);
    }

    /**
     * 文字颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue titleColor(U view, Varargs varargs) {
        return textColor(view, varargs);
    }

    public LuaValue setTitleColor(U view, Varargs varargs) {
        return setTextColor(view, varargs);
    }

    public LuaValue getTitleColor(U view, Varargs varargs) {
        return getTextColor(view, varargs);
    }

    /**
     * 设置图片
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs image(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setImage(view, varargs);
        } else {
            return getImage(view, varargs);
        }
    }

    public LuaValue setImage(U view, Varargs varargs) {
        final String normalImage = varargs.optjstring(2, null);
        final String pressedImage = varargs.optjstring(3, null);
        return view.setImage(normalImage, pressedImage);
    }

    public Varargs getImage(U view, Varargs varargs) {
        return view.getImage();
    }

    /**
     * 获取按钮点击是否高亮
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue showsTouchWhenHighlighted(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setShowsTouchWhenHighlighted(view, varargs);
        } else {
            return isShowsTouchWhenHighlighted(view, varargs);
        }
    }

    public LuaValue setShowsTouchWhenHighlighted(U view, Varargs varargs) {
        //TODO
        return view.setHighlightColor(0);
    }

    public LuaValue isShowsTouchWhenHighlighted(U view, Varargs varargs) {
        //TODO
        return valueOf(view.getHighlightColor() == 0);
    }
}