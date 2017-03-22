/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;

import android.view.Gravity;

import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * TextAlign 文本布局
 *
 * @author song
 * @date 15/9/6
 */
public class UDTextAlign extends BaseLuaTable {
    public static final int LEFT = Gravity.LEFT | Gravity.CENTER_VERTICAL;
    public static final int CENTER = Gravity.CENTER;
    public static final int RIGHT = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

    public UDTextAlign(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        initTextViewAlignment();
    }


    /**
     * 文字的排版
     */
    private void initTextViewAlignment() {
        set("LEFT", LEFT);//默认竖直居中，ios不支持居上
        set("CENTER", CENTER);
        set("RIGHT", RIGHT);//默认竖直居中，ios不支持居上


        //以下在两端统一后开启
        /*
        set("LEFT", Gravity.LEFT);
        set("RIGHT", Gravity.RIGHT);
        set("TOP", Gravity.TOP);
        set("BOTTOM", Gravity.BOTTOM);
        set("CENTER", Gravity.CENTER);
        set("HCENTER", Gravity.CENTER_HORIZONTAL);
        set("VCENTER", Gravity.CENTER_VERTICAL);
        */
    }

}
