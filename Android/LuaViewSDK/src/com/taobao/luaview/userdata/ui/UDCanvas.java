package com.taobao.luaview.userdata.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.taobao.luaview.global.LuaResourceFinder;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.lang.reflect.Method;

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
        set("save", new save());
        set("restore", new restore());
        set("clipRect", new ClipRect());

        set("drawLine", new drawLine());
        set("drawPoint", new drawPoint());
        set("drawRect", new drawRect());
        set("drawRoundRect", new drawRoundRect());
        set("drawCircle", new drawCircle());
        set("drawText", new drawText());
        set("drawOval", new drawOval());
        set("drawColor", new drawColor());
        set("drawArc", new drawArc());
        set("drawBitmap", new drawBitmap());
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
     * save
     */
    class save extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            Canvas canvas = mCanvas;
            if (canvas != null) {
                canvas.save();
            }
            return UDCanvas.this;
        }
    }

    /**
     * restore
     */
    class restore extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            Canvas canvas = mCanvas;
            if (canvas != null) {
                if (args.narg() > 1) {
                    final int count = LuaUtil.getInt(args, 2);
                    canvas.restoreToCount(count);
                } else {
                    canvas.restore();
                }
            }
            return UDCanvas.this;
        }
    }

    /**
     * clip rect
     */
    class ClipRect extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                if (varargs.istable(2)) {
                    clipRects(varargs);
                } else {
                    clipRect(varargs);
                }
            }
            return UDCanvas.this;
        }

        private void clipRect(Varargs value) {
            final Canvas canvas = mCanvas;
            if (canvas != null && value != null && value.narg() >= 4) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float x2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float y2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                canvas.clipRect(x1, y1, x2, y2);
            }
        }

        private void clipRects(Varargs varargs) {
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
                                canvas.clipRect(DimenUtil.dpiToPx(value.get(1)),
                                        DimenUtil.dpiToPx(value.get(2)),
                                        DimenUtil.dpiToPx(value.get(3)),
                                        DimenUtil.dpiToPx(value.get(4)));
                            }
                        }
                    }
                }
            }
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
            return UDCanvas.this;
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
            return UDCanvas.this;
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
            return UDCanvas.this;
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
            return UDCanvas.this;
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
            return UDCanvas.this;
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
            return UDCanvas.this;
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
            return UDCanvas.this;
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
            return UDCanvas.this;
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
            return UDCanvas.this;
        }
    }

    /**
     * draw a bitmap
     */
    class drawBitmap extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                drawBitmap(varargs);
            }
            return UDCanvas.this;
        }

        private void drawBitmap(Varargs value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Canvas canvas = mCanvas;
                if (canvas != null && value != null && value.narg() >= 4) {
                    final LuaValue param = LuaUtil.getValue(value, 2);
                    Drawable drawable = null;
                    if (LuaUtil.isString(param)) {
                        final String uri = param.optjstring(null);
                        final LuaResourceFinder finder = getLuaResourceFinder();
                        if (!TextUtils.isEmpty(uri) && finder != null) {
                            drawable = finder.findDrawable(uri);
                        }
                    } else if (param instanceof UDImageView) {
                        View view = ((UDImageView) param).getView();
                        if (view instanceof ImageView) {
                            drawable = ((ImageView) view).getDrawable();
                        }
                    }

                    Bitmap bitmap = null;
                    if (drawable instanceof BitmapDrawable) {
                        bitmap = ((BitmapDrawable) drawable).getBitmap();
                    } else {//TODO glide GlideImageDrawable，通过反射
                        try {
                            Method method = drawable.getClass().getMethod("getBitmap");
                            if (method != null) {
                                bitmap = (Bitmap) method.invoke(drawable);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (bitmap != null) {
                        final float x = DimenUtil.dpiToPx(LuaUtil.getValue(value, 3));
                        final float y = DimenUtil.dpiToPx(LuaUtil.getValue(value, 4));
                        if (value.narg() >= 6) {
                            final float x1 = DimenUtil.dpiToPx(LuaUtil.getValue(value, 5));
                            final float y1 = DimenUtil.dpiToPx(LuaUtil.getValue(value, 6));
                            canvas.drawBitmap(bitmap, null, new RectF(x, y, x1, y1), getDefaultPaint());
                        } else {
                            canvas.drawBitmap(bitmap, x, y, getDefaultPaint());
                        }
                    }
                }
            }
        }
    }
}