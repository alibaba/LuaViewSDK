/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.base;

import android.view.View;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.LuaTable;

/**
 * 封装LuaTable，可以保存View
 *
 * @author song
 * @date 15/9/2
 */
public class UDLuaTable extends LuaTable {
    private UDView udView;

    public UDLuaTable(UDView udView) {
        this.udView = udView;
        this.set("window", udView);//TODO 优化，父容器音容
    }

    public void setUdView(UDView udView) {
        this.udView = udView;
    }

    public View getView() {
        if (udView != null) {
            return udView.getView();
        }
        return null;
    }

    public LVViewGroup getLVViewGroup() {
        if (getView() instanceof LVViewGroup) {
            return (LVViewGroup) getView();
        }
        return null;
    }

    public UDView getUdView() {
        return udView;
    }
}
