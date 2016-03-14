package com.taobao.luaview.userdata.ui;

import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVViewPager;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;


/**
 * 容器类-ViewPager
 *
 * @author song
 * @date 15/8/20
 */
public class UDViewPager extends UDViewGroup<LVViewPager> {
    private LuaValue mViewPagerIndicator = NIL;
    //初始化数据
    private LuaValue mPagesDelegate = NIL;

    public UDViewPager(LVViewPager viewPager, Globals globals, LuaValue metaTable, LuaValue initParams) {
        super(viewPager, globals, metaTable, initParams);
        init();
    }

    public void init() {
        if (!this.initParams.isnil()) {
            this.mPagesDelegate = !this.initParams.isnil() ? this.initParams.get("Pages") : NIL;
            this.mCallback = !this.initParams.isnil() ? this.initParams.get("Callback") : NIL;
        }
    }

    public UDViewPager reload() {
        final LVViewPager viewPager = getView();
        if (viewPager != null) {
            init();
            if (viewPager.getAdapter() != null) {
                viewPager.getAdapter().notifyDataSetChanged();
            }
        }
        return this;
    }

    /**
     * get number of pages
     *
     * @return
     */
    public int getPageCount() {
        return LuaUtil.getOrCallFunction(this.initParams.get("PageCount")).optint(1, 0);
    }

    /**
     * get page title
     *
     * @param position
     * @return
     */
    public String getPageTitle(int position) {
        if (!mPagesDelegate.isnil()) {
            return LuaUtil.callFunction(mPagesDelegate.get("Title"), LuaUtil.toLuaInt(position)).optjstring("");
        }
        return "";
    }

    /**
     * 有page改变的监听器
     *
     * @return
     */
    public boolean hasPageChangeListeners() {
        return !mCallback.isnil();
    }

    /**
     * 调用 Init 方法
     *
     * @param page
     * @param position
     * @return
     */
    public LuaValue callPageInit(LuaValue page, int position) {
        return callPageFunction("Init", page, position);//Lua从1开始
    }

    /**
     * 调用 Layout 方法
     *
     * @param page
     * @param position
     * @return
     */
    public LuaValue callPageLayout(LuaValue page, int position) {
        return callPageFunction("Layout", page, position);
    }


    /**
     * 调用 Page的某些方法
     *
     * @param method
     * @param page
     * @param position
     * @return
     */
    private LuaValue callPageFunction(String method, LuaValue page, int position) {
        if (!mPagesDelegate.isnil()) {
            return LuaUtil.callFunction(mPagesDelegate.get(method), page, LuaUtil.toLuaInt(position));
        }
        return NIL;
    }

    /**
     * 调用PageChange对应的方法
     *
     * @param position 从1开始
     * @param distance
     * @return
     */
    public UDViewPager callPageCallbackScrolling(int position, float percent, float distance) {
        if (!mCallback.isnil()) {
            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "Scrolling", "scrolling"), LuaUtil.toLuaInt(position), valueOf(percent), valueOf(distance));
        }
        return this;
    }

    /**
     * 调用PageChange对应的方法
     *
     * @return
     */
    public UDViewPager callPageCallbackScrollBegin(int currentItem) {
        if (!mCallback.isnil()) {
            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "ScrollBegin", "scrollBegin"), LuaUtil.toLuaInt(currentItem));
        }
        return this;
    }

    /**
     * 调用PageChange对应的方法
     *
     * @param position 从1开始
     * @return
     */
    public UDViewPager callPageCallbackScrollEnd(Integer position) {
        if (!mCallback.isnil()) {
            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "ScrollEnd", "scrollEnd"), LuaUtil.toLuaInt(position));
        }
        return this;
    }

    /**
     * 调用StateChanged对应的方法
     *
     * @param stateChanged 从1开始
     * @return
     */
    public UDViewPager callPageCallbackStateChanged(int stateChanged) {
        if (!mCallback.isnil()) {
            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "StateChanged", "stateChanged"), LuaUtil.toLuaInt(stateChanged));
        }
        return this;
    }

    /**
     * 设置当前页面
     *
     * @param item
     * @return
     */
    public UDViewPager setCurrentItem(final int item, final boolean smoothScroll) {
        if (item >= 0 && getView() != null) {
            getView().setCurrentItem(item, smoothScroll);
        }
        return this;
    }

    /**
     * 当前 item
     *
     * @return
     */
    public int getCurrentItem() {
        return getView() != null ? getView().getCurrentItem() : 0;
    }

    /**
     * 设置view pager indicator
     *
     * @param indicator
     * @return
     */
    public UDViewPager setViewPagerIndicator(LuaValue indicator) {
        final LVViewPager viewPager = getView();
        if (viewPager != null) {
            mViewPagerIndicator = indicator;
            viewPager.setViewPagerIndicator(indicator);
        }
        return this;
    }

    /**
     * 得到indicator
     *
     * @return
     */
    public LuaValue getViewPagerIndicator() {
        return mViewPagerIndicator;
    }
}
