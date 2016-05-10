//package com.taobao.luaview.view.viewpager;
//
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.PagerAdapter;
//import android.util.SparseArray;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * A PagerAdapter wrapper responsible for providing a proper page to LoopViewPager
// * <p/>
// * This class shouldn't be used directly
// */
//public class LoopPagerAdapterWrapper extends PagerAdapter {
//
//    private boolean isLooping = false;
//
//    private PagerAdapter mAdapter;
//
//    private SparseArray<ToDestroy> mToDestroy = new SparseArray<ToDestroy>();
//
//    private boolean mBoundaryCaching = false;
//
//    public LoopPagerAdapterWrapper(PagerAdapter adapter) {
//        this.mAdapter = adapter;
//    }
//
//    @Override
//    public void notifyDataSetChanged() {
//        mToDestroy = new SparseArray<ToDestroy>();
//        super.notifyDataSetChanged();
//    }
//
//    public int toRealPosition(int position) {
//        if (isLooping) {
//            int realCount = getRealCount();
//            if (realCount <= 0) {
//                return 0;
//            } else if (realCount <= 1) {
//                return position;
//            }
//            int realPosition = (position - 1) % realCount;
//            if (realPosition < 0) {
//                realPosition += realCount;
//            }
//            return realPosition;
//        } else {
//            return position;
//        }
//    }
//
//    public int toFakePosition(int realPosition) {
//        if (isLooping) {
//            if (getRealCount() > 1) {
//                return realPosition + 1;
//            } else {
//                return 0;
//            }
//        } else {
//            return realPosition;
//        }
//    }
//
//    private int getRealFirstPosition() {
//        if (isLooping) {
//            return getRealCount() > 1 ? 1 : 0;
//        } else {
//            return 0;
//        }
//    }
//
//    private int getRealLastPosition() {
//        if (isLooping) {
//            return getRealFirstPosition() + getRealCount() - 1;
//        } else {
//            return getRealCount() - 1;
//        }
//    }
//
//    @Override
//    public int getCount() {
//        if (isLooping) {
//            return mAdapter.getCount() <= 1 ? mAdapter.getCount() : mAdapter.getCount() + 2;
//        } else {
//            return mAdapter.getCount();
//        }
//    }
//
//    public int getRealCount() {
//        return mAdapter.getCount();
//    }
//
//    public PagerAdapter getRealAdapter() {
//        return mAdapter;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        if (isLooping) {
//            final int realPosition = (mAdapter instanceof FragmentPagerAdapter || mAdapter instanceof FragmentStatePagerAdapter) ? position : toRealPosition(position);
//            if (mBoundaryCaching) {
//                ToDestroy toDestroy = mToDestroy.get(position);
//                if (toDestroy != null) {
//                    mToDestroy.remove(position);
//                    return toDestroy.object;
//                }
//            }
//            return mAdapter.instantiateItem(container, realPosition);
//        } else {
//            return mAdapter.instantiateItem(container, position);
//        }
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        if (isLooping) {
//            final int realPosition = (mAdapter instanceof FragmentPagerAdapter || mAdapter instanceof FragmentStatePagerAdapter) ? position : toRealPosition(position);
//            if (mBoundaryCaching && (realPosition == 0 || realPosition == getRealCount() - 1)) {//第一个或者最后一个
//                mToDestroy.put(position, new ToDestroy(container, realPosition, object));
//            } else {
//                mAdapter.destroyItem(container, realPosition, object);
//            }
//        } else {
//            mAdapter.destroyItem(container, position, object);
//        }
//    }
//
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return mAdapter.isViewFromObject(view, object);
//    }
//
//    public void setLooping(boolean looping) {
//        this.isLooping = looping;
//    }
//
//    public boolean isLooping() {
//        return isLooping;
//    }
//
//    /**
//     * Container class for caching the boundary views
//     */
//    static class ToDestroy {
//        ViewGroup container;
//        int realPosition;
//        Object object;
//
//        public ToDestroy(ViewGroup container, int position, Object object) {
//            this.container = container;
//            this.realPosition = position;
//            this.object = object;
//        }
//    }
//
//}
