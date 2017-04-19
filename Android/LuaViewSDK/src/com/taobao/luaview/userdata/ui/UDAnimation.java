/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


/**
 * AlphaAnimation, RotateAnimation, ScaleAnimation, TranslateAnimation
 * 废弃，使用Animator来处理
 * @author song
 */
@Deprecated
public class UDAnimation extends BaseUserdata {
    private static final int TYPE_ALPHA = 1;
    private static final int TYPE_ROTATE = 2;
    private static final int TYPE_SCALE = 3;
    private static final int TYPE_TRANSLATE = 4;

    private LuaValue mOnStartCallback;
    private LuaValue mOnEndCallback;
    private LuaValue mOnRepeatCallback;

    private int mAnimationType;
    private float[] mValues;
    private long mDuration = 300;
    private long mStartOffset = 0;
    private int mRepeatCount = 0;

    public UDAnimation(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals, metaTable, varargs);
    }

    public UDAnimation setAnimationType(int type) {
        mAnimationType = type;
        return this;
    }

    public UDAnimation alpha(float... values) {
        this.mAnimationType = TYPE_ALPHA;
        this.mValues = values;
        return this;
    }

    public UDAnimation rotate(float... values) {
        this.mAnimationType = TYPE_ROTATE;
        this.mValues = values;
        return this;
    }

    public UDAnimation scale(float... values) {
        this.mAnimationType = TYPE_SCALE;
        this.mValues = values;
        return this;
    }

    public UDAnimation translate(float... values) {
        this.mAnimationType = TYPE_TRANSLATE;
        this.mValues = values;
        return this;
    }

    public UDAnimation setValue(float... values) {
        this.mValues = values;
        return this;
    }

    public UDAnimation setDuration(long duration) {
        this.mDuration = duration;
        return this;
    }

    public UDAnimation setStartDelay(long mStartDelay) {
        this.mStartOffset = mStartDelay;
        return this;
    }

    public UDAnimation setRepeatCount(int mRepeatCount) {
        this.mRepeatCount = mRepeatCount;
        return this;
    }

    public UDAnimation setOnRepeatCallback(LuaFunction mOnRepeatCallback) {
        this.mOnRepeatCallback = mOnRepeatCallback;
        return this;
    }

    public UDAnimation setOnStartCallback(LuaFunction mOnStartCallback) {
        this.mOnStartCallback = mOnStartCallback;
        return this;
    }

    public UDAnimation setOnEndCallback(LuaFunction mOnEndCallback) {
        this.mOnEndCallback = mOnEndCallback;
        return this;
    }

    public UDAnimation setCallback(LuaTable callback) {
        if (callback != null) {
            this.mOnStartCallback = LuaUtil.getFunction(callback, "OnStart", "onStart");
            this.mOnEndCallback = LuaUtil.getFunction(callback, "OnEnd", "onEnd");
            this.mOnRepeatCallback = LuaUtil.getFunction(callback, "OnRepeat", "onRepeat");
        }
        return this;
    }

    /**
     * build an animation
     *
     * @return
     */
    public Animation build(View view) {
        Animation animation = null;
        if (view != null) {
            switch (mAnimationType) {
                case TYPE_ALPHA:
                    if (this.mValues != null && this.mValues.length > 0) {
                        animation = new AlphaAnimation(view.getAlpha(), this.mValues[0]);
                    }
                    break;
                case TYPE_ROTATE:
                    if (this.mValues != null && this.mValues.length > 0) {
                        animation = new RotateAnimation(view.getRotation(), this.mValues[0]);
                    }
                    break;
                case TYPE_SCALE:
                    if (this.mValues != null && this.mValues.length > 1) {
                        animation = new ScaleAnimation(view.getScaleX(), this.mValues[0], view.getScaleY(), this.mValues[1]);
                    }
                    break;
                case TYPE_TRANSLATE:
                    if (this.mValues != null && this.mValues.length > 1) {
                        animation = new TranslateAnimation(view.getX(), DimenUtil.dpiToPx(this.mValues[0]), view.getY(), DimenUtil.dpiToPx(this.mValues[1]));
                    }
                    break;
            }

            if (animation != null) {
                animation.setFillEnabled(true);
                animation.setFillAfter(true);//默认结束后设置属性
                animation.setFillBefore(true);
                animation.setDuration(mDuration);
                animation.setStartOffset(mStartOffset);
                animation.setRepeatCount(mRepeatCount);

                if (mOnStartCallback != null || mOnRepeatCallback != null || mOnEndCallback != null) {
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            LuaUtil.callFunction(mOnStartCallback);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            LuaUtil.callFunction(mOnEndCallback);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            LuaUtil.callFunction(mOnRepeatCallback);
                        }
                    });
                }
            }
        }
        return animation;
    }

}
