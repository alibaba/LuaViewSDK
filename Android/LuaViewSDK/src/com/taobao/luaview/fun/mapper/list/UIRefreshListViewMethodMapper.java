package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListView;
import com.taobao.luaview.userdata.list.UDRefreshListView;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Refreshable ListView的方法映射
 * @author song
 */
@LuaViewLib
public class UIRefreshListViewMethodMapper<U extends UDRefreshListView> extends UIBaseListViewMethodMapper<U> {


    @Override
    public UDBaseListView getUDBaseListView(Varargs varargs) {
        return getUD(varargs);
    }


    /**
     * 初始化下拉刷新 for iOS，Android不需要
     *
     * @param view
     * @param varargs
     * @return
     */
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

}