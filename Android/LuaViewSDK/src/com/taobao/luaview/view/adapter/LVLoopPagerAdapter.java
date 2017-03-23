/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import com.taobao.luaview.userdata.ui.UDViewPager;
import com.taobao.luaview.view.indicator.circle.CycleIconPagerAdapter;

import org.luaj.vm2.Globals;

/**
 * Pager Adapter for loop
 *
 * @author song
 * @date 15/9/17
 */
public class LVLoopPagerAdapter extends LVPagerAdapter implements CycleIconPagerAdapter {

    private boolean mIsLooping = false;
    private boolean mBoundaryCaching = false;
    private SparseArray<ToDestroy> mToDestroy = new SparseArray<ToDestroy>();

    public LVLoopPagerAdapter(Globals globals, UDViewPager udListView) {
        super(globals, udListView);
    }

    @Override
    public void notifyDataSetChanged() {
        mToDestroy = new SparseArray<ToDestroy>();
        super.notifyDataSetChanged();
    }

    /**
     * real:[0,1,2,3] 真实的数据
     * fake:[0,1,2,3,4,5] 虚拟的数据
     * fake<->real:
     * 0->3
     * 1->0
     * 2->1
     * 3->2
     * 4->3
     * 5->0
     *
     * @param position
     * @return
     */
    public int toRealPosition(int position) {
        if (mIsLooping) {
            int realCount = getRealCount();
            if (realCount <= 0) {
                return 0;
            } else if (realCount <= 1) {
                return position;
            }
            int realPosition = (position - 1) % realCount;
            if (realPosition < 0) {
                realPosition += realCount;
            }
            return realPosition;
        } else {
            return position;
        }
    }

    /**
     * real:[0,1,2,3] 真实的数据
     * fake:[0,1,2,3,4,5] 虚拟的数据
     * real<->fake:
     * 3->4
     * 0->1
     * 1->2
     * 2->3
     * 3->4
     * 0->1
     *
     * @param position
     * @return
     */
    public int toFakePosition(int position) {
        if (mIsLooping) {
            if (getRealCount() > 1) {
                return position + 1;
            } else {
                return 0;
            }
        } else {
            return position;
        }
    }

    @Override
    public int getCount() {
        if (mIsLooping) {
            return super.getCount() <= 1 ? super.getCount() : super.getCount() + 2;
        } else {
            return super.getCount();
        }
    }

    public boolean shouldLooping() {
        return mIsLooping && getRealCount() > 1;
    }

    public int getRealCount() {
        return super.getCount();
    }

    public int getRealFirstPosition() {
        if (mIsLooping) {
            return getRealCount() > 1 ? 1 : 0;
        } else {
            return 0;
        }
    }

    public int getRealLastPosition() {
        if (mIsLooping) {
            return getRealFirstPosition() + getRealCount() - 1;
        } else {
            return getRealCount() - 1;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mIsLooping) {
            final int realPosition = toRealPosition(position);
            if (mBoundaryCaching) {
                ToDestroy toDestroy = mToDestroy.get(position);
                if (toDestroy != null) {
                    mToDestroy.remove(position);
                    return toDestroy.object;
                }
            }
            return super.instantiateItem(container, realPosition);

            /*if (shouldLooping() && (position == 0 || position == getCount() - 1)) {
                return super.newItem(container, position);
            } else {
                final int realPosition = toRealPosition(position);
                return super.instantiateItem(container, realPosition);
            }*/

        } else {
            return super.instantiateItem(container, position);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mIsLooping) {
            final int realPosition = toRealPosition(position);
            if (mBoundaryCaching && (realPosition == 0 || realPosition == getRealCount() - 1)) {//第一个或者最后一个
                mToDestroy.put(position, new ToDestroy(container, realPosition, object));
            } else {
                super.destroyItem(container, realPosition, object);
            }

            /*if (shouldLooping() && (position == 0 || position == getCount() - 1)) {
                super.destroyItem(container, position, object);
            } else {
                final int realPosition = toRealPosition(position);
                super.destroyItem(container, realPosition, object);
            }*/
        } else {
            super.destroyItem(container, position, object);
        }
    }

    public void setLooping(boolean mIsLooping) {
        this.mIsLooping = mIsLooping;
    }

    public boolean isLooping() {
        return mIsLooping;
    }


    /**
     * Container class for caching the boundary views
     */
    static class ToDestroy {
        ViewGroup container;
        int realPosition;
        Object object;

        public ToDestroy(ViewGroup container, int position, Object object) {
            this.container = container;
            this.realPosition = position;
            this.object = object;
        }
    }

    /**
     * bugfix: mIsLooping条件为真的时候,实际要绘制的indicator个数应该扣除掉两个虚拟的
     * see: {@link com.taobao.luaview.view.viewpager.LoopViewPager}
     */
    @Override
    public int getActualCount() {
//        if (mIsLooping)
//            return getCount()-2;
//        else
            return getCount();
    }

    @Override
    public int getInstanceCount() {
        if (mIsLooping)
            return getCount()-2;
        else
            return getCount();
    }
}
