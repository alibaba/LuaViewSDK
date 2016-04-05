package com.taobao.luaview.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.taobao.luaview.userdata.base.UDLuaTable;
import com.taobao.luaview.userdata.list.UDBaseListView;
import com.taobao.luaview.userdata.list.UDListView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.adapter.LVListViewAdapter;
import com.taobao.luaview.view.interfaces.ILVListView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - ListView
 *
 * @author song
 * @date 15/8/20
 */
public class LVListView extends ListView implements ILVListView {
    public Globals mGlobals;
    private UDBaseListView mLuaUserdata;

    //adapter
    private LVListViewAdapter mAdapter;

    //header & footer container
    private LVViewGroup mHeaderContainer;
    private LVViewGroup mFooterContainer;

    public LVListView(Globals globals, LuaValue metaTable, Varargs varargs, UDBaseListView udBaseListView) {
        super(globals.context);
        this.mGlobals = globals;
        this.mLuaUserdata = udBaseListView != null ? udBaseListView : new UDListView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        init();
    }

    private void init() {
        this.mGlobals.saveContainer(this);
        initData();
        this.mGlobals.restoreContainer();
    }

    /**
     * 初始化子元素
     */
    private void initData() {
//        initHeaderContainer();
//        initFooterContainer();
        mAdapter = new LVListViewAdapter(mGlobals, mLuaUserdata);
        this.setAdapter(mAdapter);
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UDLuaTable cellData = (UDLuaTable) view.getTag();
                final int row = position - LVListView.this.getHeaderViewsCount();
                mAdapter.onCellClicked(cellData, row);
            }
        });
        this.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final UDLuaTable cellData = (UDLuaTable) view.getTag();
                final int row = position - LVListView.this.getHeaderViewsCount();
                return mAdapter.onCellLongClicked(cellData, row);
            }
        });
        this.setSelector(android.R.color.transparent);
        this.setDivider(new ColorDrawable(Color.TRANSPARENT));//设置divider颜色透明
        mLuaUserdata.initOnScrollCallback(this);
    }


    private void initHeaderContainer() {
        mHeaderContainer = new LVViewGroup(mGlobals, mLuaUserdata.getmetatable(), null);
        mHeaderContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.addHeaderView(mHeaderContainer);
        this.setHeaderDividersEnabled(false);
    }

    private void initFooterContainer() {
        mFooterContainer = new LVViewGroup(mGlobals, mLuaUserdata.getmetatable(), null);
        mFooterContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.addFooterView(mFooterContainer);
        this.setFooterDividersEnabled(false);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void addLVView(View view, Varargs varargs) {
        //TODO 这里不做操作，因为ListView不应该加子view
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }

    @Override
    public BaseAdapter getLVAdapter() {
        return mAdapter;
    }

    @Override
    public void addHeader(View view) {
        if (mHeaderContainer == null) {
            initHeaderContainer();
        }
        mHeaderContainer.addLVView(view, null);
    }

    @Override
    public void removeHeader() {
        if (mHeaderContainer != null) {
            mHeaderContainer.removeAllViews();
        }
    }

    @Override
    public void addFooter(View view) {
        if (mFooterContainer != null) {
            initFooterContainer();
        }
        mFooterContainer.addLVView(view, null);
    }

    @Override
    public void removeFooter() {
        if (mFooterContainer != null) {
            mFooterContainer.removeAllViews();
        }
    }
}
