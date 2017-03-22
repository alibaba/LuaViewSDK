/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Build;
import android.text.TextUtils;
import android.view.animation.Interpolator;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.AnimatorUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Arrays;
import java.util.List;

/**
 * Animator 数据封装
 *
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

    private Boolean isRunning;

    private UDView mTarget;


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
            mTarget = udView;
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
        if (animator != null && TextUtils.isEmpty(name) == false && values != null) {
            PropertyValuesHolder[] valuesHolders = null;
            if (animator.getValues() != null && animator.getValues().length > 0) {
                valuesHolders = Arrays.copyOf(animator.getValues(), animator.getValues().length + 1);
            } else {
                valuesHolders = new PropertyValuesHolder[1];
            }
            valuesHolders[valuesHolders.length - 1] = PropertyValuesHolder.ofFloat(name, values);
            animator.setValues(valuesHolders);
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
                animator.setRepeatCount(ValueAnimator.INFINITE);
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
    public UDAnimator setInterpolator(final Interpolator interpolator) {
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
        setupListeners(animator);
        AnimatorUtil.start(animator);
        if (mTarget != null) {
            mTarget.startAnimation();
        }
        return this;
    }

    /**
     * cancel animator
     *
     * @return
     */
    public UDAnimator cancel() {
        AnimatorUtil.cancel(getAnimator());
        if (mTarget != null) {
            mTarget.cancelAnimation();
        }
        return this;
    }

    /**
     * pause animator
     *
     * @return
     */
    public UDAnimator pause() {
        AnimatorUtil.pause(getAnimator());
        if (mTarget != null) {
            mTarget.pauseAnimation();
        }
        return this;
    }

    /**
     * resume animator
     *
     * @return
     */
    public UDAnimator resume() {
        AnimatorUtil.resume(getAnimator());
        if (mTarget != null) {
            mTarget.resumeAnimation();
        }
        return this;
    }

    /**
     * end animator
     *
     * @return
     */
    public UDAnimator end() {
        AnimatorUtil.end(getAnimator());
        if (mTarget != null) {
            mTarget.endAnimation();
        }
        return this;
    }

    /**
     * is paused
     *
     * @return
     */
    public boolean isPaused() {
        return AnimatorUtil.isPaused(getAnimator()) || (mTarget != null && mTarget.isAnimationPaused());
    }

    /**
     * is running
     * TODO 这里的判断有问题
     * @return
     */
    public boolean isRunning() {
        if (isRunning != null) {
            return isRunning;
        }
        return AnimatorUtil.isRunning(getAnimator()) || (mTarget != null && mTarget.isAnimating());
    }

    /**
     * setup listeners
     *
     * @param animator
     * @return
     */
    public UDAnimator setupListeners(ObjectAnimator animator) {
        if (animator != null) {
            animator.removeAllListeners();//删除所有listener, pause listener
            animator.removeAllUpdateListeners();//删除所有update listener
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
        //这种方式clone出来的animator不能重复播放
        /*final ObjectAnimator result = getAnimator().clone();//克隆一份
        setupListeners(result);
        result.setupStartValues();
        return result;*/

        final ObjectAnimator self = this.getAnimator();
        final ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(self.getTarget());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            anim.setAutoCancel(true);
        }
        if (self.getValues() != null) {
            anim.setValues(self.getValues());
        }
        anim.setInterpolator(self.getInterpolator());
        anim.setDuration(self.getDuration());
        anim.setStartDelay(self.getStartDelay());
        anim.setRepeatCount(self.getRepeatCount());
        anim.setRepeatMode(self.getRepeatMode());
        setupListeners(anim);
        return anim;
    }

    public List<PropertyValuesHolder> getPropertyValuesHolder() {
        return Arrays.asList(getAnimator().getValues());
    }
    //----------------------------------------listeners---------------------------------------------

    private void addAnimatorListener(ObjectAnimator animator) {
        if (animator != null && (mOnStartCallback != null || mOnEndCallback != null || mOnCancelCallback != null || mOnRepeatCallback != null)) {
            animator.addListener(new Animator.AnimatorListener() {//add a listener
                @Override
                public void onAnimationStart(Animator animation) {
                    isRunning = true;
                    LuaUtil.callFunction(mOnStartCallback);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isRunning = false;
                    LuaUtil.callFunction(mOnEndCallback);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isRunning = false;
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
