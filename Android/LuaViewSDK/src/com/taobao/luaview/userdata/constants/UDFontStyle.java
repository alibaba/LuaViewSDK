package com.taobao.luaview.userdata.constants;

import android.graphics.Typeface;
import android.text.TextUtils;

import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * FontStyle 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
public class UDFontStyle extends BaseLuaTable {

    public static final String STYLE_NORMAL = "normal";
    public static final String STYLE_ITALIC = "italic";
    public static final String STYLE_BOLD = "bold";

    public UDFontStyle(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        this.set("NORMAL", Typeface.NORMAL);
        this.set("ITALIC", Typeface.ITALIC);
        this.set("BOLD", Typeface.BOLD);
    }

    public static int getValue(final String name) {
        if (!TextUtils.isEmpty(name)) {
            if (STYLE_NORMAL.equalsIgnoreCase(name)) {
                return Typeface.NORMAL;
            } else if (STYLE_BOLD.equalsIgnoreCase(name)) {
                return Typeface.BOLD;
            } else if (STYLE_ITALIC.equalsIgnoreCase(name)) {
                return Typeface.ITALIC;
            }
        }
        return Typeface.NORMAL;
    }

    public static String getName(int type) {
        switch (type) {
            case Typeface.ITALIC:
                return STYLE_ITALIC;
            case Typeface.BOLD:
                return STYLE_BOLD;
            default:
                return STYLE_NORMAL;
        }
    }
}
