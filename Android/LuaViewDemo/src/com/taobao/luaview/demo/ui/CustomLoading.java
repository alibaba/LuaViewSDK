/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.demo.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.LVCustomPanel;
import com.taobao.luaview.view.LVLoadingView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * @author song
 * @date 15/11/3
 */
public class CustomLoading extends LVCustomPanel {

    public CustomLoading(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
    }

    @Override
    public void initPanel() {
        final View lvLoadingView = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmallInverse);
        LayoutParams relativeLayout = LuaViewUtil.createRelativeLayoutParamsWW();
        relativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        lvLoadingView.setVisibility(View.VISIBLE);
        addView(lvLoadingView, relativeLayout);
    }
}
