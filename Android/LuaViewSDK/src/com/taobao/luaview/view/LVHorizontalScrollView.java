/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.taobao.luaview.userdata.ui.UDHorizontalScrollView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - HorizontalScrollView
 *
 * @author song
 * @date 15/8/20
 */
public class LVHorizontalScrollView extends HorizontalScrollView implements ILVViewGroup {
    private UDHorizontalScrollView mLuaUserdata;

    //root view
    private LVViewGroup mContainer;

    public LVHorizontalScrollView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDHorizontalScrollView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        init(globals);
    }

    private void init(Globals globals) {
        this.setHorizontalScrollBarEnabled(false);//不显示滚动条
        mContainer = new LVViewGroup(globals, mLuaUserdata.getmetatable(), null);
        super.addView(mContainer, LuaViewUtil.createRelativeLayoutParamsMM());
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        if(mContainer != view) {
            mContainer.addView(view, layoutParams);
        }
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {
    }

    public LVViewGroup getContainer() {
        return mContainer;
    }
}
