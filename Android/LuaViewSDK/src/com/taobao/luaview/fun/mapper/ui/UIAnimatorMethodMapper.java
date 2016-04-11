package com.taobao.luaview.fun.mapper.ui;

import android.animation.ValueAnimator;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.constants.UDInterpolator;
import com.taobao.luaview.userdata.ui.UDAnimator;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.ParamUtil;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * animator 接口封装
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib
public class UIAnimatorMethodMapper<U extends UDAnimator> extends BaseMethodMapper<U> {

    public LuaValue with(U udAnimator, Varargs varargs) {
        final UDView udView = (varargs.narg() > 1 && varargs.arg(2) instanceof UDView) ? (UDView) varargs.arg(2) : null;
        return udAnimator.with(udView);
    }

    public LuaValue start(U udAnimator, Varargs varargs) {
        return udAnimator.start();
    }

    //------------------------------------------各种属性----------------------------------------------

    public LuaValue alpha(U udAnimator, Varargs varargs) {
        return udAnimator.ofProperty("alpha", ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue rotation(U udAnimator, Varargs varargs) {
        return udAnimator.ofProperty("rotation", ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue scale(U udAnimator, Varargs varargs) {
        udAnimator.ofProperty("scaleX", LuaUtil.getFloat(varargs, 2));
        return udAnimator.ofProperty("scaleY", LuaUtil.getFloat(varargs, 3, 2));
    }

    public LuaValue scaleX(U udAnimator, Varargs varargs) {
        return udAnimator.ofProperty("scaleX", ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue scaleY(U udAnimator, Varargs varargs) {
        return udAnimator.ofProperty("scaleY", ParamUtil.getFloatValues(varargs, 2));
    }

    public LuaValue translation(U udAnimator, Varargs varargs) {
        final Float translationX = LuaUtil.getFloat(varargs, 2);
        final Float translationY = LuaUtil.getFloat(varargs, 3);
        if (translationY != null) {
            udAnimator.ofProperty("translationX", DimenUtil.dpiToPxF(translationX));
            return udAnimator.ofProperty("translationY", DimenUtil.dpiToPxF(translationY));
        } else {
            return udAnimator.ofProperty("translationX", DimenUtil.dpiToPxF(translationX));
        }
    }

    public LuaValue translationX(U udAnimator, Varargs varargs) {
        return udAnimator.ofProperty("translationX", DimenUtil.dpiToPxF(ParamUtil.getFloatValues(varargs, 2)));
    }

    public LuaValue translationY(U udAnimator, Varargs varargs) {
        return udAnimator.ofProperty("translationY", DimenUtil.dpiToPxF(ParamUtil.getFloatValues(varargs, 2)));
    }

    public LuaValue duration(U udAnimator, Varargs varargs) {
        final long duration = (long) (varargs.optdouble(2, 0.3f) * 1000);
        return udAnimator.setDuration(duration);
    }

    public LuaValue delay(U udAnimator, Varargs varargs) {
        final long delay = (long) (varargs.optdouble(2, 0) * 1000);
        return udAnimator.setStartDelay(delay);
    }

    public LuaValue repeatCount(U udAnimator, Varargs varargs) {
        final int repeatCount = varargs.optint(2, 0);
        return udAnimator.setRepeatCount(repeatCount);
    }

    /**
     * 插值器，可以控制不同值
     *
     * @param udAnimator
     * @param varargs
     * @return
     */
    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue interpolator(U udAnimator, Varargs varargs) {
        final Integer type = LuaUtil.getInt(varargs, 2);
        final Float cycles = LuaUtil.getFloat(varargs, 3, 2);
        return udAnimator.setInterpolator(UDInterpolator.parse(type, cycles));
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue cancel(U udAnimator, Varargs varargs) {
        return udAnimator.cancel();
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue pause(U udAnimator, Varargs varargs) {
        return udAnimator.pause();
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue isPaused(U udAnimator, Varargs varargs) {
        return valueOf(udAnimator.isPaused());
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue isRunning(U udAnimator, Varargs varargs) {
        return valueOf(udAnimator.isRunning());
    }

    @LuaViewApi(since = VmVersion.V_500)
    public LuaValue resume(U udAnimator, Varargs varargs) {
        return udAnimator.resume();
    }

    /**
     * 设置动画repeat mode 类型是否reverse
     *
     * @param udAnimation
     * @param varargs
     * @return
     */
    public LuaValue reverses(U udAnimation, Varargs varargs) {
        final boolean reverse = varargs.optboolean(2, true);
        return udAnimation.setRepeatMode(reverse ? ValueAnimator.REVERSE : ValueAnimator.RESTART);
    }

    public LuaValue values(U udAnimator, Varargs varargs) {
        return udAnimator.setFloatValues(ParamUtil.getFloatValues(varargs, 2));
    }

    //--------------------------------------------回调----------------------------------------------

    public LuaValue callback(U udAnimator, Varargs varargs) {
        final LuaTable callback = varargs.opttable(2, null);
        return udAnimator.setCallback(callback);
    }


    public LuaValue onStart(U udAnimator, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return udAnimator.setOnStartCallback(callback);
    }

    public LuaValue onEnd(U udAnimator, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return udAnimator.setOnEndCallback(callback);
    }

    public LuaValue onRepeat(U udAnimator, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return udAnimator.setOnRepeatCallback(callback);
    }

    public LuaValue onCancel(U udAnimator, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return udAnimator.setOnCancelCallback(callback);
    }

    public LuaValue onPause(U udAnimator, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return udAnimator.setOnPauseCallback(callback);
    }

    public LuaValue onUpdate(U udAnimator, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return udAnimator.setOnUpdateCallback(callback);
    }

    public LuaValue onResume(U udAnimator, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return udAnimator.setOnResumeCallback(callback);
    }

}
