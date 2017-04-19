/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;

import android.widget.ImageView;

import com.taobao.luaview.fun.mapper.LuaViewLib;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * ScaleType 图片布局
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标"})
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
