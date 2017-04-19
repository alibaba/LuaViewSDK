/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.viewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

public class AutoScrollViewPager extends LoopViewPager {

    public static final int DEFAULT_INTERVAL = 3000;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    /**
     * do nothing when sliding at the last or first item
     **/
    public static final int SLIDE_BORDER_MODE_NONE = 0;
    /**
     * cycle when sliding at the last or first item
     **/
    public static final int SLIDE_BORDER_MODE_CYCLE = 1;
    /**
     * deliver event to parent when sliding at the last or first item
     **/
    public static final int SLIDE_BORDER_MODE_TO_PARENT = 2;

    /**
     * auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     **/
    private long interval = DEFAULT_INTERVAL;
    /**
     * auto scroll direction, default is {@link #RIGHT}
     **/
    private int direction = RIGHT;
    /**
     * whether stop auto scroll when touching, default is true
     **/
    private boolean stopScrollWhenTouch = true;
    /**
     * how to process when sliding at the last or first item, default is {@link #SLIDE_BORDER_MODE_NONE}
     **/
    private int slideBorderMode = SLIDE_BORDER_MODE_NONE;

    private Handler handler;
    private boolean reverseDirection = false;
    private boolean isAutoScroll = false;
    private boolean isStopByUIChange = false;
    private boolean isStopByTouch = false;
    private float touchX = 0f, downX = 0f;

    private boolean canAutoScroll = false;

    public static final int SCROLL_WHAT = 0;

    public AutoScrollViewPager(Context context) {
        super(context);
        init();
    }

    private void init() {
        handler = new MyHandler(this);
    }

    /**
     * 设置是否可以自动滚动，所有自动滚动的逻辑都受该参数影响
     *
     * @param canAutoScroll
     */
    public void setCanAutoScroll(boolean canAutoScroll) {
        this.canAutoScroll = canAutoScroll;
    }

