/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.list;

import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.interfaces.ILVListView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * UDBaseListView 封装
 *
 * @author song
 * @date 15/10/23
 */
public abstract class UDBaseListView<T extends ViewGroup> extends UDBaseListOrRecyclerView<T> {

    //header & footer
    private LuaValue mHeader;
    private LuaValue mFooter;

    public UDBaseListView(T view, Globals globals, LuaValue metaTable, Varargs initParams) {
        super(view, globals, metaTable, initParams);
    }

    public abstract ListView getListView();


    @Override
    public UDBaseListOrRecyclerView reload(Integer section, Integer row) {
        final T lv = getView();
        if (lv instanceof ILVListView) {
            init();//重新初始化数据
            if (((ILVListView) lv).getLVAdapter() != null) {
                ((ILVListView) lv).getLVAdapter().notifyDataSetChanged();
            }
        }
        return this;
    }

    @Override
    public void initOnScrollCallback(T view) {
        if (view instanceof ListView) {
            final ListView listview = (ListView) view;
            if (LuaUtil.isValid(mCallback) || mLazyLoad) {
                listview.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        updateAllChildScrollState(view, scrollState);

                        if (LuaUtil.isValid(mCallback)) {//callback
                            switch (scrollState) {
                                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: {
                                    final int itemPosition = listview.getFirstVisiblePosition() - listview.getHeaderViewsCount();
                                    final int section = getSectionByPosition(itemPosition);
                                    final int row = getRowInSectionByPosition(itemPosition);
                                    LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "ScrollBegin", "scrollBegin"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
                                    break;
                                }
                                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                                    break;
                                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: {
                                    final int itemPosition = listview.getFirstVisiblePosition() - listview.getHeaderViewsCount();
                                    final int section = getSectionByPosition(itemPosition);
                                    final int row = getRowInSectionByPosition(itemPosition);
                                    LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "ScrollEnd", "scrollEnd"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row));
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if (LuaUtil.isValid(mCallback)) {
                            final int itemPosition = firstVisibleItem - listview.getHeaderViewsCount();
                            final int section = getSectionByPosition(itemPosition);
                            final int row = getRowInSectionByPosition(itemPosition);
                            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "Scrolling", "scrolling"), LuaUtil.toLuaInt(section), LuaUtil.toLuaInt(row), valueOf(visibleItemCount));
                        }
                    }
                });
            }
        }
    }

    //------------------------------------------header----------------------------------------------

    /**
     * 调用Header函数
     *
     * @return
     */
    public UDView setHeader(LuaValue header) {
        final T lv = getView();
        if (lv instanceof ILVListView) {
            if (header instanceof UDView) {
                mHeader = header;
                ((ILVListView) lv).addHeader(((UDView) header).getView());
            } else if (header == null || header.isnil()) {
                mHeader = NIL;
                ((ILVListView) lv).removeHeader();
            }
        }
        return this;
    }

    public LuaValue getHeader() {
        return mHeader;
    }

    //------------------------------------------footer----------------------------------------------

    /**
     * 调用Header函数
     *
     * @return
     */
    public UDView setFooter(LuaValue footer) {
        final T lv = getView();
        if (lv instanceof ILVListView) {
            if (footer instanceof UDView) {
                mFooter = footer;
                ((ILVListView) lv).addFooter(((UDView) footer).getView());
            } else if (footer == null || footer.isnil()) {
                mFooter = NIL;
                ((ILVListView) lv).removeFooter();
            }
        }
        return this;
    }

    public LuaValue getFooter() {
        return mFooter;
    }


    //-------------------------------------------scroll---------------------------------------------

    /**
     * listview滚动到顶部
     *
     * @param animate 是否动画
     * @return
     */
    public UDBaseListOrRecyclerView scrollToTop(final int offset, final boolean animate) {
        final ListView lv = getListView();
        if (lv != null) {
            if (animate) {
                if (lv.getFirstVisiblePosition() > 7) {//hack fast scroll
                    lv.setSelection(7);
                }
                lv.smoothScrollToPositionFromTop(0, offset);
            } else {
                lv.setSelectionFromTop(0, offset);
            }
        }
        return this;
    }

    /**
     * listview滚动到某个位置
     *
     * @param section
     * @param rowInSection
     * @param offset
     * @return
     */
    public UDBaseListOrRecyclerView scrollToItem(final int section, final int rowInSection, final int offset, final boolean animate) {
        final ListView lv = getListView();
        if (lv != null) {
            if (animate) {
                lv.smoothScrollToPositionFromTop(getPositionBySectionAndRow(section, rowInSection), offset);
            } else {
                lv.setSelectionFromTop(getPositionBySectionAndRow(section, rowInSection), offset);
            }
        }
        return this;
    }


    /**
     * 设置分隔条高度
     *
     * @param height
     * @return
     */
    @Override
    public UDBaseListOrRecyclerView setMiniSpacing(int height) {
        final ListView lv = getListView();
        if (lv != null && height >= 0) {
            lv.setDividerHeight(height);
        }
        return this;
    }

    /**
     * 获取分隔高度
     *
     * @return
     */
    @Override
    public int getMiniSpacing() {
        return getListView() != null ? getListView().getDividerHeight() : 0;
    }
}