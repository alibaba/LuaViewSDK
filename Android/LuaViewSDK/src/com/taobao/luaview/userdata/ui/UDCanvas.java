/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.taobao.luaview.global.LuaResourceFinder;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.userdata.constants.UDTextAlign;
import com.taobao.luaview.userdata.kit.UDBitmap;
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

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Canvas，View.onDraw返回的数据
 *
 * @author song
 */
public class UDCanvas extends BaseLuaTable {
    private static final Map<String, Integer> sPaintAttrIndex;
    private static final int PAINT_COLOR = 1;
    private static final int PAINT_ALPHA = 2;
    private static final int PAINT_STROKE_WIDTH = 3;
    private static final int PAINT_TEXT_SIZE = 4;
    private static final int PAINT_UNDERLINE = 5;
    private static final int PAINT_STRIKE_THROUGH = 6;
    private static final int PAINT_BOLD = 7;
    private static final int PAINT_LETTER_SPACING = 8;
    private static final int PAINT_TYPEFACE = 9;
    private static final int PAINT_TEXT_SCALE_X = 10;
    private static final int PAINT_TEXT_SKEW_X = 11;
    private static final int PAINT_LINEAR_TEXT = 12;
    private static final int PAINT_TEXT_ALIGN = 13;
    private static final int PAINT_STYLE = 14;
    private static final int PAINT_FILTER_BITMAP = 15;

    private static final int PAINT_STYLE_FILL = 16;
    private static final int PAINT_STYLE_STROKE = 17;
    private static final int PAINT_STYLE_BOTH = 18;

    private WeakReference<Canvas> mCanvas;
    private Paint mPaint;
    private RectF mRectF = new RectF();

    static {
        //attrs
        sPaintAttrIndex = new HashMap<String, Integer>();
        sPaintAttrIndex.put("color", PAINT_COLOR);
        sPaintAttrIndex.put("alpha", PAINT_ALPHA);
        sPaintAttrIndex.put("strokewidth", PAINT_STROKE_WIDTH);
        sPaintAttrIndex.put("textsize", PAINT_TEXT_SIZE);
        sPaintAttrIndex.put("underline", PAINT_UNDERLINE);
        sPaintAttrIndex.put("strikethrough", PAINT_STRIKE_THROUGH);
        sPaintAttrIndex.put("bold", PAINT_BOLD);//bold
        sPaintAttrIndex.put("textbold", PAINT_BOLD);//bold
        sPaintAttrIndex.put("letterspacing", PAINT_LETTER_SPACING);
        sPaintAttrIndex.put("typeface", PAINT_TYPEFACE);//font
        sPaintAttrIndex.put("font", PAINT_TYPEFACE);//font
        sPaintAttrIndex.put("textscalex", PAINT_TEXT_SCALE_X);
        sPaintAttrIndex.put("textskewx", PAINT_TEXT_SKEW_X);
        sPaintAttrIndex.put("lineartext", PAINT_LINEAR_TEXT);
        sPaintAttrIndex.put("textalign", PAINT_TEXT_ALIGN);
        sPaintAttrIndex.put("style", PAINT_STYLE);
        sPaintAttrIndex.put("filterbitmap", PAINT_FILTER_BITMAP);

        //style
        sPaintAttrIndex.put("fill", PAINT_STYLE_FILL);
        sPaintAttrIndex.put("stroke", PAINT_STYLE_STROKE);
        sPaintAttrIndex.put("strokefill", PAINT_STYLE_BOTH);
        sPaintAttrIndex.put("fillstroke", PAINT_STYLE_BOTH);
        sPaintAttrIndex.put("both", PAINT_STYLE_BOTH);
    }

