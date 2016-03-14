package com.taobao.luaview.view.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.taobao.android.luaview.R;
import com.taobao.luaview.userdata.base.UDLuaTable;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.Globals;

/**
 * Adapter for RecyclerView
 *
 * @author song
 * @date 15/11/30
 */
public class LVRecyclerViewAdapter extends RecyclerView.Adapter<LVRecyclerViewHolder> {

    private UDBaseRecyclerView mLuaUserData;
    private Globals mGlobals;

    public LVRecyclerViewAdapter(Globals globals, UDBaseRecyclerView luaViewData) {
        this.mGlobals = globals;
        this.mLuaUserData = luaViewData;
    }

    @Override
    public LVRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View itemView = createItemView(viewType);
        return new LVRecyclerViewHolder(itemView, mGlobals, mLuaUserData);
    }

    /**
     * create item view for view holder
     *
     * @param viewType
     * @return
     */
    private View createItemView(final int viewType) {
        final UDViewGroup layout = new UDViewGroup(createLayout(), mGlobals, mLuaUserData.getmetatable(), null);
        //对外数据封装，必须使用LuaTable
        final UDLuaTable cellData = new UDLuaTable(layout);
        View itemView = null;
        //View封装
        if (this.mLuaUserData.hasCellSize(viewType)) {//有Size的定义
            final LVViewGroup cellView = createLayout();
            cellView.addView(layout.getView());
            itemView = cellView;
        } else {
            itemView = layout.getView();
        }
        itemView.setTag(R.id.lv_tag, cellData);
        return itemView;
    }

    @Override
    public int getItemViewType(int position) {//TODO 从0开始，如何去除非viewtype内容
        return this.mLuaUserData.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(LVRecyclerViewHolder holder, int position) {
        if (position >= 0 && position < getItemCount()) {
            if (holder != null) {
                if (this.mLuaUserData.hasCellSize(getItemViewType(position))) {
                    if (holder.itemView != null && holder.itemView.getTag(R.id.lv_tag) instanceof UDLuaTable) {
                        UDLuaTable cellData = (UDLuaTable) holder.itemView.getTag(R.id.lv_tag);
                        initCellSize(cellData, position);//初始化View的size，这里因为每个cell的宽度高度可能不一样，需要再调用一遍
                    }
                }

                if (holder.itemView != null && holder.itemView.getTag(R.id.lv_tag_init) == null) {//是否已经调用过onInit，如果调用过则不重复调用
                    holder.onInit(position);
                    holder.itemView.setTag(R.id.lv_tag_init, true);
                }
                holder.onLayout(position);
            }
        }
    }

    /**
     * 初始化cell的size
     *
     * @param cell
     * @param position
     */
    private void initCellSize(final UDLuaTable cell, final int position) {
        final View view = cell.getView();
        if (view != null) {
            int[] size = this.mLuaUserData.callCellSize(cell, position);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            layoutParams.width = size[0];
            layoutParams.height = size[1];
            view.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return this.mLuaUserData.getTotalCount();
    }

    /**
     * 创建 cell 的布局
     *
     * @return
     */
    private LVViewGroup createLayout() {
        return new LVViewGroup(mGlobals, mLuaUserData.getmetatable(), null);
    }

}
