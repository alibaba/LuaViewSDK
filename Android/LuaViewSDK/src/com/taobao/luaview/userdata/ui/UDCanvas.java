package com.taobao.luaview.userdata.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;

import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * Canvas，View.onDraw返回的数据
 *
 * @author song
 */
public class UDCanvas extends BaseLuaTable {

    private LVViewGroup mTarget;
    private Canvas mCanvas;
    private Paint mPaint;

    public UDCanvas(LVViewGroup target, Canvas canvas, Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable, varargs);
        init(target, canvas);
    }

    private void init(LVViewGroup target, Canvas canvas) {
        this.mTarget = target;
        this.mCanvas = canvas;
        set("nativeObj", new nativeObj());
        set("drawLine", new drawLine());
        set("drawPoint", new drawPoint());
        set("drawRect", new drawRect());
        set("drawRoundRect", new drawRoundRect());
        set("drawCircle", new drawCircle());
        set("drawText", new drawText());
        set("drawOval", new drawOval());
        set("drawColor", new drawColor());
        set("drawArc", new drawArc());
    }

    public void setTarget(LVViewGroup mTarget) {
        this.mTarget = mTarget;
    }

    public void setCanvas(Canvas mCanvas) {
        this.mCanvas = mCanvas;
    }

    private Paint getDefaultPaint() {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
//        mPaint.setColor(color);
//        mPaint.setAlpha(alpha);
//        mPaint.setStrokeWidth(width);
        return mPaint;
    }

    //-----------------------------------------------funs-------------------------------------------

    /**
     * return native Object
     */
    class nativeObj extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            return CoerceJavaToLua.coerce(mCanvas);
        }
    }

    /**
     * draw Line
     */
    class drawLine extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    drawLines(varargs);
                } else {
                    drawLine(varargs);
                }
            }
            return LuaValue.NIL;
        }

        private void drawLine(Varargs value) {
            final Canvas canvas = mCanvas;
            if (canvas != null && value != null && value.narg() >= 4) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float x2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float y2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                canvas.drawLine(x1, y1, x2, y2, getDefaultPaint());
            }
        }

        private void drawLines(Varargs varargs) {
            final Canvas canvas = mCanvas;
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        final float pts[] = new float[keys.length * 4];
                        LuaValue value = null;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 4) {
                                for (int j = 0; j < 4; j++) {
                                    pts[i * 4 + j] = DimenUtil.dpiToPx(value.get(j + 1));
                                }
                            }
                        }
                        canvas.drawLines(pts, getDefaultPaint());
                    }
                }
            }
        }
    }

    /**
     * draw rects
     */
    class drawRect extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    drawRects(varargs);
                } else {
                    drawRect(varargs);
                }
            }
            return LuaValue.NIL;
        }

        private void drawRect(Varargs value) {
            final Canvas canvas = mCanvas;

            if (canvas != null && value != null && value.narg() >= 4) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float x2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float y2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                canvas.drawRect(x1, y1, x2, y2, getDefaultPaint());
            }
        }

        private void drawRects(Varargs varargs) {
            final Canvas canvas = mCanvas;
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        LuaValue value = null;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 4) {
                                canvas.drawRect(DimenUtil.dpiToPx(value.get(1)),
                                        DimenUtil.dpiToPx(value.get(2)),
                                        DimenUtil.dpiToPx(value.get(3)),
                                        DimenUtil.dpiToPx(value.get(4)),
                                        getDefaultPaint());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * draw rects
     */
    class drawRoundRect extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    drawRoundRects(varargs);
                } else {
                    drawRoundRect(varargs);
                }
            }
            return LuaValue.NIL;
        }

        private void drawRoundRect(Varargs value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Canvas canvas = mCanvas;
                if (canvas != null && value != null && value.narg() >= 6) {
                    final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                    final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                    final float x2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                    final float y2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                    final float x3 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 6));
                    final float y3 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 7));
                    canvas.drawRoundRect(x1, y1, x2, y2, x3, y3, getDefaultPaint());
                }
            }
        }

        private void drawRoundRects(Varargs varargs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Canvas canvas = mCanvas;
                if (canvas != null) {
                    final LuaTable table = LuaUtil.getTable(varargs, 2);
                    if (table != null) {
                        final LuaValue[] keys = table.keys();
                        if (keys.length > 0) {
                            LuaValue value = null;
                            for (int i = 0; i < keys.length; i++) {
                                value = table.get(keys[i]);
                                if (value instanceof LuaTable && value.length() >= 6) {
                                    canvas.drawRoundRect(DimenUtil.dpiToPx(value.get(1)),
                                            DimenUtil.dpiToPx(value.get(2)),
                                            DimenUtil.dpiToPx(value.get(3)),
                                            DimenUtil.dpiToPx(value.get(4)),
                                            DimenUtil.dpiToPx(value.get(5)),
                                            DimenUtil.dpiToPx(value.get(6)),
                                            getDefaultPaint());
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * draw arc
     */
    class drawArc extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    drawArcs(varargs);
                } else {
                    drawArc(varargs);
                }
            }
            return LuaValue.NIL;
        }

        private void drawArc(Varargs value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Canvas canvas = mCanvas;
                if (canvas != null && value != null && value.narg() >= 7) {
                    final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                    final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                    final float x2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                    final float y2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                    final float x3 = LuaUtil.getFloat(value, 6);
                    final float y3 = LuaUtil.getFloat(value, 7);
                    final boolean use = LuaUtil.getBoolean(value, false, 8);
                    canvas.drawArc(x1, y1, x2, y2, x3, y3, use, getDefaultPaint());
                }
            }
        }

        private void drawArcs(Varargs varargs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Canvas canvas = mCanvas;
                if (canvas != null) {
                    final LuaTable table = LuaUtil.getTable(varargs, 2);
                    if (table != null) {
                        final LuaValue[] keys = table.keys();
                        if (keys.length > 0) {
                            LuaValue value = null;
                            for (int i = 0; i < keys.length; i++) {
                                value = table.get(keys[i]);
                                if (value instanceof LuaTable && value.length() >= 7) {
                                    canvas.drawArc(DimenUtil.dpiToPx(value.get(1)),
                                            DimenUtil.dpiToPx(value.get(2)),
                                            DimenUtil.dpiToPx(value.get(3)),
                                            DimenUtil.dpiToPx(value.get(4)),
                                            (float) value.get(5).optdouble(0),
                                            (float) value.get(6).optdouble(0),
                                            value.get(7).optboolean(false),
                                            getDefaultPaint());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * draw a point in canvas
     */
    class drawPoint extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    drawPoints(varargs);
                } else {
                    drawPoint(varargs);
                }
            }
            return LuaValue.NIL;
        }

        private void drawPoint(Varargs value) {
            final Canvas canvas = mCanvas;
            if (canvas != null && value != null && value.narg() >= 4) {
                final float x = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                canvas.drawPoint(x, y, getDefaultPaint());
            }
        }

        private void drawPoints(Varargs varargs) {
            final Canvas canvas = mCanvas;
            if (canvas != null) {
                LuaTable table = LuaUtil.getTable(varargs, 2);
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        final float pts[] = new float[keys.length * 2];
                        LuaValue value = null;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 2) {
                                for (int j = 0; j < 2; j++) {
                                    pts[i * 2 + j] = DimenUtil.dpiToPx(value.get(j + 1));
                                }
                            }
                        }
                        canvas.drawPoints(pts, getDefaultPaint());
                    }
                }
            }
        }
    }

    /**
     * draw a circle in canvas
     */
    class drawCircle extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    drawCircles(varargs);
                } else {
                    drawCircle(varargs);
                }
            }
            return LuaValue.NIL;
        }

        private void drawCircle(Varargs value) {
            final Canvas canvas = mCanvas;
            if (canvas != null && value != null && value.narg() >= 3) {
                final float x = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float r = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                canvas.drawCircle(x, y, r, getDefaultPaint());
            }
        }

        private void drawCircles(Varargs varargs) {
            final Canvas canvas = mCanvas;
            if (canvas != null) {
                LuaTable table = LuaUtil.getTable(varargs, 2);
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        LuaValue value = null;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 3) {
                                canvas.drawCircle(DimenUtil.dpiToPx(value.get(1)),
                                        DimenUtil.dpiToPx(value.get(2)),
                                        DimenUtil.dpiToPx(value.get(3)),
                                        getDefaultPaint());
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * draw a text in canvas
     */
    class drawText extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    drawTexts(varargs);
                } else {
                    drawText(varargs);
                }
            }
            return LuaValue.NIL;
        }

        private void drawText(Varargs value) {
            final Canvas canvas = mCanvas;
            if (canvas != null && value != null && value.narg() >= 3) {
                final CharSequence text = LuaUtil.getText(value, 2);
                if (!TextUtils.isEmpty(text)) {
                    final float x = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                    final float y = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                    canvas.drawText(text, 0, text.length(), x, y, getDefaultPaint());
                }
            }
        }

        private void drawTexts(Varargs varargs) {
            final Canvas canvas = mCanvas;
            if (canvas != null) {
                LuaTable table = LuaUtil.getTable(varargs, 2);
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        LuaValue value = null;
                        CharSequence text = null;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 3) {
                                text = LuaUtil.toText(value.get(1));
                                if (!TextUtils.isEmpty(text)) {
                                    canvas.drawText(text, 0, text.length(),
                                            DimenUtil.dpiToPx(value.get(2)),
                                            DimenUtil.dpiToPx(value.get(3)),
                                            getDefaultPaint());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * draw a oval in canvas
     */
    class drawOval extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    drawOvals(varargs);
                } else {
                    drawOval(varargs);
                }
            }
            return LuaValue.NIL;
        }

        private void drawOval(Varargs value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Canvas canvas = mCanvas;
                if (canvas != null && value != null && value.narg() >= 4) {
                    final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                    final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                    final float x2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                    final float y2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                    canvas.drawOval(x1, y1, x2, y2, getDefaultPaint());
                }
            }
        }

        private void drawOvals(Varargs varargs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Canvas canvas = mCanvas;
                if (canvas != null) {
                    LuaTable table = LuaUtil.getTable(varargs, 2);
                    if (table != null) {
                        final LuaValue[] keys = table.keys();
                        if (keys.length > 0) {
                            LuaValue value = null;
                            for (int i = 0; i < keys.length; i++) {
                                value = table.get(keys[i]);
                                if (value instanceof LuaTable && value.length() >= 4) {
                                    canvas.drawOval(DimenUtil.dpiToPx(value.get(1)),
                                            DimenUtil.dpiToPx(value.get(2)),
                                            DimenUtil.dpiToPx(value.get(3)),
                                            DimenUtil.dpiToPx(value.get(4)),
                                            getDefaultPaint());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * draw color
     */
    class drawColor extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs value) {
            final Canvas canvas = mCanvas;
            if (mCanvas != null && value != null && value.narg() > 1) {
                final Integer color = ColorUtil.parse(LuaUtil.getInt(value, 2));
                final Integer alpha = LuaUtil.getAlphaInt(value, 3);
                if (color != null) {
                    if (alpha != null) {
                        final int r = Color.red(color);
                        final int g = Color.green(color);
                        final int b = Color.green(color);
                        canvas.drawARGB(alpha, r, g, b);
                    } else {
                        canvas.drawColor(color);
                    }
                }
            }
            return LuaValue.NIL;
        }
    }


//        mCanvas.drawLine(float startX, float startY, float stopX, float stopY);
//        mCanvas.drawLine({
//                {sx1, sy1, ex1, ey1},
//                {sx2, sy2, ex2, ey2}
//        });
//        mCanvas.drawPoint(float x, float y);
//        mCanvas.drawPoint({
//                {},
//                {}
//        })
//
//        mCanvas.drawRect(float left, float top, float right, float bottom);
//        mCanvas.drawRect({
//                {},
//                {}
//        })
//
//        mCanvas.drawRoundRect(float left, float top, float right, float bottom, float rx, float ry);
//        mCanvas.drawRoundRect({
//                {},
//                {}
//        })
//
//        mCanvas.drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter);
//        mCanvas.drawArc({
//                {},
//                {}
//        })
//
//        mCanvas.drawCircle(x, y, r);
//        mCanvas.drawCircle({
//                {},
//                {}
//        })
//
//        mCanvas.drawText(text, x, y);
//        mCanvas.drawText({
//                {text, x, y},
//                {text, x, y}
//        })
//
//
//        mCanvas.drawOval(left, top, right, bottom);
//        mCanvas.drawOval({
//                {left, top, right, bottom},
//        });
//
//        mCanvas.drawColor(color, alpha);

//        mCanvas.save();
//        mCanvas.restore();
//
//        mCanvas.drawBitmap(bitmap, x, y, paint);

}