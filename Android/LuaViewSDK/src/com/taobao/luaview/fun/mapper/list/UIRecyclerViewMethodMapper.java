/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.list;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListOrRecyclerView;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;
import com.taobao.luaview.userdata.list.UDRecyclerView;
import com.taobao.luaview.util.DimenUtil;

import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * RecyclerView的方法映射
 *
 * @author song
 */
@LuaViewLib
public class UIRecyclerViewMethodMapper<U extends UDRecyclerView> extends UIBaseRecyclerViewMethodMapper<U> {

    private static final String TAG = "UIRecyclerViewMethodMapper";

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), new String[]{});
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            //TODO
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    @Override
    public UDBaseRecyclerView getUDBaseRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }

    @Override
    public UDBaseListOrRecyclerView getUDBaseListOrRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }

    @Override
    public Varargs initParams(U view, Varargs varargs) {
        Varargs ret = super.initParams(view, varargs);
        this.reload(view, varargs);
        return ret;
    }

    @Override
    public Varargs offset(U view, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setScrollXY(view, varargs);
        } else {
            LinearLayoutManager layoutManager = (LinearLayoutManager) view.getLVRecyclerView().getLayoutManager();
            int position = layoutManager.findFirstVisibleItemPosition();
            View firstVisiableChildView = layoutManager.findViewByPosition(position);
            int itemHeight = firstVisiableChildView.getHeight();
            int dy = (position) * itemHeight - firstVisiableChildView.getTop();
            return varargsOf(valueOf(0), valueOf(DimenUtil.pxToDpi(dy)));
        }
    }
}