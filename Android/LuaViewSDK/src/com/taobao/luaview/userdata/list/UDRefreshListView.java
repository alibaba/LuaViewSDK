/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.list;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.taobao.luaview.userdata.refreshable.OnLVRefreshListener;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVRefreshListView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * 容器类-ListView，模拟OC的section分区实现，Section顺序排列
 *
 * @author song
 * @date 15/8/20
 */
public class UDRefreshListView extends UDBaseListView<LVRefreshListView> implements OnLVRefreshListener {

    public UDRefreshListView(LVRefreshListView view, Globals globals, LuaValue metaTable, Varargs initParams) {
        super(view, globals, metaTable, initParams);
    }

    @Override
    public ListView getListView() {
        return getView() != null ? getView().getListView() : null;
    }

    //------------------------------------------Refresh---------------------------------------------

    /**
     * 初始化下拉刷新
     */
    public void initPullRefresh() {
        final LVRefreshListView view = getView();
        if (view != null && LuaUtil.isValid(mCallback)) {
            view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    UDRefreshListView.this.onRefresh(null);
                }
            });
        }
    }

    /**
     * 设置是否可以刷新
     *
     * @param enable
     */
    public UDRefreshListView setRefreshEnable(boolean enable) {
        final LVRefreshListView view = getView();
        if (view != null) {
            view.setEnabled(enable);

            if (!enable) {
                view.setOnRefreshListener(null);
            }
        }
        return this;
    }

    @Override
    public void onRefresh(final Object param) {
        final LVRefreshListView view = getView();
        if (view != null && LuaUtil.isValid(mCallback)) {
            //这里必须放在handler中执行，否则会造成nullpointerexception(dispatchDraw的时候调用removeView出错) http://dashasalo.com/2013/09/16/android-removeview-crashes-animation-listener/
            view.post(new Runnable() {//下一帧回调，否则会造成view onDraw乱序。否则在同一帧调用的时候再调用addView, removeView会有bug
                @Override
                public void run() {
                    if (LuaUtil.isValid(mCallback)) {
                        LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "PullDown", "pullDown"));
                    }

                    if (param instanceof OnLVRefreshListener) {
                        ((OnLVRefreshListener) param).onRefresh(null);
                    }
                }
            });
        } else {
            if (param instanceof OnLVRefreshListener) {
                ((OnLVRefreshListener) param).onRefresh(null);
            }
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
    public UDRefreshListView startPullDownRefreshing() {
        final LVRefreshListView lv = getView();
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
    public UDRefreshListView stopPullDownRefreshing() {
        final LVRefreshListView lv = getView();
        if (lv != null) {
            lv.stopPullDownRefreshing();
        }
        return this;
    }
}
