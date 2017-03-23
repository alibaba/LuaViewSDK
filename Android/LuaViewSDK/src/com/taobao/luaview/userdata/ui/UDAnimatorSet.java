/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.text.TextUtils;
import android.view.animation.Interpolator;

import com.taobao.luaview.extend.animation.AnimatorDecorationHelper;
import com.taobao.luaview.extend.animation.BaseViewAnimatorDecoration;
import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.List;

/**
 * Animator 数据封装
 *
 * @author song
 */
public class UDAnimatorSet extends BaseUserdata {
    private LuaValue mOnStartCallback;
    private LuaValue mOnEndCallback;
    private LuaValue mOnCancelCallback;
    private LuaValue mOnRepeatCallback;
    private LuaValue mOnPauseCallback;
    private LuaValue mOnResumeCallback;
    private LuaValue mOnUpdateCallback;

    private List<Animator> mAnimators;
    private UDView mTarget;
    private int mRepeatCount;
    private int mRepeatMode;
    private float[] mFloatValues;
    private int[] mIntValues;

    private BaseViewAnimatorDecoration mAnimatorDecoration;

    public UDAnimatorSet(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(new AnimatorSet(), globals, metaTable, varargs);
        init(varargs);
    }

    private AnimatorSet getAnimatorSet() {
        return (AnimatorSet) userdata();
    }

    private void init(Varargs varargs) {
        mAnimators = new ArrayList<Animator>();
        String animTypeName = LuaUtil.getString(varargs, 1);
        this.mAnimatorDecoration = AnimatorDecorationHelper.createDecoration(animTypeName);
    }

