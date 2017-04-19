/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.RelativeLayout;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Align 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标", "iOS无Start、End"})
public class UDAlign extends BaseLuaTable {

    public UDAlign(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        initViewAlign();
    }

    /**
     * view的布局
     */
    private void initViewAlign() {
        set("LEFT", RelativeLayout.ALIGN_PARENT_LEFT);
        set("TOP", RelativeLayout.ALIGN_PARENT_TOP);
        set("RIGHT", RelativeLayout.ALIGN_PARENT_RIGHT);
        set("BOTTOM", RelativeLayout.ALIGN_PARENT_BOTTOM);
        set("CENTER", RelativeLayout.CENTER_IN_PARENT);
        set("H_CENTER", RelativeLayout.CENTER_HORIZONTAL);
        set("V_CENTER", RelativeLayout.CENTER_VERTICAL);

        intViewAlignV17();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void intViewAlignV17() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            set("START", RelativeLayout.ALIGN_PARENT_START);
            set("END", RelativeLayout.ALIGN_PARENT_END);
        } else {
            set("START", RelativeLayout.ALIGN_PARENT_LEFT);
            set("END", RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

}
