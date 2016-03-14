package com.taobao.luaview.fun.mapper.ui;

import android.text.TextUtils;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.constants.UDEllipsize;
import com.taobao.luaview.userdata.ui.UDTextView;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.LuaViewUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

@LuaViewLib
public class UITextViewMethodMapper<U extends UDTextView> extends UIViewMethodMapper<U> {

    /**
     * 获得文本
     *
     * @param view    UDTextView
     * @param varargs Varargs
     * @return LuaValue
     */
    public LuaValue text(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setText(view, varargs);
        } else {
            return getText(view, varargs);
        }
    }

    public LuaValue setText(U view, Varargs varargs) {
        final CharSequence text = LuaViewUtil.getText(varargs.optvalue(2, NIL));
        return view.setText(text);
    }

    public LuaValue getText(U view, Varargs varargs) {
        return valueOf(String.valueOf(view.getText()));
    }


    /**
     * 获取字体颜色
     *
     * @param view    UDTextView
     * @param varargs Varargs
     * @return LuaValue
     */
    public LuaValue textColor(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTextColor(view, varargs);
        } else {
            return getTextColor(view, varargs);
        }
    }

    public LuaValue setTextColor(U view, Varargs varargs) {
        final int color = ColorUtil.parse(varargs.optvalue(2, NIL));
        return view.setTextColor(color);
    }

    public LuaValue getTextColor(U view, Varargs varargs) {
        return valueOf(view.getTextColor());
    }


    /**
     * 字体大小
     *
     * @param view    UDTextView
     * @param varargs Varargs
     * @return LuaValue
     */
    public LuaValue textSize(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTextSize(view, varargs);
        } else {
            return getTextSize(view, varargs);
        }
    }

    public LuaValue setTextSize(U view, Varargs varargs) {
        final float fontSize = (float) varargs.optdouble(2, 12.0);
        return view.setTextSize(fontSize);
    }

    public LuaValue getTextSize(U view, Varargs varargs) {
        return valueOf(view.getTextSize());
    }

    /**
     * 设置文字大小
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue fontSize(U view, Varargs varargs) {
        return textSize(view, varargs);
    }

    public LuaValue setFontSize(U view, Varargs varargs) {
        return setTextSize(view, varargs);
    }

    public LuaValue getFontSize(U view, Varargs varargs) {
        return getTextSize(view, varargs);
    }

    /**
     * 设置字体
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue fontName(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setFontName(view, varargs);
        } else {
            return getFontName(view, varargs);
        }
    }

    public LuaValue setFontName(U view, Varargs varargs) {
        final String fontName = varargs.optjstring(2, null);
        return view.setFont(fontName);
    }

    public LuaValue getFontName(U view, Varargs varargs) {
        return valueOf(view.getFont());
    }

    /**
     * 获得文字和大小
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs font(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setFont(view, varargs);
        } else {
            return getFont(view, varargs);
        }
    }

    public LuaValue setFont(U view, Varargs varargs) {
        if (varargs.narg() > 2) {//两个参数
            final String fontName = varargs.optjstring(2, null);
            final float fontSize = (float) varargs.optdouble(3, -1);
            return view.setFont(fontName, fontSize);
        } else {//一个参数
            final float fontSize = (float) varargs.optdouble(2, -1);
            return view.setTextSize(fontSize);
        }
    }

    public Varargs getFont(U view, Varargs varargs) {
        return varargsOf(valueOf(view.getFont()), valueOf(view.getTextSize()));
    }

    /**
     * 设置文字对齐方式
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue gravity(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setGravity(view, varargs);
        } else {
            return getGravity(view, varargs);
        }
    }

    public LuaValue setGravity(U view, Varargs varargs) {
        final int gravity = varargs.optint(2, 0);
        return view.setGravity(gravity);
    }

    public LuaValue getGravity(U view, Varargs varargs) {
        return valueOf(view.getGravity());
    }

    /**
     * 获得文字的对齐方式
     * TODO 需要跟iOS统一alignment的数值
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue textAlign(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setTextAlign(view, varargs);
        } else {
            return getTextAlign(view, varargs);
        }
    }

    public LuaValue setTextAlign(U view, Varargs varargs) {
        final int alignment = varargs.optint(2, 0);
        return view.setTextAlign(alignment);
    }

    public LuaValue getTextAlign(U view, Varargs varargs) {
        return valueOf(view.getTextAlign());
    }


    /**
     * 获得文字行数
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue lines(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setLines(view, varargs);
        } else {
            return getLines(view, varargs);
        }
    }

    public LuaValue setLines(U view, Varargs varargs) {
        final int lines = varargs.optint(2, -1);
        return view.setLines(lines);
    }

    public LuaValue getLines(U view, Varargs varargs) {
        return valueOf(view.getLines());
    }


    /**
     * 获得文字最大行数
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue maxLines(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setMaxLines(view, varargs);
        } else {
            return getMaxLines(view, varargs);
        }
    }

    public LuaValue setMaxLines(U view, Varargs varargs) {
        final int lines = varargs.optint(2, -1);
        return view.setMaxLines(lines);
    }

    public LuaValue getMaxLines(U view, Varargs varargs) {
        return valueOf(view.getMaxLines());
    }

    /**
     * 文字行数(最大）
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue lineCount(U view, Varargs varargs) {
        return maxLines(view, varargs);
    }

    public LuaValue setLineCount(U view, Varargs varargs) {
        return setMaxLines(view, varargs);
    }

    public LuaValue getLineCount(U view, Varargs varargs) {
        return getMaxLines(view, varargs);
    }


    /**
     * 获得文字最小行数
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue minLines(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setMinLines(view, varargs);
        } else {
            return getMinLines(view, varargs);
        }
    }

    public LuaValue setMinLines(U view, Varargs varargs) {
        final int lines = varargs.optint(2, -1);
        return view.setMinLines(lines);
    }

    public LuaValue getMinLines(U view, Varargs varargs) {
        return valueOf(view.getMinLines());
    }


    /**
     * 文字显示不下的时候如何显示...
     */
    public LuaValue ellipsize(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setEllipsize(view, varargs);
        } else {
            return getEllipsize(view, varargs);
        }
    }

    public LuaValue setEllipsize(U view, Varargs varargs) {
        //TODO 这里需要统一
        final String ellipsizeName = varargs.optjstring(2, TextUtils.TruncateAt.END.name());
        final TextUtils.TruncateAt ellipsize = UDEllipsize.parse(ellipsizeName);
        return view.setEllipsize(ellipsize);
    }

    public LuaValue getEllipsize(U view, Varargs varargs) {
        return valueOf(view.getEllipsize());
    }


    /**
     * 让Label字体大小适应宽度, 设置的字体为最大字体, 如果文字超出会缩小字体.
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue adjustTextSize(U view, Varargs varargs) {
        return view.adjustTextSize();
    }

    public LuaValue adjustFontSize(U view, Varargs varargs) {
        return view.adjustTextSize();
    }
}