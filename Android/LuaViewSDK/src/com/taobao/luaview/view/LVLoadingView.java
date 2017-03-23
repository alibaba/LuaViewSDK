/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ProgressBar;

import com.taobao.luaview.userdata.ui.UDLoadingView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-Loading
 *
 * @author song
 * @date 15/8/20
 */
public class LVLoadingView extends LVViewGroup implements ILVView {
    private ProgressBar mProgressBar;

    public LVLoadingView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
        init();
    }

    private void init() {
        mProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmallInverse);
        final LayoutParams layoutParams = LuaViewUtil.createRelativeLayoutParamsMM();
        this.addView(mProgressBar, layoutParams);
        this.setVisibility(View.GONE);
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        if (mProgressBar.getIndeterminateDrawable() != null) {
            mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public UDViewGroup createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
        return new UDLoadingView(this, globals, metaTable, varargs);
    }
}
