package com.taobao.luaview.userdata.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.text.TextUtils;
import android.view.animation.Interpolator;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Animator 数据封装
 * @author song
 */
public class UDAnimator extends BaseUserdata {
    private LuaValue mOnStartCallback;
    private LuaValue mOnEndCallback;
    private LuaValue mOnCancelCallback;
    private LuaValue mOnRepeatCallback;
    private LuaValue mOnPauseCallback;
    private LuaValue mOnResumeCallback;
    private LuaValue mOnUpdateCallback;

    public UDAnimator(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(new ObjectAnimator(), globals, metaTable, varargs);
        init();
    }

    private ObjectAnimator getAnimator() {
        return (ObjectAnimator) userdata();
    }

    private void init() {
        final ObjectAnimator animator = getAnimator();
        if (animator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                animator.setAutoCancel(true);
            }
        }
    }

    /**
     * 设置target
     *
     * @param udView
     * @return
     */
    public UDAnimator with(UDView udView) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null && udView != null && udView.getView() != null) {
            animator.setTarget(udView.getView());
        }

        return this;
    }

    public UDAnimator setCallback(LuaTable callbacks) {
        if (callbacks != null) {
            mOnStartCallback = LuaUtil.getFunction(callbacks, "onStart", "OnStart");
            mOnEndCallback = LuaUtil.getFunction(callbacks, "onEnd", "OnEnd");
            mOnCancelCallback = LuaUtil.getFunction(callbacks, "onCancel", "OnCancel");
            mOnPauseCallback = LuaUtil.getFunction(callbacks, "onPause", "OnPause");
            mOnResumeCallback = LuaUtil.getFunction(callbacks, "onResume", "OnResume");
            mOnRepeatCallback = LuaUtil.getFunction(callbacks, "onRepeat", "OnRepeat");
            mOnUpdateCallback = LuaUtil.getFunction(callbacks, "onUpdate", "OnUpdate");
        }
        return this;
    }

    public UDAnimator setOnStartCallback(LuaValue mOnStartCallback) {
        this.mOnStartCallback = mOnStartCallback;
        return this;
    }

    public UDAnimator setOnEndCallback(LuaValue mOnEndCallback) {
        this.mOnEndCallback = mOnEndCallback;
        return this;
    }

    public UDAnimator setOnCancelCallback(LuaValue mOnCancelCallback) {
        this.mOnCancelCallback = mOnCancelCallback;
        return this;
    }

    public UDAnimator setOnRepeatCallback(LuaValue mOnRepeatCallback) {
        this.mOnRepeatCallback = mOnRepeatCallback;
        return this;
    }

    public UDAnimator setOnPauseCallback(LuaValue mOnPauseCallback) {
        this.mOnPauseCallback = mOnPauseCallback;
        return this;
    }

    public UDAnimator setOnResumeCallback(LuaValue mOnResumeCallback) {
        this.mOnResumeCallback = mOnResumeCallback;
        return this;
    }

    public UDAnimator setOnUpdateCallback(LuaValue mOnUpdateCallback) {
        this.mOnUpdateCallback = mOnUpdateCallback;
        return this;
    }


    /**
     * 设置属性
     *
     * @param name
     * @return
     */
    public UDAnimator ofProperty(final String name, float... values) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null && TextUtils.isEmpty(name) == false) {
            animator.setPropertyName(name);
            if (values != null && values.length > 0) {
                animator.setFloatValues(values);
            }
        }
        return this;
    }


    /**
     * 时长
     *
     * @param duration
     * @return
     */
    public UDAnimator setDuration(long duration) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null && duration >= 0) {
            animator.setDuration(duration);
        }
        return this;
    }

    /**
     * 启动延时
     *
     * @param delay
     * @return
     */
    public UDAnimator setStartDelay(long delay) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null && delay >= 0) {
            animator.setStartDelay(delay);
        }
        return this;
    }

    /**
     * 重复次数，负数标示无限
     *
     * @param repeatCount
     * @return
     */
    public UDAnimator setRepeatCount(int repeatCount) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null) {
            if (repeatCount >= 0) {
                animator.setRepeatCount(repeatCount);
            } else {
                animator.setRepeatMode(ValueAnimator.INFINITE);
            }
        }
        return this;
    }

    /**
     * 重复方式(1 default, -1 infinite, 2 reverse)
     *
     * @param repeatMode
     * @return
     */
    public UDAnimator setRepeatMode(int repeatMode) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null) {
            animator.setRepeatMode(repeatMode);
        }
        return this;
    }

    /**
     * 浮点数
     *
     * @param values
     * @return
     */
    public UDAnimator setFloatValues(float... values) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null && values != null && values.length > 0) {
            animator.setFloatValues(values);
        }
        return this;
    }

    /**
     * 整数
     *
     * @param values
     * @return
     */
    public UDAnimator setIntValues(int... values) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null && values != null && values.length > 0) {
            animator.setIntValues(values);
        }
        return this;
    }

    /**
     * 加速器
     * AccelerateDecelerateInterpolator,
     * AccelerateInterpolator,
     * AnticipateInterpolator,
     * AnticipateOvershootInterpolator,
     * BaseInterpolator,
     * BounceInterpolator,
     * CycleInterpolator,
     * DecelerateInterpolator,
     * FastOutLinearInInterpolator,
     * FastOutSlowInInterpolator,
     * LinearInterpolator,
     * LinearOutSlowInInterpolator,
     * OvershootInterpolator,
     * PathInterpolator
     *
     * @param interpolator
     * @return
     */
    public UDAnimator setInteplator(final Interpolator interpolator) {
        final ObjectAnimator animator = getAnimator();
        if (animator != null && interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        return this;
    }

    /**
     * start all animator
     *
     * @return
     */
    public UDAnimator start() {
        final ObjectAnimator animator = getAnimator();
        if (animator != null && animator.getTarget() != null && animator.isStarted() == false) {
            setupListeners(animator);
            animator.start();
        }
        return this;
    }

    public UDAnimator setupListeners(ObjectAnimator animator) {
        if (animator != null) {
            addAnimatorListener(animator);
            addOnPauseListener(animator);
            addOnUpdateListener(animator);
        }
        return this;
    }

    /**
     * build a copy of given animator
     *
     * @return
     */
    public Animator build() {
        UDAnimator animator = setupListeners(getAnimator());
        return animator.getAnimator().clone();//克隆一份
    }
    //----------------------------------------listeners---------------------------------------------

    private void addAnimatorListener(ObjectAnimator animator) {
        if (animator != null && (mOnStartCallback != null || mOnEndCallback != null || mOnCancelCallback != null || mOnRepeatCallback != null)) {
            animator.addListener(new Animator.AnimatorListener() {//add a listener
                @Override
                public void onAnimationStart(Animator animation) {
                    LuaUtil.callFunction(mOnStartCallback);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    LuaUtil.callFunction(mOnEndCallback);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    LuaUtil.callFunction(mOnCancelCallback);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    LuaUtil.callFunction(mOnRepeatCallback);
                }
            });
        }
    }

    private void addOnPauseListener(ObjectAnimator animator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (animator != null && (mOnPauseCallback != null || mOnResumeCallback != null)) {
                animator.addPauseListener(new Animator.AnimatorPauseListener() {
                    @Override
                    public void onAnimationPause(Animator animation) {
                        LuaUtil.callFunction(mOnPauseCallback);
                    }

                    @Override
                    public void onAnimationResume(Animator animation) {
                        LuaUtil.callFunction(mOnResumeCallback);
                    }
                });
            }
        }
    }

    private void addOnUpdateListener(ObjectAnimator animator) {
        if (animator != null && mOnUpdateCallback != null) {
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LuaUtil.callFunction(mOnUpdateCallback);
                }
            });
        }
    }
}