    /**
     * start auto scroll, first scroll delay time is {@link #getInterval()}
     */
    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage(interval);
    }

    /**
     * start auto scroll
     *
     * @param delayTimeInMills first scroll delay time
     */
    public void startAutoScroll(int delayTimeInMills) {
        isAutoScroll = true;
        sendScrollMessage(delayTimeInMills);
    }

    /**
     * stop auto scroll
     */
    public void stopAutoScroll() {
        isAutoScroll = false;
        handler.removeMessages(SCROLL_WHAT);
    }


    private void sendScrollMessage(long delayTimeInMills) {
        /** remove messages before, keeps one message is running at most **/
        handler.removeMessages(SCROLL_WHAT);
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }

    /**
     * scroll only once
     */
    public void scrollOnce() {
        PagerAdapter adapter = getAdapter();
        int realPosition = getCurrentItem();
        int realCount;
        if (adapter == null || (realCount = getRealCount()) <= 1) {
            return;
        }

        //调整方向
        if (reverseDirection) {
            if (direction == RIGHT && realPosition + 1 >= realCount) {
                direction = LEFT;
            } else if (direction == LEFT && realPosition - 1 < 0) {
                direction = RIGHT;
            }
        }

        if (isLooping()) {
            setCurrentItem(direction == LEFT ? (realPosition - 1) % getCount() : (realPosition + 1) % getCount(), true);
        } else {
            int nextItem = (direction == LEFT) ? --realPosition : ++realPosition;
            if (nextItem < 0) {
                setCurrentItem(realCount - 1, true);
            } else if (nextItem == realCount) {
                setCurrentItem(0, true);
            } else {
                setCurrentItem(nextItem, true);
            }
        }
    }

    /**
     * <ul>
     * if stopScrollWhenTouch is true
     * <li>if event is down, stop auto scroll.</li>
     * <li>if event is up, start auto scroll again.</li>
     * </ul>
     *
     * bugfix: 增加ev.getAction() == MotionEvent.ACTION_CANCEL条件判断,Action Cancel事件发生的时候也要重新开始自动滚动
     *
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!canAutoScroll) {
            return super.dispatchTouchEvent(ev);
        }
        int action = MotionEventCompat.getActionMasked(ev);

        if (stopScrollWhenTouch) {
            if ((action == MotionEvent.ACTION_DOWN) && isAutoScroll) {
                isStopByTouch = true;
                stopAutoScroll();
            } else if ((ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) && isStopByTouch) {
                startAutoScroll();
            }
        }

//        if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
//            touchX = ev.getX();
//            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//                downX = touchX;
//            }
//            int currentItem = getCurrentItem();
//            PagerAdapter adapter = getAdapter();
//            int pageCount = adapter == null ? 0 : adapter.getCount();
//            /**
//             * current index is first one and slide to right or current index is last one and slide to left.<br/>
//             * if slide border mode is to parent, then requestDisallowInterceptTouchEvent false.<br/>
//             * else scroll to last one when current item is first one, scroll to first one when current item is last
//             * one.
//             */
//            if ((currentItem == 0 && downX <= touchX) || (currentItem == pageCount - 1 && downX >= touchX)) {
//                if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                } else {
//                    if (pageCount > 1) {
//                        setCurrentItem(pageCount - currentItem - 1, false);
//                    }
////                    getParent().requestDisallowInterceptTouchEvent(true);
//                }
//                return super.dispatchTouchEvent(ev);
//            }
//        }
////        getParent().requestDisallowInterceptTouchEvent(true);

        return super.dispatchTouchEvent(ev);
    }

    private static class MyHandler extends Handler {

        private final WeakReference<AutoScrollViewPager> autoScrollViewPager;

        public MyHandler(AutoScrollViewPager autoScrollViewPager) {
            this.autoScrollViewPager = new WeakReference<AutoScrollViewPager>(autoScrollViewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_WHAT:
                    AutoScrollViewPager pager = this.autoScrollViewPager.get();
                    if (pager != null) {
                        pager.scrollOnce();
                        pager.sendScrollMessage(pager.interval);
                    }
                default:
                    break;
            }
        }
    }

    /**
     * get auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     *
     * @return the interval
     */
    public long getInterval() {
        return interval;
    }

    /**
     * set auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     *
     * @param interval the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * get auto scroll direction
     *
     * @return {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
     */
    public int getDirection() {
        return (direction == LEFT) ? LEFT : RIGHT;
    }

    /**
     * set auto scroll direction
     *
     * @param direction {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * whether stop auto scroll when touching, default is true
     *
     * @return the stopScrollWhenTouch
     */
    public boolean isStopScrollWhenTouch() {
        return stopScrollWhenTouch;
    }

    /**
     * set whether stop auto scroll when touching, default is true
     *
     * @param stopScrollWhenTouch
     */
    public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
        this.stopScrollWhenTouch = stopScrollWhenTouch;
    }

    /**
     * get how to process when sliding at the last or first item
     *
     * @return the slideBorderMode {@link #SLIDE_BORDER_MODE_NONE}, {@link #SLIDE_BORDER_MODE_TO_PARENT},
     * {@link #SLIDE_BORDER_MODE_CYCLE}, default is {@link #SLIDE_BORDER_MODE_NONE}
     */
    public int getSlideBorderMode() {
        return slideBorderMode;
    }

    /**
     * set how to process when sliding at the last or first item
     *
     * @param slideBorderMode {@link #SLIDE_BORDER_MODE_NONE}, {@link #SLIDE_BORDER_MODE_TO_PARENT},
     *                        {@link #SLIDE_BORDER_MODE_CYCLE}, default is {@link #SLIDE_BORDER_MODE_NONE}
     */
    public void setSlideBorderMode(int slideBorderMode) {
        this.slideBorderMode = slideBorderMode;
    }

    public void setReverseDirection(boolean reverseDirection) {
        this.reverseDirection = reverseDirection;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (canAutoScroll && visibility == View.VISIBLE && isStopByUIChange) {
            isStopByUIChange = false;
            startAutoScroll();
        }
        super.onVisibilityChanged(changedView, visibility);
        if (canAutoScroll && visibility != View.VISIBLE) {
            isStopByUIChange = true;
            stopAutoScroll();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!canAutoScroll) {
            return;
        }
        isStopByUIChange = true;
        stopAutoScroll();
    }
}