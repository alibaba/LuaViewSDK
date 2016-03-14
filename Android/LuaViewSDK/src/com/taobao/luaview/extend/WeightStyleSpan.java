package com.taobao.luaview.extend;

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import com.taobao.luaview.userdata.constants.UDFontWeight;

/**
 * custom style for weight
 */
public class WeightStyleSpan extends CharacterStyle {
    private final int mWeight;

    public WeightStyleSpan(int weight) {
        mWeight = weight;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        final float newStrokeWidth = (mWeight / (UDFontWeight.WEIGHT_NORMAL_INT + 0.0f));
        if (paint.getStyle() == Paint.Style.FILL) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        paint.setStrokeWidth(newStrokeWidth);
    }

}
