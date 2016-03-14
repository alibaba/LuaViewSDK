package com.taobao.luaview.view.indicator.circle;

/**
 * Pager Icon
 * @author song
 */
public interface CycleIconPagerAdapter {

    // From PagerAdapter
    int getCount();

    // actual count
    public abstract int getActualCount();

    // instance count, for cycle scroll, if actualCount <=3, instanceCount == actualCount * 2
    public abstract int getInstanceCount();

}
