/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.view.adapter.LVLoopPagerAdapter;

/**
 * A ViewPager subclass enabling infinte scrolling of the viewPager elements
 * <p/>
 * When used for paginating views (in opposite to fragments), no code changes
 * should be needed only change xml's from <android.support.v4.view.ViewPager>
 * to <com.imbryk.viewPager.LoopViewPager>
 * <p/>
 * If "blinking" can be seen when paginating to first or last view, simply call
 * seBoundaryCaching( true ), or change DEFAULT_BOUNDARY_CASHING to true
 * <p/>
 * When using a FragmentPagerAdapter or FragmentStatePagerAdapter,
 * additional changes in the adapter must be done.
 * The adapter must be prepared to create 2 extra items e.g.:
 * <p/>
 * The original adapter creates 4 items: [0,1,2,3]
 * The modified adapter will have to create 6 items [0,1,2,3,4,5]
 * with mapping realPosition=(position-1)%count
 * [0->3, 1->0, 2->1, 3->2, 4->3, 5->0]
 */
public class LoopViewPager extends ViewPager {
    private OnPageChangeListener mOuterPageChangeListener;
    private LVLoopPagerAdapter mAdapter;

    private float mPreviousOffset = -1;
    private float mPreviousRealPosition = -1;

    public LoopViewPager(Context context) {
        super(context);
        init();
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mAdapter = (LVLoopPagerAdapter) adapter;
        super.setAdapter(mAdapter);
        setCurrentItem(0, false);
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    public int getRealCount() {
        return mAdapter != null ? mAdapter.getRealCount() : 0;
    }

    public int getCount() {
        return mAdapter != null ? mAdapter.getCount() : 0;
    }

    public void setLooping(boolean looping) {
        if (mAdapter != null && mAdapter.isLooping() != looping) {
            final int currentPosition = super.getCurrentItem();
            super.setAdapter(null);
            mAdapter.setLooping(looping);
            super.setAdapter(mAdapter);
            if (looping) {
                super.setCurrentItem(mAdapter.toFakePosition(currentPosition), false);
            }
            mPreviousRealPosition = -1;
            mPreviousOffset = -1;
        }
    }

    public boolean isLooping() {
        return mAdapter != null && mAdapter.isLooping();
    }

    @Override
    public int getCurrentItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : super.getCurrentItem();
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    public void setCurrentItem(int realItem, boolean smoothScroll) {
        final int fakeItem = mAdapter.toFakePosition(realItem);
        final int currentFakeItem = super.getCurrentItem();
//        LogUtil.d("yesong", "setCurrentItem", fakeItem, currentFakeItem);
        if (fakeItem != currentFakeItem) {
            super.setCurrentItem(fakeItem, smoothScroll);
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOuterPageChangeListener = listener;
    }

    private boolean isBoundaryPosition(int position) {
        return position == 0 || (position == getCount() - 1);
    }

    private void init() {
        super.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                LogUtil.d("yesong", "onPageSelected", position);
                if (mAdapter != null && mAdapter.shouldLooping()) {
                    int fakePosition = position;
                    int realPosition = mAdapter.toRealPosition(fakePosition);
                    if (mPreviousRealPosition != realPosition) {
                        mPreviousRealPosition = realPosition;
                        if (mOuterPageChangeListener != null) {
                            mOuterPageChangeListener.onPageSelected(realPosition);
                        }
                    }
                } else {
                    mPreviousRealPosition = position;
                    if (mOuterPageChangeListener != null) {
                        mOuterPageChangeListener.onPageSelected(position);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                LogUtil.d("yesong", "onPageScrolled－offset", position, positionOffset, positionOffsetPixels);

                if (mAdapter != null && mAdapter.shouldLooping()) {
                    final int fakePosition = position;
                    final int realPosition = mAdapter.toRealPosition(fakePosition);
                    if (positionOffset == 0 && mPreviousOffset == 0 && isBoundaryPosition(fakePosition)) {
//                        LogUtil.d("yesong", "onPageScrolled", fakePosition, realPosition);
                        setCurrentItem(realPosition, false);
                    }
                    mPreviousOffset = positionOffset;
                    if (mOuterPageChangeListener != null) {
                        if (realPosition == mAdapter.getRealCount() - 1) {
                            if (positionOffset > 0.5) {
                                mOuterPageChangeListener.onPageScrolled(0, 0, 0);
                            } else {
                                mOuterPageChangeListener.onPageScrolled(realPosition, 0, 0);
                            }
                        } else {
                            mOuterPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                        }
                    }
                } else {
                    if (mOuterPageChangeListener != null) {
                        mOuterPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                LogUtil.d("yesong", "onPageScrollStateChanged", state);
                if (mAdapter != null && mAdapter.shouldLooping()) {
                    int fakePosition = LoopViewPager.super.getCurrentItem();
                    int realPosition = mAdapter.toRealPosition(fakePosition);

                    if (state == ViewPager.SCROLL_STATE_IDLE && isBoundaryPosition(fakePosition)) {
//                        LogUtil.d("yesong", "onPageScrollStateChanged", fakePosition, realPosition);
                        setCurrentItem(realPosition, false);
                    }
                }
                if (mOuterPageChangeListener != null) {
                    mOuterPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

}