/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.graphics.Canvas;
import android.view.ViewGroup;

import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class UDCustomView<T extends ViewGroup> extends UDViewGroup<T> {
    private LuaValue mOnDraw;
    private UDCanvas mCanvas;

    public UDCustomView(T view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    @Override
    public UDCustomView setCallback(LuaValue callbacks) {
        super.setCallback(callbacks);
        if (this.mCallback != null) {
            mOnDraw = LuaUtil.getFunction(mCallback, "onDraw", "OnDraw");
        }
        return this;
    }

    public LuaValue setOnDrawCallback(LuaValue mOnDraw) {
        this.mOnDraw = mOnDraw;
        return this;
    }

    public LuaValue getOnDrawCallback() {
        return this.mOnDraw;
    }

    public boolean hasOnDrawCallback() {
        return this.mOnDraw != null && this.mOnDraw.isfunction();
    }

    public LuaValue callOnDraw(LVViewGroup viewGroup, Canvas canvas) {
        if (mCanvas == null) {
            mCanvas = new UDCanvas(viewGroup, canvas, getGlobals(), getmetatable(), null);
        } else {
            mCanvas.setCanvas(canvas);
            mCanvas.setTarget(viewGroup);
        }
        return LuaUtil.callFunction(mOnDraw, mCanvas);
    }
}