    private ObjectAnimator createAnimator() {
        final ObjectAnimator animator = new ObjectAnimator();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            animator.setAutoCancel(true);
        }
        return animator;
    }

    /**
     * 设置target
     *
     * @param udView
     * @return
     */
    public UDAnimatorSet with(UDView udView) {
        final AnimatorSet animator = getAnimatorSet();
        if (animator != null && udView != null && udView.getView() != null) {
            mTarget = udView;
            if (mAnimatorDecoration != null) {//decoration
                mAnimatorDecoration.setTarget(animator, udView.getView());
            }
        }
        return this;
    }

    public UDAnimatorSet setCallback(LuaTable callbacks) {
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

    public UDAnimatorSet setOnStartCallback(LuaValue mOnStartCallback) {
        this.mOnStartCallback = mOnStartCallback;
        return this;
    }

    public UDAnimatorSet setOnEndCallback(LuaValue mOnEndCallback) {
        this.mOnEndCallback = mOnEndCallback;
        return this;
    }

    public UDAnimatorSet setOnCancelCallback(LuaValue mOnCancelCallback) {
        this.mOnCancelCallback = mOnCancelCallback;
        return this;
    }

    public UDAnimatorSet setOnRepeatCallback(LuaValue mOnRepeatCallback) {
        this.mOnRepeatCallback = mOnRepeatCallback;
        return this;
    }

    public UDAnimatorSet setOnPauseCallback(LuaValue mOnPauseCallback) {
        this.mOnPauseCallback = mOnPauseCallback;
        return this;
    }

    public UDAnimatorSet setOnResumeCallback(LuaValue mOnResumeCallback) {
        this.mOnResumeCallback = mOnResumeCallback;
        return this;
    }

    // 这个在AnimatorSet中没有
    public UDAnimatorSet setOnUpdateCallback(LuaValue mOnUpdateCallback) {
        this.mOnUpdateCallback = mOnUpdateCallback;
        return this;
    }


    /**
     * 设置属性
     *
     * @param name
     * @return
     */
    public UDAnimatorSet ofProperty(final String name, float... values) {
        if (mAnimators != null && TextUtils.isEmpty(name) == false) {
            final ObjectAnimator animator = createAnimator();
            animator.setPropertyName(name);
            if (values != null && values.length > 0) {
                animator.setFloatValues(values);
            }
            mAnimators.add(animator);
        }
        return this;
    }

    /**
     * 时长
     *
     * @param duration
     * @return
     */
    public UDAnimatorSet setDuration(long duration) {
        final AnimatorSet animatorSet = getAnimatorSet();
        if (animatorSet != null && duration >= 0) {
            animatorSet.setDuration(duration);
        }
        return this;
    }

    /**
     * 启动延时
     *
     * @param delay
     * @return
     */
    public UDAnimatorSet setStartDelay(long delay) {
        final AnimatorSet animator = getAnimatorSet();
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
    public UDAnimatorSet setRepeatCount(int repeatCount) {
        this.mRepeatCount = repeatCount;
        return this;
    }

    /**
     * 重复方式(1 default, -1 infinite, 2 reverse)
     *
     * @param repeatMode
     * @return
     */
    public UDAnimatorSet setRepeatMode(int repeatMode) {
        this.mRepeatMode = repeatMode;
        return this;
    }

    /**
     * 浮点数
     *
     * @param values
     * @return
     */
    public UDAnimatorSet setFloatValues(float... values) {
        mFloatValues = values;
        return this;
    }

    /**
     * 整数
     *
     * @param values
     * @return
     */
    public UDAnimatorSet setIntValues(int... values) {
        mIntValues = values;
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
    public UDAnimatorSet setInterpolator(final Interpolator interpolator) {
        final AnimatorSet animator = getAnimatorSet();
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
    public UDAnimatorSet start() {
        final AnimatorSet animatorSet = getAnimatorSet();
        if (animatorSet != null && mTarget != null) {
            if (!animatorSet.isStarted()) {
                setup(animatorSet);
                animatorSet.setupStartValues();//设置开始值
                animatorSet.start();
            }
        }
        return this;
    }

    /**
     * cancel animator
     *
     * @return
     */
    public UDAnimatorSet cancel() {
        final AnimatorSet animatorSet = getAnimatorSet();
        if (animatorSet != null) {
            if (animatorSet.isStarted()) {
                animatorSet.cancel();
                animatorSet.setupEndValues();
            }
        }

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
    public UDAnimatorSet pause() {
        final AnimatorSet animatorSet = getAnimatorSet();
        if (animatorSet != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!animatorSet.isPaused()) {
                    animatorSet.pause();
                }
            }
        }

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
    public UDAnimatorSet resume() {
        final AnimatorSet animatorSet = getAnimatorSet();
        if (animatorSet != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (animatorSet.isPaused()) {
                    animatorSet.resume();
                }
            }
        }

        if (mTarget != null) {
            mTarget.resumeAnimation();
        }

        return this;
    }

    public UDAnimatorSet setup(Animator animator) {
        setupValues(animator);
        setupListeners(animator);
        return this;
    }

    public UDAnimatorSet setupValues(Animator animator) {
        animator.setupStartValues();
        if (mTarget != null && mAnimators != null) {
            for (Animator anim : mAnimators) {
                anim.setTarget(mTarget.getView());
                anim.setupStartValues();
                if (anim instanceof ValueAnimator) {
                    ((ValueAnimator) anim).setRepeatCount(mRepeatCount);
                    ((ValueAnimator) anim).setRepeatMode(mRepeatMode);
                    //TODO
                }
            }
            if (animator instanceof AnimatorSet) {
                ((AnimatorSet) animator).playTogether(mAnimators);
            }
        }
        return this;
    }

    /**
     * setup listeners
     *
     * @param animator
     * @return
     */
    public UDAnimatorSet setupListeners(Animator animator) {
        if (animator != null) {
            animator.removeAllListeners();//先移除所有listener、pause listener
            if (animator instanceof ValueAnimator) {//移除所有update listener
                ((ValueAnimator) animator).removeAllUpdateListeners();
            }
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
        final AnimatorSet animatorSet = getAnimatorSet();
        final AnimatorSet result = animatorSet.clone();//克隆一份
        setup(result);
        return result;
    }

    public boolean isRunning() {
        return getAnimatorSet().isRunning();
    }

    public boolean isStarted() {
        return getAnimatorSet().isStarted();
    }

    public boolean isPaused() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return getAnimatorSet().isPaused();
        } else {//TODO 这个判断是否有问题?
            return !isRunning() && isStarted();
        }
    }
    //----------------------------------------listeners---------------------------------------------

    private void addAnimatorListener(Animator animator) {
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

    private void addOnPauseListener(Animator animator) {
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

    private void addOnUpdateListener(Animator animator) {
        if (mOnUpdateCallback != null) {
            if (animator instanceof ValueAnimator) {
                ((ValueAnimator) animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        LuaUtil.callFunction(mOnUpdateCallback);
                    }
                });
            }
        }
    }
}
