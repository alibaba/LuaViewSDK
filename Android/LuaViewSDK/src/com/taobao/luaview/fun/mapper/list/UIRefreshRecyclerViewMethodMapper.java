/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;
import com.taobao.luaview.userdata.list.UDRefreshRecyclerView;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Refreshable RecyclerView的方法映射
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UIRefreshRecyclerViewMethodMapper<U extends UDRefreshRecyclerView> extends UIBaseRecyclerViewMethodMapper<U> {

    private static final String TAG = "UIRefreshRecyclerViewMethodMapper";
    private static final String[] sMethods = new String[]{
            "refreshEnable",//0
            "initRefreshing",//1
            "isRefreshing",//2
            "startRefreshing",//3
            "stopRefreshing"//4
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return refreshEnable(target, varargs);
            case 1:
                return initRefreshing(target, varargs);
            case 2:
                return isRefreshing(target, varargs);
            case 3:
                return startRefreshing(target, varargs);
            case 4:
                return stopRefreshing(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    @Override
    public UDBaseRecyclerView getUDBaseRecyclerView(Varargs varargs) {
        return getUD(varargs);
    }



    /**
     * 设置是否可以刷新
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue refreshEnable(U view, Varargs varargs){
        final boolean enable = LuaUtil.getBoolean(varargs, 2);
        return view.setRefreshEnable(enable);
    }

    /**
     * 初始化下拉刷新 for iOS，Android不需要
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue initRefreshing(U view, Varargs varargs) {
        return view;
    }

    public LuaValue initPullDownRefreshing(U view, Varargs varargs) {
        return view;
    }

    /**
     * 是否刷新
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue isRefreshing(U view, Varargs varargs) {
        return valueOf(view.isRefreshing());
    }

    public LuaValue isPullDownRefreshing(U view, Varargs varargs) {
        return valueOf(view.isRefreshing());
    }

    /**
     * 停止刷新
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue startRefreshing(U view, Varargs varargs) {
        return view.startPullDownRefreshing();
    }

    public LuaValue startPullDownRefreshing(U view, Varargs varargs) {
        return view.startPullDownRefreshing();
    }

    /**
     * 停止刷新
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue stopRefreshing(U view, Varargs varargs) {
        return view.stopPullDownRefreshing();
    }

    public LuaValue stopPullDownRefreshing(U view, Varargs varargs) {
        return view.stopPullDownRefreshing();
    }

    @Override
    public Varargs initParams(U view, Varargs varargs) {
        Varargs ret = super.initParams(view, varargs);
        this.reload(view, varargs);
        return ret;
    }

}