package com.taobao.luaview.userdata.constants;

import android.widget.ImageView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * ScaleType 图片布局
 *
 * @author song
 * @date 15/9/6
 */
public class UDImageScaleType extends LuaTable {
    private Globals mGlobals;

    public UDImageScaleType(Globals globals, LuaValue metatable) {
        this.mGlobals = globals;
        init();
    }

    private void init() {
        initScaleType();
    }

    /**
     * init scale type
     */
    private void initScaleType() {
        set("FIT_XY", ImageView.ScaleType.FIT_XY.name());
        set("FIT_START", ImageView.ScaleType.FIT_START.name());
        set("FIT_END", ImageView.ScaleType.FIT_END.name());
        set("FIT_CENTER", ImageView.ScaleType.FIT_CENTER.name());
        set("CENTER", ImageView.ScaleType.CENTER.name());
        set("CENTER_CROP", ImageView.ScaleType.CENTER_CROP.name());
        set("CENTER_INSIDE", ImageView.ScaleType.CENTER_INSIDE.name());
        set("MATRIX", ImageView.ScaleType.MATRIX.name());
    }

    public static ImageView.ScaleType parse(String scaleTypeName) {
        return parse(scaleTypeName, ImageView.ScaleType.FIT_XY);
    }

    public static ImageView.ScaleType parse(String scaleTypeName, ImageView.ScaleType defaultValue) {
        try {
            return ImageView.ScaleType.valueOf(scaleTypeName);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

}
