/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.widget.TextView;

import com.taobao.luaview.util.TextUtil;
import com.taobao.luaview.util.TypefaceUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Label 数据封装
 *
 * @param <T>
 * @author song
 */
public class UDTextView<T extends TextView> extends UDView<T> {
    private int mMaxLines = 1;
    private int mMinLines = 1;
    private int mTextAlignment = TextView.TEXT_ALIGNMENT_GRAVITY;

    public UDTextView(T view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    /**
     * 设置显示文字
     *
     * @param text String
     */
    public UDTextView setText(CharSequence text) {
        final T view = getView();
        if (view != null) {
            view.setText(text);
        }
        return this;
    }

    /**
     * get text
     *
     * @return
     */
    public CharSequence getText() {
        return getView() != null ? getView().getText() : "";
    }

    /**
     * 设置显示文字颜色
     *
     * @param color int
     */
    public UDView setTextColor(Integer color) {
        if (color != null) {
            final T view = getView();
            if (view != null) {
                view.setTextColor(color);
            }
        }
        return this;
    }

    /**
     * 获取文字颜色
     *
     * @return
     */
    public int getTextColor() {
        return getView() != null ? getView().getTextColors().getDefaultColor() : 0;
    }

    /**
     * 设置字体和大小
     *
     * @param fontName
     * @param fontSize
     * @return
     */
    public UDTextView setFont(String fontName, float fontSize) {
        setFont(fontName);
        setTextSize(fontSize);
        return this;
    }

    /**
     * 设置字体
     *
     * @param fontName
     * @return
     */
    public UDTextView setFont(String fontName) {
        if (fontName != null) {
            final T view = getView();
            if (view != null && getLuaResourceFinder() != null) {
                view.setTypeface(getLuaResourceFinder().findTypeface(fontName));
            }
        }
        return this;
    }

    /**
     * 获取字体
     *
     * @return
     */
    public String getFont() {
        return getView() != null ? TypefaceUtil.getTypefaceName(getView().getTypeface()) : "";
    }

    /**
     * 设置字体
     *
     * @param textSize
     * @return
     */
    public UDTextView setTextSize(float textSize) {
        if (textSize > -1) {
            final T view = getView();
            if (view != null) {
                view.setTextSize(textSize);
            }
        }
        return this;
    }

    /**
     * 获取文字大小
     *
     * @return
     */
    public float getTextSize() {
        return getView() != null ? getView().getTextSize() : 0f;
    }


    /**
     * 设置文字的对齐
     *
     * @param gravity
     * @return
     */
    public UDTextView setGravity(int gravity) {
        final T view = getView();
        if (gravity > 0 && view != null) {
            view.setGravity(gravity);
        }
        return this;
    }

    /**
     * 文字对齐
     *
     * @return
     */
    public int getGravity() {
        return getView() != null ? getView().getGravity() : -1;
    }


    /**
     * 设置文字的对齐
     *
     * @param gravity
     * @return
     */
    public UDTextView setTextAlign(int gravity) {
        return setGravity(gravity);
    }

    /**
     * 获得文字的对齐方式
     *
     * @return
     */
    public int getTextAlign() {
        return getGravity();
    }


    /**
     * 设置文字的绘制
     *
     * @param alignment
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public UDTextView setTextAlignment(int alignment) {
        mTextAlignment = alignment;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final T view = getView();
            if (alignment > 0 && view != null) {
                view.setTextAlignment(alignment);
            }
        }
        return this;
    }

    /**
     * 获得文字的对齐方式
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int getTextAlignment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getView() != null ? getView().getTextAlignment() : TextView.TEXT_ALIGNMENT_GRAVITY;
        } else {
            return mTextAlignment;
        }
    }

    /**
     * 设置文字的行数
     *
     * @param lines
     * @return
     */
    public UDTextView setLines(int lines) {
        if (lines > 0) {
            final T view = getView();
            if (view != null) {
                view.setLines(lines);
            }
        }
        return this;
    }

    /**
     * 获得行数
     *
     * @return
     */
    public int getLines() {
        return getView() != null ? getView().getLineCount() : 0;
    }

    /**
     * 设置文字的最小行数
     *
     * @param lines
     * @return
     */
    public UDTextView setMinLines(int lines) {
        if (lines > 0) {
            final T view = getView();
            if (view != null) {
                mMinLines = lines;
                view.setMinLines(lines);
            }
        }
        return this;
    }

    /**
     * 获得最小行数
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getMinLines() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return getView() != null ? getView().getMinLines() : 1;
        } else {
            return mMinLines;
        }
    }

    /**
     * 设置文字的最大行数
     *
     * @param lines
     * @return
     */
    public UDTextView setMaxLines(int lines) {
        final T view = getView();
        if (view != null) {
            this.mMaxLines = lines <= 0 ? Integer.MAX_VALUE : lines;
            view.setMaxLines(mMaxLines);
        }

        return this;
    }

    /**
     * 获得最大行数
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getMaxLines() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return getView() != null ? getView().getMaxLines() : 1;
        } else {
            return mMaxLines;
        }
    }


    /**
     * 设置超出部分的显示方式
     *
     * @param truncateAt
     * @return
     */
    public UDTextView setEllipsize(TextUtils.TruncateAt truncateAt) {
        final T view = getView();
        if (view != null) {
            view.setEllipsize(truncateAt);
        }
        return this;
    }

    public String getEllipsize() {
        return getView() != null ? getView().getEllipsize().name() : TextUtils.TruncateAt.END.name();
    }

    /**
     * 修改文字大小
     *
     * @return
     */
    public UDTextView adjustTextSize() {
        final T view = getView();
        if (view != null) {
            TextUtil.adjustTextSize(view);
        }
        return this;
    }

    /**
     * 修改文字的frame
     *
     * @return
     */
    @Override
    public UDTextView adjustSize() {
        final T view = getView();
        if (view != null) {
            TextUtil.adjustSize(view);
        }
        return this;
    }
}
