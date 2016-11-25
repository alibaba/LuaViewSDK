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

/*
 * Copyright 2014 Nathan VanBenschoten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SensorInterpreter {

    private static final String TAG = SensorInterpreter.class.getName();

    /**
     * The standardized tilt vector corresponding to yaw, pitch, and roll deltas from target matrix.
     */
    private float[] mTiltVector = new float[3];

    /**
     * Whether the SensorInterpreter has set a target to calculate tilt offset from.
     */
    private boolean mTargeted = false;

    /**
     * The target rotation matrix to calculate tilt offset from.
     */
    private float[] mTargetMatrix = new float[16];

    /**
     * Rotation matrices used during calculation.
     */
    private float[] mRotationMatrix = new float[16];
    private float[] mOrientedRotationMatrix = new float[16];

    /**
     * Holds a shortened version of the rotation vector for compatibility purposes.
     */
    private float[] mTruncatedRotationVector;

    /**
     * The sensitivity the parallax effect has towards tilting.
     */
    private float mTiltSensitivity = 2.0f;

    /**
     * Converts sensor data in a {@link android.hardware.SensorEvent} to yaw, pitch, and roll.
     *
     * @param context the context of the
     * @param event   the event to interpret
     * @return an interpreted vector of yaw, pitch, and roll delta values
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public float[] interpretSensorEvent(@NonNull Context context, @Nullable SensorEvent event) {
        if (event == null) {
            return null;
        }

        // Retrieves the RotationVector from SensorEvent
        float[] rotationVector = getRotationVectorFromSensorEvent(event);

        // Set target rotation if none has been set
        if (!mTargeted) {
            setTargetVector(rotationVector);
            return null;
        }

        // Get rotation matrix from event's values
        SensorManager.getRotationMatrixFromVector(mRotationMatrix, rotationVector);

        // Acquire rotation of screen
        final int rotation = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getRotation();

        // Calculate angle differential between target and current orientation
        if (rotation == Surface.ROTATION_0) {
            SensorManager.getAngleChange(mTiltVector, mRotationMatrix, mTargetMatrix);
        } else {
            // Adjust axes on screen orientation by remapping coordinates
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

        // Perform value scaling and clamping on value array
        for (int i = 0; i < mTiltVector.length; i++) {
            // Map domain of tilt vector from radian (-PI, PI) to fraction (-1, 1)
            mTiltVector[i] /= Math.PI;

            // Adjust for tilt sensitivity
            mTiltVector[i] *= mTiltSensitivity;

            // Clamp values to image bounds
            if (mTiltVector[i] > 1) {
                mTiltVector[i] = 1f;
            } else if (mTiltVector[i] < -1) {
                mTiltVector[i] = -1f;
            }
        }

        return mTiltVector;
    }

    /**
     * Pulls out the rotation vector from a {@link android.hardware.SensorEvent}, with a maximum length
     * vector of four elements to avoid potential compatibility issues.
     *
     * @param event the sensor event
     * @return the events rotation vector, potentially truncated
     */
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

    /**
     * Sets the target direction used for angle deltas to determine tilt.
     *
     * @param values a rotation vector (presumably from a ROTATION_VECTOR sensor)
     */
    protected void setTargetVector(float[] values) {
        SensorManager.getRotationMatrixFromVector(mTargetMatrix, values);
        mTargeted = true;
    }

    /**
     * Resets the state of the SensorInterpreter, removing any target direction used for angle
     * deltas to determine tilt.
     */
    public void reset() {
        mTargeted = false;
    }

    /**
     * Determines the tilt sensitivity of the SensorInterpreter.
     *
     * @return the tilt sensitivity
     */
    public float getTiltSensitivity() {
        return mTiltSensitivity;
    }

    /**
     * Sets the new sensitivity that the SensorInterpreter will scale tilt calculations by. If this
     * sensitivity is above 1, the interpreter will have to clamp percentages to 100% and -100% at
     * the tilt extremes.
     *
     * @param tiltSensitivity the new tilt sensitivity
     */
    public void setTiltSensitivity(float tiltSensitivity) {
        if (tiltSensitivity <= 0) {
            throw new IllegalArgumentException("Tilt sensitivity must be positive");
        }

        mTiltSensitivity = tiltSensitivity;
    }

}

