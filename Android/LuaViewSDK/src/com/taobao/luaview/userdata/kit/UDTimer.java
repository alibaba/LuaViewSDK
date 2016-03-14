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
     * start a timer
     *
     * @param interval
     * @param repeat
     */
    public UDTimer start(final long interval, final boolean repeat) {
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                LuaViewUtil.runOnUiThread(getContext(), new Runnable() {
                    @Override
                    public void run() {
                        LuaUtil.callFunction(mCallback);
                    }
                });
                if (repeat && mTimerHandler != null) {
                    mTimerHandler.postDelayed(this, interval);
                }
            }
        };
        this.mTimerHandler.postDelayed(mTimerRunnable, interval);
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
