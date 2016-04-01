package com.taobao.luaview.userdata.kit;

import android.os.Handler;

import com.taobao.luaview.userdata.base.BaseCacheUserdata;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Timer 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
public class UDTimer extends BaseCacheUserdata {
    private LuaFunction mCallback;

    private Handler mTimerHandler;

    private Runnable mTimerRunnable;

    //启动延时，默认为0
    private long mDelay = 0L;

    //是否重复，默认为false
    private boolean mRepeat = false;

    //间隔，重复间隔，默认为1秒
    private long mInterval = 1000L;

    public UDTimer(Globals globals, LuaValue metatable, Varargs varargs) {
        super(new Handler(), globals, metatable, varargs);
        init();
    }

    private void init() {
        this.mTimerHandler = (Handler) userdata();
        this.mCallback = mVarargs.optfunction(1, null);
    }

    @Override
    public void onCacheClear() {
        this.cancel();
    }

    /**
     * set delegate
     *
     * @param callback
     */
    public UDTimer setCallback(LuaFunction callback) {
        this.mCallback = callback;
        return this;
    }

    public LuaFunction getCallback() {
        return this.mCallback;
    }

    /**
     * 设置delay
     *
     * @param delay
     * @return
     */
    public UDTimer setDelay(long delay) {
        this.mDelay = delay;
        return this;
    }

    public long getDelay() {
        return mDelay;
    }

    /**
     * 设置repeat
     *
     * @param repeat
     * @return
     */
    public UDTimer setRepeat(boolean repeat) {
        this.mRepeat = repeat;
        return this;
    }

    public boolean isRepeat() {
        return mRepeat;
    }

    /**
     * 设置intervale
     *
     * @param interval
     * @return
     */
    public UDTimer setInterval(long interval) {
        if(interval >= 0) {
            this.mInterval = interval;
        }
        return this;
    }

    public long getInterval() {
        return mInterval;
    }



    /**
     * start a timer
     *
     * @param interval
     * @param repeat
     */
    public UDTimer start(final Long interval, final Boolean repeat) {
        if (interval != null) {
            mInterval = interval;
            mDelay = interval;
        }
        if (repeat != null) {
            mRepeat = repeat;
        }
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                LuaViewUtil.runOnUiThread(getContext(), new Runnable() {
                    @Override
                    public void run() {
                        LuaUtil.callFunction(mCallback);
                    }
                });
                if (mRepeat && mTimerHandler != null) {
                    mTimerHandler.postDelayed(this, mInterval);
                }
            }
        };
        this.mTimerHandler.postDelayed(mTimerRunnable, mDelay);
        return this;
    }

    /**
     * cancel a runnable
     */
    public UDTimer cancel() {
        if (this.mTimerHandler != null && this.mTimerRunnable != null) {
            this.mTimerHandler.removeCallbacks(this.mTimerRunnable);
            this.mTimerRunnable = null;
        }
        return this;
    }
}
