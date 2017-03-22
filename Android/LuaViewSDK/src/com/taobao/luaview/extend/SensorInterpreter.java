/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */
package com.taobao.luaview.extend;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.WindowManager;

import static android.hardware.SensorManager.AXIS_MINUS_X;
import static android.hardware.SensorManager.AXIS_MINUS_Y;
import static android.hardware.SensorManager.AXIS_X;
import static android.hardware.SensorManager.AXIS_Y;

public class SensorInterpreter {

    private static final String TAG = SensorInterpreter.class.getName();

    private float[] mTiltVector = new float[3];

    private boolean mTargeted = false;

    private float[] mTargetMatrix = new float[16];

    private float[] mRotationMatrix = new float[16];
    private float[] mOrientedRotationMatrix = new float[16];

    private float[] mTruncatedRotationVector;

    private float mTiltSensitivity = 2.0f;

    @SuppressWarnings("SuspiciousNameCombination")
    public float[] interpretSensorEvent(@NonNull Context context, @Nullable SensorEvent event) {
        if (event == null) {
            return null;
        }

        float[] rotationVector = getRotationVectorFromSensorEvent(event);

        if (!mTargeted) {
            setTargetVector(rotationVector);
            return null;
        }

        SensorManager.getRotationMatrixFromVector(mRotationMatrix, rotationVector);

        final int rotation = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getRotation();

        if (rotation == Surface.ROTATION_0) {
            SensorManager.getAngleChange(mTiltVector, mRotationMatrix, mTargetMatrix);
        } else {
            switch (rotation) {
                case Surface.ROTATION_90:
                    SensorManager.remapCoordinateSystem(mRotationMatrix, AXIS_Y, AXIS_MINUS_X, mOrientedRotationMatrix);
                    break;

                case Surface.ROTATION_180:
                    SensorManager.remapCoordinateSystem(mRotationMatrix, AXIS_MINUS_X, AXIS_MINUS_Y, mOrientedRotationMatrix);
                    break;

                case Surface.ROTATION_270:
                    SensorManager.remapCoordinateSystem(mRotationMatrix, AXIS_MINUS_Y, AXIS_X, mOrientedRotationMatrix);
                    break;
            }

            SensorManager.getAngleChange(mTiltVector, mOrientedRotationMatrix, mTargetMatrix);
        }

        for (int i = 0; i < mTiltVector.length; i++) {
            mTiltVector[i] /= Math.PI;

            mTiltVector[i] *= mTiltSensitivity;

            if (mTiltVector[i] > 1) {
                mTiltVector[i] = 1f;
            } else if (mTiltVector[i] < -1) {
                mTiltVector[i] = -1f;
            }
        }

        return mTiltVector;
    }

    @NonNull
    float[] getRotationVectorFromSensorEvent(@NonNull SensorEvent event) {
        if (event.values.length > 4) {
            // On some Samsung devices SensorManager.getRotationMatrixFromVector
            // appears to throw an exception if rotation vector has length > 4.
            // For the purposes of this class the first 4 values of the
            // rotation vector are sufficient (see crbug.com/335298 for details).
            if (mTruncatedRotationVector == null) {
                mTruncatedRotationVector = new float[4];
            }
            System.arraycopy(event.values, 0, mTruncatedRotationVector, 0, 4);
            return mTruncatedRotationVector;
        } else {
            return event.values;
        }
    }

    protected void setTargetVector(float[] values) {
        SensorManager.getRotationMatrixFromVector(mTargetMatrix, values);
        mTargeted = true;
    }

    public void reset() {
        mTargeted = false;
    }

    public float getTiltSensitivity() {
        return mTiltSensitivity;
    }

    public void setTiltSensitivity(float tiltSensitivity) {
        if (tiltSensitivity <= 0) {
            throw new IllegalArgumentException("Tilt sensitivity must be positive");
        }

        mTiltSensitivity = tiltSensitivity;
    }

}

