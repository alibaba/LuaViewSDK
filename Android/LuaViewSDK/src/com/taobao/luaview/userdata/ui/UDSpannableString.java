/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import com.taobao.luaview.extend.CustomTypefaceSpan;
import com.taobao.luaview.extend.WeightStyleSpan;
import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.userdata.constants.UDFontStyle;
import com.taobao.luaview.userdata.constants.UDFontWeight;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


public class UDSpannableString extends BaseUserdata {

    public UDSpannableString(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(new SpannableStringBuilder(), globals, metaTable, varargs);
        init(varargs);
    }

    public SpannableStringBuilder getSpannableStringBuilder() {
        return (SpannableStringBuilder) userdata();
    }

    /**
     * 初始化数据
     */
    public void init(Varargs initParams) {
        LuaValue text = NIL, config = NIL;
        if (initParams != null) {
            text = getInitParam1();
            config = getInitParam2();
        }
        initSpannableStringBuilder(text, config);
    }


    private void initSpannableStringBuilder(LuaValue text, LuaValue config) {
        SpannableStringBuilder spannableStringBuilder = getSpannableStringBuilder();
        if (text != null && text.isstring()) {
            spannableStringBuilder = spannableStringBuilder.append(text.tojstring());
        }

        if (spannableStringBuilder.length() > 0) {
            if (config != null && config.istable()) {
                final int end = spannableStringBuilder.length();
                final int fontSize = DimenUtil.spToPx(config.get("fontSize").optint(-1));
                final Integer fontColor = ColorUtil.parse(LuaUtil.getValue(config, "fontColor"));
                final String fontName = config.get("fontName").optjstring(null);

                final LuaValue weightValue = config.get("fontWeight");
                final int fontWeight = LuaUtil.isNumber(weightValue) ? weightValue.optint(UDFontWeight.WEIGHT_NORMAL_INT) : UDFontWeight.getValue(weightValue.optjstring(UDFontWeight.WEIGHT_NORMAL));

                final LuaValue styleValue = config.get("fontStyle");
                final int fontStyle = LuaUtil.isNumber(styleValue) ? styleValue.optint(Typeface.NORMAL) : UDFontStyle.getValue(styleValue.optjstring(UDFontStyle.STYLE_NORMAL));

                final Integer backgroundColor = ColorUtil.parse(LuaUtil.getValue(config, "backgroundColor"));
                final boolean strikethrough = config.get("strikethrough").optboolean(false);
                final boolean underline = config.get("underline").optboolean(false);

                if (fontSize != -1) {//字体大小
                    spannableStringBuilder.setSpan(new AbsoluteSizeSpan(fontSize), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (fontColor != null) {//字体颜色
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(fontColor), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (fontName != null && getLuaResourceFinder() != null) {//字体
                    spannableStringBuilder.setSpan(new CustomTypefaceSpan(fontName, getLuaResourceFinder().findTypeface(fontName)), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (fontWeight != -1 && fontWeight > UDFontWeight.WEIGHT_NORMAL_INT) {//文字Weight
                    spannableStringBuilder.setSpan(new WeightStyleSpan(fontWeight), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (fontStyle != -1 && (fontStyle >= Typeface.NORMAL && fontStyle <= Typeface.BOLD_ITALIC)) {//文字样式
                    spannableStringBuilder.setSpan(new StyleSpan(fontStyle), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (backgroundColor != null) {//背景色
                    spannableStringBuilder.setSpan(new BackgroundColorSpan(backgroundColor), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (strikethrough) {//删除线
                    spannableStringBuilder.setSpan(new StrikethroughSpan(), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (underline) {//下划线
                    spannableStringBuilder.setSpan(new UnderlineSpan(), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    /**
     * 新增一段内容，可以使字符串，也可以是一段spannable
     *
     * @param ssb
     * @return
     */
    public LuaValue append(LuaValue ssb) {
        if (getSpannableStringBuilder() != null) {
            getSpannableStringBuilder().append(ssb instanceof UDSpannableString ? ((UDSpannableString) ssb).getSpannableStringBuilder() : ssb.optjstring(""));
        }
        return this;
    }

    /**
     * 两则字符串相加，产生一个新的字符串
     *
     * @param ssb
     * @return
     */
    @Override
    public LuaValue add(LuaValue ssb) {
        final UDSpannableString newSpannableString = new UDSpannableString(getGlobals(), getmetatable(), null);
        SpannableStringBuilder spannableStringBuilder = getSpannableStringBuilder();
        if (spannableStringBuilder != null) {
            newSpannableString.getSpannableStringBuilder().append(spannableStringBuilder);
        }
        newSpannableString.getSpannableStringBuilder().append(ssb instanceof UDSpannableString ? ((UDSpannableString) ssb).getSpannableStringBuilder() : ssb.optjstring(""));
        return newSpannableString;
    }


    @Override
    public String tojstring() {
        return String.valueOf(getSpannableStringBuilder());
    }

}
