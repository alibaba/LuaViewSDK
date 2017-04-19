/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.view.View;

import com.taobao.luaview.extend.SensorInterpreter;
import com.taobao.luaview.userdata.ui.UDImageView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.view.imageview.BaseImageView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * LuaView-ImageView
 *
 * @author song
 * @date 15/8/20
 */
public class LVImageView extends BaseImageView implements ILVView, SensorEventListener {
    private UDImageView mLuaUserdata;

    private SensorManager mSensorManager;
    private SensorInterpreter mSensorInterpreter;

    //图片运动的偏移
    private float mMotionOffsetX = 0;
    private float mMotionOffsetY = 0;
    /**
     * 开关控制是否普通的JuImageView
     * 默认关闭,只有调用 {@link LVImageView#setMotionDistanceXY(float, float)} 才打开
     * 从而调用 {@link LVImageView#registerSensorManager(int)} 才会注册监听器
     * 以此保证不是所有的{@link LVImageView}都会监听SensorEvent
     */
    private boolean mMotionDetect = false;

    public LVImageView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDImageView(this, globals, metaTable, varargs);
        init();
    }

    private void init() {
        this.setScaleType(ScaleType.FIT_XY);//默认FIT_XY
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mSensorInterpreter == null) return;
        final float[] vectors = mSensorInterpreter.interpretSensorEvent(getContext(), event);

        // Return if interpretation of data failed
        if (vectors == null) return;

        // Set translation on ImageView matrix
        setTranslate(vectors[2], -vectors[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Sets the maximum percentage of the image that image matrix is allowed to translate
     * for each sensor reading.
     *
     * @param motionDistanceX horizontall motion
     * @param motionDistanceY vertical motion
     */
    public void setMotionDistanceXY(float motionDistanceX, float motionDistanceY) {
        mMotionDetect = true;
        if (mSensorInterpreter == null) {
            mSensorInterpreter = new SensorInterpreter();
            mSensorInterpreter.setTiltSensitivity(1);   // TODO: 11/16/16 配置一个可接受的默认敏感度
        }
        registerSensorManager();
        mMotionOffsetX = motionDistanceX;
        mMotionOffsetY = motionDistanceY;
    }

    /**
     * Sets the image view's translation coordinates. These values must be between -1 and 1,
     * representing the transaction percentage from the center.
     *
     * @param x the horizontal translation
     * @param y the vertical translation
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setTranslate(float x, float y) {
        if (Math.abs(x) > 1 || Math.abs(y) > 1) {
            throw new IllegalArgumentException("Parallax effect cannot translate more than 100% of its off-screen size");
        }
        setTranslationX(x * mMotionOffsetX);
        setTranslationY(y * mMotionOffsetY);
    }

    /**
     * Registers a sensor manager with the parallax ImageView. Should be called in onResume
     * or onStart lifecycle callbacks from an Activity or Fragment.
     */
    public void registerSensorManager() {
        registerSensorManager(SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Registers a sensor manager with the parallax ImageView. Should be called in onResume
     * or onStart lifecycle callbacks from an Activity or Fragment.
     *
     * @param samplingPeriodUs the sensor sampling period rate
     */
    public void registerSensorManager(int samplingPeriodUs) {
        if (getContext() == null || mSensorManager != null || !mMotionDetect) return;

        // Acquires a sensor manager
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager != null) {
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
            if (sensor == null) {
                sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                if (sensor == null) {
                    sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
                }
            }
            if (sensor != null) {
                mSensorManager.registerListener(this, sensor, samplingPeriodUs);
            } else {
                LogUtil.i("LuaViewSDK Exception", "Rotation sensor is null");
            }
        }
    }

    /**
     * Unregisters the ParallaxImageView's SensorManager. Should be called in onPause or onStop
     * lifecycle callbacks from an Activity or Fragment to avoid leaking sensor usage.
     */
    public void unregisterSensorManager() {
        unregisterSensorManager(false);
    }

    /**
     * Unregisters the ParallaxImageView's SensorManager. Should be called in onPause from
     * an Activity or Fragment to avoid continuing sensor usage.
     *
     * @param resetTranslation if the image translation should be reset to the origin
     */
    public void unregisterSensorManager(boolean resetTranslation) {
        if (mSensorManager == null || mSensorInterpreter == null) return;

        mSensorManager.unregisterListener(this);
        mSensorManager = null;
        mSensorInterpreter.reset();

        if (resetTranslation) {
            setTranslate(0, 0);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        if (visibility == View.VISIBLE) {
            registerSensorManager();
        }

        if (visibility == View.GONE) {
            unregisterSensorManager();
        }
    }
}
