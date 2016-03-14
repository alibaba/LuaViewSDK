package com.taobao.luaview.userdata.list;

import android.support.v4.widget.SwipeRefreshLayout;

import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVRecyclerView;
import com.taobao.luaview.view.LVRefreshRecyclerView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * 容器类-RecyclerView，模拟OC的section分区实现，Section顺序排列
 *
 * @author song
 * @date 15/8/20
 */
public class UDRefreshRecyclerView extends UDBaseRecyclerView<LVRefreshRecyclerView> {

    public UDRefreshRecyclerView(LVRefreshRecyclerView view, Globals globals, LuaValue metaTable, LuaValue initParams) {
        super(view, globals, metaTable, initParams);
    }

    @Override
    public LVRecyclerView getLVRecyclerView() {
        return getView().getRecyclerView();
    }

    //------------------------------------------Refresh---------------------------------------------

    /**
     * 初始化下拉刷新
     */
    public void initPullRefresh() {
        final LVRefreshRecyclerView view = getView();
        if (view != null && !mCallback.isnil()) {
            view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //这里必须放在handler中执行，否则会造成nullpointerexception(dispatchDraw的时候调用removeView出错) http://dashasalo.com/2013/09/16/android-removeview-crashes-animation-listener/
                    view.post(new Runnable() {//下一帧回调，否则会造成view onDraw乱序。否则在同一帧调用的时候再调用addView, removeView会有bug
                        @Override
                        public void run() {
                            if (!mCallback.isnil()) {
                                LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "PullDown", "pullDown"));
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 是否在刷新
     *
     * @return
     */
    public boolean isRefreshing() {
        return getView() != null && getView().isRefreshing();
    }

    /**
     * 开始刷新
     *
     * @return
     */
    public UDRefreshRecyclerView startPullDownRefreshing() {
        final LVRefreshRecyclerView lv = getView();
        if (lv != null) {
            lv.startPullDownRefreshing();
        }
        return this;
    }

    /**
     * 停止刷新
     *
     * @return
     */
    public UDRefreshRecyclerView stopPullDownRefreshing() {
        final LVRefreshRecyclerView lv = getView();
        if (lv != null) {
            lv.stopPullDownRefreshing();
        }
        return this;
    }


}