    public UDCanvas(LVViewGroup target, Canvas canvas, Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable, varargs);
        init(target, canvas);
    }

    private void init(LVViewGroup target, Canvas canvas) {
        setCanvas(canvas);

        //其他 5
        set("nativeObj", new nativeObj());//获取canvas对象

        set("size", new size());//获取canvas的宽高, size(), return 100, 100
        set("save", new save());//保持当前canvas状态, save()
        set("restore", new restore());//恢复canvas状态, restore()
        set("clipRect", new clipRect());//clip rect, clipRect(left, top, right, bottom)

        //paint 8
        set("color", new color());//color(0xff0000)
        set("textSize", new textSize());//textSize(20)
        set("alpha", new alpha());//alpha(0.5)
        set("strokeWidth", new strokeWidth());//画笔宽度 strokeWidth(2)
        set("style", new style());//style("fill", "stroke", "fillStroke")
        set("font", new font());//font("name")
        set("bold", new textBold());//bold(true)
        set("resetPaint", new resetPaint());//resetPaint()

        //变换操作 4
        set("translate", new translate());//translate(dx, dy), translate(-10, 10)
        set("scale", new scale());//scale(2, 2) or scale(2, 2, 坐标x, 坐标y)
        set("rotate", new rotate());//rotate(30) or rotate(30, 0, 0)
        set("skew", new skew());//skew(x, y)

        //绘制操作 9
        set("drawLine", new drawLine());//drawLine(x1, y1, x2, y2)
        set("drawPoint", new drawPoint());//drawPoint(x, y)
        set("drawRect", new drawRect());//drawRect(x1, y1, x2, y2)
        set("drawRoundRect", new drawRoundRect());//drawRoundRect(x1, y1, x2, y2, rx, ry)
        set("drawCircle", new drawCircle());//drawCircle(x, y, r)
        set("drawText", new drawText());//drawText("text", x, y)
        set("drawOval", new drawOval());//drawOval(x1, y1, x2, y2)
        set("drawArc", new drawArc());//drawArc(x1, y1, x2, y2, 开始角度, 结束角度, 是否以中心为点)
        set("drawImage", new drawImage());//drawBitmap("name", x, y) or drawBitmap("name, x1, y1, x2, y2) or drawBitmap(Image(), ...)
    }

    public void setTarget(LVViewGroup mTarget) {
        //TODO
    }

    public void setCanvas(Canvas mCanvas) {
        this.mCanvas = new WeakReference<Canvas>(mCanvas);
    }

    public Canvas getCanvas() {
        return mCanvas != null ? mCanvas.get() : null;
    }

    private Paint getDefaultPaint(LuaValue config) {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        //config
        if (config instanceof LuaTable) {
            mPaint.reset();

            final LuaValue[] keys = ((LuaTable) config).keys();
            if (keys != null && keys.length > 0) {

                String key;
                LuaValue value;
                for (int i = 0; i < keys.length; i++) {
                    if (LuaUtil.isString(keys[i])) {
                        key = keys[i].optjstring(null);
                        if (!TextUtils.isEmpty(key)) {
                            key = key.toLowerCase();
                            value = config.get(keys[i]);
                            if (LuaUtil.isValid(value)) {
                                Integer pos = sPaintAttrIndex.get(key);
                                if (pos == null) {
                                    continue;
                                }
                                switch (pos) {
                                    case PAINT_COLOR:
                                        mPaint.setColor(ColorUtil.parse(value));
                                        break;
                                    case PAINT_ALPHA:
                                        mPaint.setAlpha(LuaUtil.toAlphaInt(value));
                                        break;
                                    case PAINT_STROKE_WIDTH:
                                        mPaint.setStrokeWidth(DimenUtil.dpiToPxF((float) value.optdouble(0)));
                                        break;
                                    case PAINT_TEXT_SIZE:
                                        mPaint.setTextSize(DimenUtil.spToPx((float) value.optdouble(12.0f)));
                                        break;
                                    case PAINT_UNDERLINE:
                                        mPaint.setUnderlineText(value.optboolean(false));
                                        break;
                                    case PAINT_STRIKE_THROUGH:
                                        mPaint.setStrikeThruText(value.optboolean(false));
                                        break;
                                    case PAINT_BOLD:
                                        mPaint.setFakeBoldText(value.optboolean(false));
                                        break;
                                    case PAINT_LETTER_SPACING:
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            mPaint.setLetterSpacing(DimenUtil.dpiToPxF((float) value.optdouble(0)));
                                        }
                                        break;
                                    case PAINT_TYPEFACE: {
                                        String typeface = value.optjstring(null);
                                        if (!TextUtils.isEmpty(typeface)) {
                                            mPaint.setTypeface(getLuaResourceFinder().findTypeface(typeface));
                                        }
                                        break;
                                    }
                                    case PAINT_TEXT_SCALE_X:
                                        mPaint.setTextScaleX((float) value.optdouble(1));
                                        break;
                                    case PAINT_TEXT_SKEW_X:
                                        mPaint.setTextSkewX((float) value.optdouble(0));
                                        break;
                                    case PAINT_LINEAR_TEXT:
                                        mPaint.setLinearText(value.optboolean(false));
                                        break;
                                    case PAINT_TEXT_ALIGN: {
                                        setPaintTextAlign(value);
                                        break;
                                    }
                                    case PAINT_STYLE: {
                                        setPaintStyle(value);
                                        break;
                                    }
                                    case PAINT_FILTER_BITMAP:
                                        mPaint.setFilterBitmap(value.optboolean(false));
                                        break;

                                }
                            }
                        }
                    }
                }
            }

            //anti alias
            mPaint.setAntiAlias(true);
        }

        return mPaint;
    }

    /**
     * set Paint text Align
     *
     * @param value
     */
    private void setPaintTextAlign(LuaValue value) {
        Paint paint = getDefaultPaint(null);
        if (paint != null) {
            final int align = value.optint(1);
            switch (align) {
                case 0:
                case UDTextAlign.LEFT:
                    paint.setTextAlign(Paint.Align.LEFT);
                    break;
                case 2:
                case UDTextAlign.RIGHT:
                    paint.setTextAlign(Paint.Align.RIGHT);
                    break;
                case 1:
                case UDTextAlign.CENTER:
                default:
                    paint.setTextAlign(Paint.Align.CENTER);
                    break;
            }
        }
    }

    /**
     * set Paint Style
     *
     * @param value
     */
    private void setPaintStyle(LuaValue value) {
        Paint paint = getDefaultPaint(null);
        if (paint != null) {
            if (LuaUtil.isNumber(value)) {
                final int style = value.optint(1);
                switch (style) {
                    case 1:
                        paint.setStyle(Paint.Style.STROKE);
                        break;
                    case 2:
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        break;
                    case 0:
                    default:
                        paint.setStyle(Paint.Style.FILL);
                        break;
                }
            } else if (LuaUtil.isString(value)) {
                final String style = value.optjstring(null);
                final Integer styleInt = sPaintAttrIndex.get(style != null ? style.toLowerCase() : null);
                if (styleInt != null) {
                    switch (styleInt) {
                        case PAINT_STYLE_BOTH:
                            paint.setStyle(Paint.Style.FILL_AND_STROKE);
                            break;
                        case PAINT_STYLE_FILL:
                            paint.setStyle(Paint.Style.FILL);
                            break;
                        case PAINT_STYLE_STROKE:
                            paint.setStyle(Paint.Style.STROKE);
                            break;
                    }
                }
            }
        }
    }

    //-----------------------------------------------funs-------------------------------------------

    /**
     * return native Object
     */
    class nativeObj extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            return CoerceJavaToLua.coerce(getCanvas());
        }
    }

    /**
     * save
     */
    class save extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            Canvas canvas = getCanvas();
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
            Canvas canvas = getCanvas();
            if (canvas != null) {
                if (args.narg() >= 2) {
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
     * size
     */
    class size extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                final float width = DimenUtil.pxToDpi(canvas.getWidth());
                final float height = DimenUtil.pxToDpi(canvas.getHeight());
                return varargsOf(valueOf(width), valueOf(height));
            } else {
                return varargsOf(valueOf(0), valueOf(0));
            }
        }
    }

    /**
     * reset paint
     */
    class resetPaint extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            getDefaultPaint(null).reset();
            getDefaultPaint(null).setAntiAlias(true);
            return UDCanvas.this;
        }
    }

    //-----------------------------------------属性 opts--------------------------------------------

    /**
     * set color
     */
    class color extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() >= 2) {
                getDefaultPaint(null).setColor(ColorUtil.parse(LuaUtil.getValue(args, 2)));
            }
            return UDCanvas.this;
        }
    }

    /**
     * text size
     */
    class textSize extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() >= 2) {
                getDefaultPaint(null).setTextSize(DimenUtil.spToPx((float) args.optdouble(2, 12.0f)));
            }
            return UDCanvas.this;
        }
    }

    /**
     * alpha
     */
    class alpha extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() >= 2) {
                getDefaultPaint(null).setAlpha(LuaUtil.getAlphaInt(args, 2));
            }
            return UDCanvas.this;
        }
    }

    /**
     * strokeWidth
     */
    class strokeWidth extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() >= 2) {
                getDefaultPaint(null).setStrokeWidth(DimenUtil.dpiToPxF((float) args.optdouble(2, 0)));
            }
            return UDCanvas.this;
        }
    }

    /**
     * style
     */
    class style extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() >= 2) {
                setPaintStyle(args.arg(2));
            }
            return UDCanvas.this;
        }
    }

    /**
     * font
     */
    class font extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() >= 2) {
                final String typeface = args.optjstring(2, null);
                if (!TextUtils.isEmpty(typeface)) {
                    getDefaultPaint(null).setTypeface(getLuaResourceFinder().findTypeface(typeface));
                }
            }
            return UDCanvas.this;
        }
    }

    /**
     * text bold
     */
    class textBold extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() >= 2) {
                getDefaultPaint(null).setFakeBoldText(args.optboolean(2, false));
            }
            return UDCanvas.this;
        }
    }

    //-----------------------------------------变换 opts--------------------------------------------

    /**
     * translate
     */
    class translate extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                if (args.narg() >= 3) {
                    final float dx = DimenUtil.dpiToPx(LuaUtil.getFloat(args, 2));
                    final float dy = DimenUtil.dpiToPx(LuaUtil.getFloat(args, 3));
                    canvas.translate(dx, dy);
                }
            }
            return UDCanvas.this;
        }
    }

    /**
     * scale
     */
    class scale extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                if (args.narg() >= 5) {
                    final float sx = LuaUtil.getFloat(args, 2);
                    final float sy = LuaUtil.getFloat(args, 3);
                    final float px = DimenUtil.dpiToPx(LuaUtil.getFloat(args, 4));
                    final float py = DimenUtil.dpiToPx(LuaUtil.getFloat(args, 5));
                    canvas.scale(sx, sy, px, py);
                } else if (args.narg() >= 2) {//支持一个参数
                    final float sx = LuaUtil.getFloat(args, 2);
                    final float sy = LuaUtil.getFloat(args, 3, 2);
                    canvas.scale(sx, sy);
                }
            }
            return UDCanvas.this;
        }
    }

    /**
     * rotate
     */
    class rotate extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                if (args.narg() >= 4) {
                    final float degree = LuaUtil.getFloat(args, 2);
                    final float x = DimenUtil.dpiToPx(LuaUtil.getFloat(args, 3));
                    final float y = DimenUtil.dpiToPx(LuaUtil.getFloat(args, 4));
                    canvas.rotate(degree, x, y);
                } else if (args.narg() >= 2) {
                    final float degree = LuaUtil.getFloat(args, 2);
                    canvas.rotate(degree);
                }
            }
            return UDCanvas.this;
        }
    }

    /**
     * skew
     */
    class skew extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                if (args.narg() >= 3) {
                    final float sx = DimenUtil.dpiToPx(LuaUtil.getFloat(args, 2));
                    final float sy = DimenUtil.dpiToPx(LuaUtil.getFloat(args, 3));
                    canvas.skew(sx, sy);
                }
            }
            return UDCanvas.this;
        }
    }

    /**
     * clip rect
     */
    class clipRect extends VarArgFunction {
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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 5) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float dx = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float dy = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                canvas.clipRect(x1, y1, x1 + dx, y1 + dy);
            }
        }

        private void clipRects(Varargs varargs) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        LuaValue value = null;
                        float x1, y1, dx, dy;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 4) {
                                x1 = DimenUtil.dpiToPx(value.get(1));
                                y1 = DimenUtil.dpiToPx(value.get(2));
                                dx = DimenUtil.dpiToPx(value.get(3));
                                dy = DimenUtil.dpiToPx(value.get(4));
                                canvas.clipRect(x1, y1, x1 + dx, y1 + dy);
                            }
                        }
                    }
                }
            }
        }
    }

    //-----------------------------------------draw opts--------------------------------------------

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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 5) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float x2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float y2 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                final LuaValue config = LuaUtil.isTable(value.arg(6)) ? LuaUtil.getTable(value, 6) : null;
                canvas.drawLine(x1, y1, x2, y2, getDefaultPaint(config));
            }
        }

        private void drawLines(Varargs varargs) {
            final Canvas canvas = getCanvas();
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
                        final LuaValue config = LuaUtil.isTable(varargs.arg(3)) ? LuaUtil.getTable(varargs, 3) : null;
                        canvas.drawLines(pts, getDefaultPaint(config));
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
            final Canvas canvas = getCanvas();

            if (canvas != null && value != null && value.narg() >= 5) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float dx = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float dy = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                final LuaValue config = LuaUtil.isTable(value.arg(6)) ? LuaUtil.getTable(value, 6) : null;
                canvas.drawRect(x1, y1, x1 + dx, y1 + dy, getDefaultPaint(config));
            }
        }

        private void drawRects(Varargs varargs) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                final LuaValue config = LuaUtil.isTable(varargs.arg(3)) ? LuaUtil.getTable(varargs, 3) : null;
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        LuaValue value = null;
                        float x1, y1, dx, dy;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 4) {
                                x1 = DimenUtil.dpiToPx(value.get(1));
                                y1 = DimenUtil.dpiToPx(value.get(2));
                                dx = DimenUtil.dpiToPx(value.get(3));
                                dy = DimenUtil.dpiToPx(value.get(4));
                                canvas.drawRect(x1, y1, x1 + dx, y1 + dy, getDefaultPaint(LuaUtil.isTable(value.get(5)) ? value.get(5) : config));
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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 7) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float dx = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float dy = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                final float x3 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 6));
                final float y3 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 7));
                final LuaValue config = LuaUtil.isTable(value.arg(8)) ? LuaUtil.getTable(value, 8) : null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(x1, y1, x1 + dx, y1 + dy, x3, y3, getDefaultPaint(config));
                } else {
                    drawRoundRectPlain(x1, y1, x1 + dx, y1 + dy, x3, y3, getDefaultPaint(config), canvas);
                }
            }
        }

        private void drawRoundRects(Varargs varargs) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                final LuaValue config = LuaUtil.isTable(varargs.arg(3)) ? LuaUtil.getTable(varargs, 3) : null;
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        LuaValue value = null;
                        float x1, y1, dx, dy, x3, y3;
                        Paint paint = null;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 6) {
                                x1 = DimenUtil.dpiToPx(value.get(1));
                                y1 = DimenUtil.dpiToPx(value.get(2));
                                dx = DimenUtil.dpiToPx(value.get(3));
                                dy = DimenUtil.dpiToPx(value.get(4));
                                x3 = DimenUtil.dpiToPx(value.get(5));
                                y3 = DimenUtil.dpiToPx(value.get(6));
                                paint = getDefaultPaint(LuaUtil.isTable(value.get(7)) ? value.get(7) : config);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    canvas.drawRoundRect(x1, y1, x1 + dx, y1 + dy, x3, y3, paint);
                                } else {
                                    drawRoundRectPlain(x1, y1, x1 + dx, y1 + dy, x3, y3, paint, canvas);
                                }
                            }
                        }
                    }
                }
            }
        }

        //api 21 以下的绘制
        public void drawRoundRectPlain(float left, float top, float right, float bottom, float rx, float ry, Paint paint, Canvas canvas) {
            Path path = new Path();
            if (rx < 0) {
                rx = 0;
            }
            if (ry < 0) {
                ry = 0;
            }
            float width = right - left;
            float height = bottom - top;
            if (rx > width / 2) {
                rx = width / 2;
            }
            if (ry > height / 2) {
                ry = height / 2;
            }
            float widthMinusCorners = (width - (2 * rx));
            float heightMinusCorners = (height - (2 * ry));

            path.moveTo(right, top + ry);
            path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
            path.rLineTo(-widthMinusCorners, 0);
            path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
            path.rLineTo(0, heightMinusCorners);
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
            path.rLineTo(widthMinusCorners, 0);
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
            path.rLineTo(0, -heightMinusCorners);

            path.close();//Given close, last lineto can be removed.

            canvas.drawPath(path, paint);
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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 8) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float dx = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float dy = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                final float x3 = LuaUtil.getFloat(value, 6);
                final float y3 = LuaUtil.getFloat(value, 7);
                final boolean use = LuaUtil.getBoolean(value, false, 8);
                final LuaValue config = LuaUtil.isTable(value.arg(9)) ? LuaUtil.getTable(value, 9) : null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawArc(x1, y1, x1 + dx, y1 + dy, x3, y3, use, getDefaultPaint(config));
                } else {
                    final RectF rectF = mRectF;
                    rectF.left = x1;
                    rectF.top = y1;
                    rectF.right = x1 + dx;
                    rectF.bottom = y1 + dy;
                    canvas.drawArc(rectF, x3, y3, use, getDefaultPaint(config));
                }
            }
        }

        private void drawArcs(Varargs varargs) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                final LuaValue config = LuaUtil.isTable(varargs.arg(3)) ? LuaUtil.getTable(varargs, 3) : null;
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        LuaValue value = null;
                        float x1, y1, dx, dy, x3, y3;
                        boolean use = false;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 7) {
                                x1 = DimenUtil.dpiToPx(value.get(1));
                                y1 = DimenUtil.dpiToPx(value.get(2));
                                dx = DimenUtil.dpiToPx(value.get(3));
                                dy = DimenUtil.dpiToPx(value.get(4));
                                x3 = (float) value.get(5).optdouble(0);
                                y3 = (float) value.get(6).optdouble(0);
                                use = value.get(7).optboolean(false);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    canvas.drawArc(x1, y1, x1 + dx, y1 + dy, x3, y3, use,
                                            getDefaultPaint(LuaUtil.isTable(value.get(8)) ? value.get(8) : config));
                                } else {
                                    final RectF rectF = mRectF;
                                    rectF.left = x1;
                                    rectF.top = y1;
                                    rectF.right = x1 + dx;
                                    rectF.bottom = y1 + dy;
                                    canvas.drawArc(rectF, x3, y3, use, getDefaultPaint(config));
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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 3) {
                final float x = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final LuaValue config = LuaUtil.isTable(value.arg(4)) ? LuaUtil.getTable(value, 4) : null;
                canvas.drawPoint(x, y, getDefaultPaint(config));
            }
        }

        private void drawPoints(Varargs varargs) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                final LuaValue config = LuaUtil.isTable(varargs.arg(3)) ? LuaUtil.getTable(varargs, 3) : null;
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
                        canvas.drawPoints(pts, getDefaultPaint(config));
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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 4) {
                final float x = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float r = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final LuaValue config = LuaUtil.isTable(value.arg(5)) ? LuaUtil.getTable(value, 5) : null;
                canvas.drawCircle(x, y, r, getDefaultPaint(config));
            }
        }

        private void drawCircles(Varargs varargs) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                LuaTable table = LuaUtil.getTable(varargs, 2);
                final LuaValue config = LuaUtil.isTable(varargs.arg(3)) ? LuaUtil.getTable(varargs, 3) : null;
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
                                        getDefaultPaint(LuaUtil.isTable(value.get(4)) ? value.get(4) : config));
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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 4) {
                final CharSequence text = LuaUtil.getText(value, 2);
                if (!TextUtils.isEmpty(text)) {
                    final float x = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                    final float y = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                    final LuaValue config = LuaUtil.isTable(value.arg(5)) ? LuaUtil.getTable(value, 5) : null;
                    canvas.drawText(text, 0, text.length(), x, y, getDefaultPaint(config));
                }
            }
        }

        private void drawTexts(Varargs varargs) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                final LuaValue config = LuaUtil.isTable(varargs.arg(3)) ? LuaUtil.getTable(varargs, 3) : null;
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
                                            getDefaultPaint(LuaUtil.isTable(value.get(4)) ? value.get(4) : config));
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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 5) {
                final float x1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 2));
                final float y1 = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 3));
                final float dx = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 4));
                final float dy = DimenUtil.dpiToPx(LuaUtil.getFloat(value, 5));
                final LuaValue config = LuaUtil.isTable(value.arg(6)) ? LuaUtil.getTable(value, 6) : null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawOval(x1, y1, x1 + dx, y1 + dy, getDefaultPaint(config));
                } else {
                    final RectF rectF = mRectF;
                    rectF.left = x1;
                    rectF.top = y1;
                    rectF.right = x1 + dx;
                    rectF.bottom = y1 + dy;
                    canvas.drawOval(rectF, getDefaultPaint(config));
                }
            }
        }

        private void drawOvals(Varargs varargs) {
            final Canvas canvas = getCanvas();
            if (canvas != null) {
                final LuaTable table = LuaUtil.getTable(varargs, 2);
                final LuaValue config = LuaUtil.isTable(varargs.arg(3)) ? LuaUtil.getTable(varargs, 3) : null;
                if (table != null) {
                    final LuaValue[] keys = table.keys();
                    if (keys.length > 0) {
                        LuaValue value = null;
                        float x1, y1, dx, dy;
                        for (int i = 0; i < keys.length; i++) {
                            value = table.get(keys[i]);
                            if (value instanceof LuaTable && value.length() >= 4) {
                                x1 = DimenUtil.dpiToPx(value.get(1));
                                y1 = DimenUtil.dpiToPx(value.get(2));
                                dx = DimenUtil.dpiToPx(value.get(3));
                                dy = DimenUtil.dpiToPx(value.get(4));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    canvas.drawOval(x1, y1, x1 + dx, y1 + dy, getDefaultPaint(LuaUtil.isTable(value.get(5)) ? value.get(5) : config));
                                } else {
                                    final RectF rectF = mRectF;
                                    rectF.left = x1;
                                    rectF.top = y1;
                                    rectF.right = x1 + dx;
                                    rectF.bottom = y1 + dy;
                                    canvas.drawOval(rectF, getDefaultPaint(config));
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
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() > 1) {
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
    class drawImage extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs varargs) {
            if (varargs != null && varargs.narg() > 1) {
                drawImage(varargs);
            }
            return UDCanvas.this;
        }

        private void drawImage(Varargs value) {
            final Canvas canvas = getCanvas();
            if (canvas != null && value != null && value.narg() >= 4) {
                final LuaValue param = LuaUtil.getValue(value, 2);
                Drawable drawable = null;
                Bitmap bitmap = null;
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
                } else if (param instanceof UDBitmap){
                    bitmap = ((UDBitmap)param).getBitmap();
                }

                if (drawable != null) {
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
                }

                if (bitmap != null) {
                    final float x = DimenUtil.dpiToPx(LuaUtil.getValue(value, 3));
                    final float y = DimenUtil.dpiToPx(LuaUtil.getValue(value, 4));
                    LuaValue config = null;
                    if (value.narg() >= 6) {
                        final float dx = DimenUtil.dpiToPx(LuaUtil.getValue(value, 5));
                        final float dy = DimenUtil.dpiToPx(LuaUtil.getValue(value, 6));
                        config = LuaUtil.isTable(value.arg(7)) ? LuaUtil.getTable(value, 7) : null;
                        canvas.drawBitmap(bitmap, null, new RectF(x, y, x + dx, y + dy), getDefaultPaint(config));
                    } else {
                        config = LuaUtil.isTable(value.arg(5)) ? LuaUtil.getTable(value, 5) : null;
                        canvas.drawBitmap(bitmap, x, y, getDefaultPaint(config));
                    }
                }
            }
        }
    }
}