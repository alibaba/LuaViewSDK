package com.taobao.luaview.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.taobao.luaview.userdata.list.UDRefreshRecyclerView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVRecyclerView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - Refresh Recycler View
 *
 * @author song
 * @date 15/8/20
 */
public class LVRefreshRecyclerView extends SwipeRefreshLayout implements ILVRecyclerView {
    public Globals mGlobals;

    //adapter
    private LVRecyclerView mRecyclerView;

    public LVRefreshRecyclerView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.context);
        this.mGlobals = globals;
//        this.mRecyclerView = new LVRecyclerView(mGlobals, metaTable, varargs, new UDRefreshRecyclerView(this, globals, metaTable, varargs != null ? varargs.arg1() : null));
        this.mRecyclerView = LVRecyclerView.createVerticalView(mGlobals, metaTable, varargs, new UDRefreshRecyclerView(this, globals, metaTable, varargs != null ? varargs.arg1() : null));
        init();
    }

    private void init() {
        mGlobals.saveContainer(mRecyclerView);
        this.addView(mRecyclerView, LuaViewUtil.createRelativeLayoutParamsMM());
        mGlobals.restoreContainer();
        ((UDRefreshRecyclerView) getUserdata()).initPullRefresh();
    }

    /**
     * 停止刷新
     */
    public void startPullDownRefreshing() {
        setRefreshing(true);
    }

    /**
     * 停止刷新
     */
    public void stopPullDownRefreshing() {
        setRefreshing(false);
    }

    public LVRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public UDView getUserdata() {
        return mRecyclerView != null ? mRecyclerView.getUserdata() : null;
    }

    @Override
    public void addLVView(View view, Varargs varargs) {
        //TODO 这里不做操作，因为ListView不应该加子view
    }


    @Override
    public RecyclerView.Adapter getLVAdapter() {
        return mRecyclerView != null ? mRecyclerView.getLVAdapter() : null;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }
}
