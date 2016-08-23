package com.taobao.luaview.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.BaseAdapter;

import com.taobao.luaview.userdata.list.UDRefreshListView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVListView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - Refresh TableView
 *
 * @author song
 * @date 15/8/20
 */
public class LVRefreshListView extends SwipeRefreshLayout implements ILVListView {
    //adapter
    private LVListView mListView;

    public LVRefreshListView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mListView = new LVListView(globals, metaTable, varargs, new UDRefreshListView(this, globals, metaTable, varargs != null ? varargs.arg1() : null));
        init(globals);
    }

    private void init(Globals globals) {
        globals.saveContainer(mListView);
        this.addView(mListView, LuaViewUtil.createRelativeLayoutParamsMM());
        globals.restoreContainer();

        if (globals.getLuaView() == null || globals.getLuaView().isRefreshContainerEnable() == false) {
            this.setEnabled(false);
        } else {
            ((UDRefreshListView) getUserdata()).initPullRefresh();
        }
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

    @Override
    public UDView getUserdata() {
        return mListView != null ? mListView.getUserdata() : null;
    }

    @Override
    public void addLVView(View view, Varargs varargs) {
        //TODO 这里不做操作，因为ListView不应该加子view
    }

    public LVListView getListView() {
        return mListView;
    }

    @Override
    public BaseAdapter getLVAdapter() {
        return mListView != null ? mListView.getLVAdapter() : null;
    }

    @Override
    public void addHeader(View header) {
        if (mListView != null) {
            mListView.addHeader(header);
        }
    }

    @Override
    public void removeHeader() {
        if (mListView != null) {
            mListView.removeHeader();
        }
    }

    @Override
    public void addFooter(View footer) {
        if (mListView != null) {
            mListView.addFooter(footer);
        }
    }

    @Override
    public void removeFooter() {
        if (mListView != null) {
            mListView.removeFooter();
        }
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }
}
