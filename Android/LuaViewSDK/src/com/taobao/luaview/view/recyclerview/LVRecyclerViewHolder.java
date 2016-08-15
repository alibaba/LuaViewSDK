package com.taobao.luaview.view.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.taobao.android.luaview.R;
import com.taobao.luaview.userdata.base.UDLuaTable;
import com.taobao.luaview.userdata.list.UDBaseRecyclerView;

import org.luaj.vm2.Globals;

/**
 * ViewHolder for recyclerview
 *
 * @author song
 * @date 15/11/30
 */
public class LVRecyclerViewHolder extends RecyclerView.ViewHolder {
    private Globals mGlobals;
    private UDBaseRecyclerView mLuaUserData;


    public LVRecyclerViewHolder(View container, Globals globals, UDBaseRecyclerView luaUserData) {
        super(container);
        this.mGlobals = globals;
        this.mLuaUserData = luaUserData;
    }

    public void onInit(int position) {
        Object obj = itemView != null ? itemView.getTag(R.id.lv_tag) : null;
        if (obj instanceof UDLuaTable) {
            UDLuaTable cellData = (UDLuaTable) obj;
            initView(cellData, position);//初始化View
            initCallbacks(cellData, position);//点击回调
        }
    }

    /**
     * 初始化View
     *
     * @param cellData
     * @param position
     */
    private void initView(final UDLuaTable cellData, final int position) {
        mGlobals.saveContainer(cellData.getLVViewGroup());
        mLuaUserData.callCellInit(cellData, position);
        mGlobals.restoreContainer();
    }

    /**
     * init onClickListener, onLongClickListener
     *
     * @param position
     */
    public void initCallbacks(final UDLuaTable cellData, final int position) {
        if (itemView != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLuaUserData.onCellClicked(cellData, position);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mLuaUserData.onCellLongClicked(cellData, position);
                }
            });
        }
    }

    /**
     * onLayout view
     *
     * @param position
     */
    public void onLayout(final int position) {
        Object obj = itemView.getTag(R.id.lv_tag);
        if (obj instanceof UDLuaTable) {
            final UDLuaTable cellData = (UDLuaTable) obj;
            mGlobals.saveContainer(cellData.getLVViewGroup());
            mLuaUserData.callCellLayout(cellData, position);
            mGlobals.restoreContainer();
        }
    }

}
