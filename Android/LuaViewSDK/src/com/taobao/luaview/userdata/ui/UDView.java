package com.taobao.luaview.userdata.ui;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.csslayout.CSSNode;
import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.util.AnimatorUtil;
import com.taobao.luaview.util.FlexboxCSSParser;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.LVViewGroup;
import com.taobao.luaview.view.drawable.LVGradientDrawable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

/**
 * View 数据封装
 *
 * @param <T>
 * @author song
 */
public class UDView<T extends View> extends BaseUserdata {
    public LuaValue initParams = LuaValue.NIL;
    //回调
    public LuaValue mCallback;

    //点击
    private LuaValue mOnClick;
    private LuaValue mOnLongClick;

    //动画列表
    private List<Animator> mAnimators;

    /**
     * Flexbox layout
     */
    private CSSNode mCssNode;
    private String mFlexCss;

    public CSSNode getCssNode() {
        if (mCssNode == null) {
            View view = getView();
            if (view instanceof LVViewGroup) {
                LVViewGroup group = (LVViewGroup) view;
                return group.getCssNode();
            } else {
                mCssNode = new CSSNode();
            }
        }

        return mCssNode;
    }

    public UDView setFlexCss(String cssString) {
        if (mFlexCss == null || !mFlexCss.equals(cssString)) {
            CSSNode node = getCssNode();
            FlexboxCSSParser.parseFlexNodeCSS(node, cssString);
            mFlexCss = cssString;
        }

        return this;
    }

    public String getFlexCss() {
        return mFlexCss;
    }

    public UDView(T view, Globals globals, LuaValue metatable, LuaValue initParams) {
        super(view, globals, metatable);
        this.initParams = initParams;
        setSize(0, 0);//默认初始化size全0
    }

    public T getView() {
        return (T) userdata();
    }

    public Context getContext() {
        return getView() != null ? getView().getContext() : null;
    }


    public UDView setInitParams(LuaValue initParams) {
        this.initParams = initParams;
        return this;
    }

    public LuaValue getInitParams() {
        return initParams;
    }

    /**
     * 刷新
     *
     * @return
     */
    public UDView invalidate() {
        final View view = getView();
        if (view != null) {
            view.invalidate();
        }
        return this;
    }

    /**
     * 设置padding
     *
     * @param top
     * @param left
     * @param right
     * @param bottom
     * @return
     */
    public UDView setPadding(int left, int top, int right, int bottom) {
        final View view = getView();
        if (view != null) {
            view.setPadding(left, top, right, bottom);
        }
        return this;
    }

    public int getPaddingLeft() {
        return getView() != null ? getView().getPaddingLeft() : 0;
    }

    public int getPaddingTop() {
        return getView() != null ? getView().getPaddingTop() : 0;
    }

    public int getPaddingRight() {
        return getView() != null ? getView().getPaddingRight() : 0;
    }

    public int getPaddingBottom() {
        return getView() != null ? getView().getPaddingBottom() : 0;
    }

    /**
     * 设置背景颜色 & alpha
     *
     * @param color int
     */
    public UDView setBackgroundColorAndAlpha(int color, float alpha) {
        setBackgroundColor(color);
        setBackgroundAlpha(alpha);
        return this;
    }

    /**
     * 设置view的alpha
     *
     * @param alpha 百分比的alpha 0-1
     */
    public UDView setBackgroundAlpha(final float alpha) {
        final T view = getView();
        if (view != null) {
            final Drawable drawable = view.getBackground() != null ? view.getBackground() : null;
            if (drawable != null) {
                drawable.setAlpha((int) (alpha * 0xFF));
                LuaViewUtil.setBackground(view, drawable);
            }
        }
        return this;
    }

    /**
     * 获取alpha
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public float getBackgroundAlpha() {
        final T view = getView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return (view != null && view.getBackground() != null) ? view.getBackground().getAlpha() / 255.0f : 1;
        }
        return 1;
    }

    /**
     * 设置背景颜色
     *
     * @param color int
     */
    public UDView setBackgroundColor(int color) {
        final View view = getView();
        if (view != null) {
            final LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
            drawable.setColor(color);
            LuaViewUtil.setBackground(view, drawable);
        }
        return this;
    }

    /**
     * 获取background颜色
     *
     * @return
     */
    public int getBackgroundColor() {
        final View view = getView();
        if (view != null) {
            final Drawable drawable = view.getBackground();
            if (drawable instanceof LVGradientDrawable) {
                return ((LVGradientDrawable) drawable).getColor();
            }
        }
        return 0;
    }

    public UDView setCornerRadius(float radius) {
        final T view = getView();
        if (view != null) {
            final LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
            drawable.setCornerRadius(radius);
            LuaViewUtil.setBackground(view, drawable);
        }
        return this;
    }

    public float getCornerRadius() {
        final T view = getView();
        if (view != null) {
            return view.getBackground() instanceof LVGradientDrawable ? ((LVGradientDrawable) view.getBackground()).getCornerRadius() : 0;
        }
        return 0;
    }

    /**
     * 边框宽度
     *
     * @param borderWidth
     * @return
     */
    public UDView setBorderWidth(final int borderWidth) {
        final T view = getView();
        if (view != null) {
            final LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
            drawable.setStrokeWidth(borderWidth);
            LuaViewUtil.setBackground(view, drawable);
        }
        return this;
    }

    public int getBorderWidth() {
        final T view = getView();
        if (view != null) {
            return view.getBackground() instanceof LVGradientDrawable ? ((LVGradientDrawable) view.getBackground()).getStrokeWidth() : 0;
        }
        return 0;
    }

    /**
     * 边框宽度
     *
     * @param borderColor
     * @return
     */
    public UDView setBorderColor(final int borderColor) {
        final T view = getView();
        if (view != null) {
            final LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
            drawable.setStrokeColor(borderColor);
            LuaViewUtil.setBackground(view, drawable);
        }
        return this;
    }

    public int getBorderColor() {
        final T view = getView();
        if (view != null) {
            return view.getBackground() instanceof LVGradientDrawable ? ((LVGradientDrawable) view.getBackground()).getStrokeColor() : 0;
        }
        return 0;
    }


    /**
     * set position of a view
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public UDView setFrame(final int x, final int y, final int width, final int height) {
        final View view = getView();
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = x;
            layoutParams.topMargin = y;
            layoutParams.width = width;
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);

            // flexbox needs know the style
            getCssNode().setStyleWidth(width);
            getCssNode().setStyleHeight(height);

        } else {
            //TODO 其他Layout处理
        }
        return this;
    }

    /**
     * 设置X坐标
     *
     * @param x
     * @return
     */
    public UDView setXY(final int x, final int y) {
        final View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = x;
                layoutParams.topMargin = y;
                view.setLayoutParams(layoutParams);
            } else {
                //TODO 其他Layout处理
            }
        }
        return this;
    }

    /**
     * 设置X坐标
     *
     * @param x
     * @return
     */
    public UDView setX(final int x) {
        final View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = x;
                view.setLayoutParams(layoutParams);
            } else {
                //TODO 其他Layout处理
            }
        }
        return this;
    }

    /**
     * 获取x坐标
     *
     * @return
     */
    public int getX() {
        final View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin;
            } else {
                //TODO 其他Layout处理
            }
        }
        return 0;
    }

    /**
     * 设置Y坐标
     *
     * @param y
     * @return
     */
    public UDView setY(final int y) {
        final View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.topMargin = y;
                view.setLayoutParams(layoutParams);
            } else {
                //TODO 其他Layout处理
            }
        }
        return this;
    }


    /**
     * 获取y坐标
     *
     * @return
     */
    public int getY() {
        final View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                return ((RelativeLayout.LayoutParams) view.getLayoutParams()).topMargin;
            } else {
                //TODO 其他Layout处理
            }
        }
        return 0;
    }

    /**
     * 设置Translation X, Y
     *
     * @param tx
     * @param ty
     * @return
     */
    public UDView setTranslation(Float tx, Float ty) {
        final View view = getView();
        if (view != null) {
            if(tx != null) {
                view.setTranslationX(tx);
            }
            if(ty != null) {
                view.setTranslationY(ty);
            }
        }
        return this;
    }

    public UDView setTranslation(float translationX, float translationY) {
        final View view = getView();
        if (view != null) {
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
        }
        return this;
    }


    /**
     * 获取TranslationX
     *
     * @return
     */
    public float getTranslationX() {
        final View view = getView();
        if (view != null) {
            return view.getTranslationX();
        }
        return 0;
    }

    /**
     * 获取TranslationY
     *
     * @return
     */
    public float getTranslationY() {
        final View view = getView();
        if (view != null) {
            return view.getTranslationY();
        }
        return 0;
    }


    /**
     * 获取宽度
     *
     * @param width
     * @return
     */
    public UDView setWidth(int width) {
        if (width >= 0) {
            final View view = getView();
            if (view != null && view.getLayoutParams() != null) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = width;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }

    /**
     * 获取宽度
     *
     * @return
     */
    public int getWidth() {
        final View view = getView();
        if (view != null && view.getLayoutParams() != null) {
            return view.getLayoutParams().width >= 0 ? view.getLayoutParams().width : view.getWidth();
        }
        return view != null ? view.getWidth() : 0;
    }

    /**
     * 获取宽度
     *
     * @param width
     * @return
     */
    public UDView setMinWidth(int width) {
        if (width >= 0) {
            final View view = getView();
            if (view != null) {
                view.setMinimumWidth(width);
            }
        }
        return this;
    }

    /**
     * 获取宽度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getMinWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return getView() != null ? getView().getMinimumWidth() : 0;
        } else {
            return 0;
        }
    }


    /**
     * 获取高度
     *
     * @param height
     * @return
     */
    public UDView setHeight(int height) {
        if (height > 0) {
            final View view = getView();
            if (view != null && view.getLayoutParams() != null) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = height;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }

    /**
     * 获取高度
     *
     * @return
     */
    public int getHeight() {
        final View view = getView();
        if (view != null && view.getLayoutParams() != null) {
            return view.getLayoutParams().height >= 0 ? view.getLayoutParams().height : view.getHeight();
        }
        return view != null ? view.getHeight() : 0;
    }

    /**
     * 设置view的宽、高
     *
     * @param width
     * @param height
     */
    public UDView setSize(final int width, final int height) {
        if (width >= 0 && height >= 0) {
            final View view = getView();
            if (view != null && view.getLayoutParams() != null) {
                final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }

    /**
     * 设置背景和透明度
     */
    public UDView setBackgroundResourceAndAlpha(String picName, float alpha) {
        setBackgroundResource(picName);
        setBackgroundAlpha(alpha);
        return this;
    }

    /**
     * setBackgroundResource,根据picName找到对应id
     *
     * @param picName String
     */
    public UDView setBackgroundResource(String picName) {
        final View view = getView();
        if (view != null) {
            final int id = view.getResources().getIdentifier(picName, "drawable", view.getContext().getPackageName());
            if (id != 0) {
                view.setBackgroundResource(id);
            }
        }
        return this;
    }

    /**
     * 设置view位于父容器的中心
     *
     * @return
     */
    public UDView align(Integer... rules) {
        final View view = getView();
        if (view != null && view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (rules != null) {
                for (int rule : rules) {
                    layoutParams.addRule(rule);
                }
            }
            view.setLayoutParams(layoutParams);
        }
        return this;
    }

    /**
     * 设置view的中心移动到x,y
     *
     * @param x
     * @param y
     */
    public UDView setCenter(final int x, final int y) {
        return setXY(x - getWidth() / 2, y - getHeight() / 2);
    }

    /**
     * 设置view的中心移动到x
     *
     * @param x
     */
    public UDView setCenterX(final int x) {
        return setX(x - getWidth() / 2);
    }

    /**
     * 设置view的中心移动到y
     *
     * @param y
     */
    public UDView setCenterY(final int y) {
        return setY(y - getHeight() / 2);
    }


    /**
     * 开始动画
     *
     * @return
     */
    public UDView show() {
        final T view = getView();
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 是否显示
     *
     * @return
     */
    public boolean isShow() {
        return getView() != null && getView().getVisibility() == View.VISIBLE;
    }

    /**
     * 隐藏动画
     *
     * @return
     */
    public UDView hide() {
        final T view = getView();
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 是否隐藏
     *
     * @return
     */
    public boolean isHide() {
        return getView() == null || getView().getVisibility() != View.VISIBLE;
    }

    /**
     * 设置是否有效
     *
     * @param enable
     * @return
     */
    public UDView setEnabled(boolean enable) {
        final View view = getView();
        if (view != null) {
            view.setEnabled(enable);
        }
        return this;
    }

    /**
     * 是否有效
     *
     * @return
     */
    public boolean isEnabled() {
        return getView() != null && getView().isEnabled();
    }

    /**
     * 设置view的alpha
     */
    public UDView setAlpha(final float alpha) {
        final View view = getView();
        if (view != null) {
            view.setAlpha(alpha);
        }
        return this;
    }

    /**
     * 获取alpha
     *
     * @return
     */
    public float getAlpha() {
        return getView() != null ? getView().getAlpha() : 1f;
    }

    /**
     * 从父容器中移除
     * <p><strong>Note:</strong> do not invoke this method from
     * draw(Canvas)}, onDraw(Canvas),
     * dispatchDraw(Canvas) or any related method.</p>
     * <p/>
     * 见 SwipeRefreshLayout.OnRefreshListener
     *
     * @return
     */
    public UDView removeFromParent() {
        final View view = getView();
        if (view != null && view.getParent() instanceof ViewGroup) {
            final ViewGroup parent = (ViewGroup) view.getParent();
            LuaViewUtil.removeView(parent, view);
        }
        return this;
    }

    /**
     * 设置锚点
     *
     * @param pivotX
     * @param pivotY
     * @return
     */
    public UDView setPivot(float pivotX, float pivotY) {
        final View view = getView();
        if (view != null) {
            view.setPivotX(pivotX);
            view.setPivotY(pivotY);
        }
        return this;
    }

    /**
     * 获取x锚点
     *
     * @return
     */
    public float getPivotX() {
        return getView() != null ? getView().getPivotX() : 0f;
    }

    /**
     * 获取y锚点
     *
     * @return
     */
    public float getPivotY() {
        return getView() != null ? getView().getPivotY() : 0f;
    }

    /**
     * 旋转并缩放
     *
     * @param rotation
     * @return
     */
    public UDView setRotation(float rotation) {
        final View view = getView();
        if (view != null) {
            view.setRotation(rotation);
        }
        return this;
    }

    public float getRotation() {
        return getView() != null ? getView().getRotation() : 0f;
    }

    /**
     * 旋转并缩放
     *
     * @param rotationX
     * @param rotationY
     * @return
     */
    public UDView setRotationXY(float rotationX, float rotationY) {
        final View view = getView();
        if (view != null) {
            view.setRotationX(rotationX);
            view.setRotationY(rotationY);
        }
        return this;
    }

    public float getRotationX() {
        return getView() != null ? getView().getRotationX() : 0f;
    }

    public float getRotationY() {
        return getView() != null ? getView().getRotationY() : 0f;
    }

    /**
     * 缩放
     *
     * @param scaleX
     * @param scaleY
     * @return
     */
    public UDView setScale(float scaleX, float scaleY) {
        final View view = getView();
        if (view != null) {
            view.setScaleX(scaleX);
            view.setScaleY(scaleY);
        }
        return this;
    }

    public UDView setScaleX(float scaleX) {
        final View view = getView();
        if (view != null) {
            view.setScaleX(scaleX);
        }
        return this;
    }

    public float getScaleX() {
        return getView() != null ? getView().getScaleX() : 0f;
    }

    public UDView setScaleY(float scaleY) {
        final View view = getView();
        if (view != null) {
            view.setScaleY(scaleY);
        }
        return this;
    }

    public float getScaleY() {
        return getView() != null ? getView().getScaleY() : 0f;
    }

    /**
     * set callback
     *
     * @param callbacks 回调集合
     */
    public UDView setCallback(final LuaValue callbacks) {
        this.mCallback = callbacks;
        if (this.mCallback != null) {
            mOnClick = mCallback.isfunction() ? mCallback : LuaUtil.getFunction(mCallback, "onClick", "Click", "OnClick", "click");
            mOnLongClick = mCallback.istable() ? LuaUtil.getFunction(mCallback, "onLongClick", "LongClick", "OnLongClick", "longClick") : null;

            //setup listener
            setOnClickListener();
            setOnLongClickListener();
        }
        return this;
    }

    /**
     * 设置点击事件
     *
     * @param callback
     * @return
     */
    public UDView setOnClickCallback(final LuaValue callback) {
        this.mOnClick = callback;
        setOnClickListener();
        return this;
    }

    public UDView setOnLongClickCallback(final LuaValue callback) {
        this.mOnLongClick = callback;
        setOnLongClickListener();
        return this;
    }

    /**
     * 点击
     *
     * @return
     */
    public LuaValue callOnClick() {
        return LuaUtil.callFunction(this.mOnClick);
    }

    public boolean callOnLongClick() {
        return LuaUtil.callFunction(this.mOnLongClick).optboolean(false);
    }

    /**
     * 点击
     */
    private void setOnClickListener() {
        if (LuaUtil.isValid(this.mOnClick)) {
            final T view = getView();
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callOnClick();
                    }
                });
            }
        }
    }

    /**
     * 长按
     */
    private void setOnLongClickListener() {
        if (LuaUtil.isValid(this.mOnLongClick)) {
            final T view = getView();
            if (view != null) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return callOnLongClick();
                    }
                });
            }
        }
    }

    /**
     * get callback
     *
     * @return
     */

    public LuaValue getCallback() {
        return mCallback != null ? mCallback : LuaValue.NIL;
    }

    public LuaValue getOnClickCallback() {
        return this.mOnClick;
    }

    public LuaValue getOnLongClickCallback() {
        return this.mOnLongClick;
    }

    /**
     * 是否有焦点
     *
     * @return
     */
    public boolean hasFocus() {
        return getView() != null && getView().hasFocus();
    }

    /**
     * 请求焦点
     *
     * @return
     */
    public UDView requestFocus() {
        final View view = getView();
        if (view != null) {
            view.requestFocus();
        }
        return this;
    }

    /**
     * 清空焦点
     *
     * @return
     */
    public UDView clearFocus() {
        final View view = getView();
        if (view != null) {
            view.clearFocus();
        }
        return this;
    }

    /**
     * bring a view to front
     *
     * @return
     */
    public UDView bringToFront() {
        final View view = getView();
        if (view != null) {
            view.bringToFront();
        }
        return this;
    }

    /**
     * 滚动到某个位置
     *
     * @param x
     * @param y
     * @return
     */
    public UDView scrollTo(final int x, final int y) {
        final View view = getView();
        if (view != null) {
            view.scrollTo(x, y);
        }
        return this;
    }

    /**
     * 滚动dx, dy
     *
     * @param dx
     * @param dy
     * @return
     */
    public UDView scrollBy(final int dx, final int dy) {
        final View view = getView();
        if (view != null) {
            view.scrollBy(dx, dy);
        }
        return this;
    }

    /**
     * 获取滚动x
     *
     * @return
     */
    public int getScrollX() {
        return getView() != null ? getView().getScrollX() : 0;
    }

    /**
     * 获取滚动y
     *
     * @return
     */
    public int getScrollY() {
        return getView() != null ? getView().getScrollY() : 0;
    }

    /**
     * 设置横向滚动条
     *
     * @param enabled
     * @return
     */
    public UDView setHorizontalScrollBarEnabled(boolean enabled) {
        final View view = getView();
        if (view != null) {
            view.setHorizontalScrollBarEnabled(enabled);
        }
        return this;
    }

    public boolean isHorizontalScrollBarEnabled() {
        return getView() != null && getView().isHorizontalScrollBarEnabled();
    }

    /**
     * 设置纵向滚动条
     *
     * @param enabled
     * @return
     */
    public UDView setVerticalScrollBarEnabled(boolean enabled) {
        final View view = getView();
        if (view != null) {
            view.setVerticalScrollBarEnabled(enabled);
        }
        return this;
    }

    public boolean isVerticalScrollBarEnabled() {
        return getView() != null && getView().isVerticalScrollBarEnabled();
    }

    /**
     * 调整大小以适应内容
     *
     * @return
     */
    public UDView adjustSize() {
        //TODO 由子类实现
        return this;
    }


    //-------------------------------------------动画相关--------------------------------------------

    /**
     * 开始动画
     * TODO 这里的start／stop需要跟 UDAnimator的保持同步
     *
     * @param animators
     * @return
     */
    public UDView startAnimation(LuaValue[] animators) {
        final View view = getView();
        if (view != null && animators.length > 0) {
            if (isAnimating() == false) {//不在动画中才可以动画
                if (mAnimators != null) {
                    AnimatorUtil.cancel(mAnimators);
                    mAnimators.clear();
                } else {
                    mAnimators = new ArrayList<Animator>();
                }

                for (LuaValue animator : animators) {
                    if (animator instanceof UDAnimator) {
                        final Animator anim = ((UDAnimator) animator).with(this).build();
                        mAnimators.add(anim);
                    }
                }

                startAnimation();
            }
        }
        return this;
    }

    /**
     * 开始动画
     *
     * @return
     */
    public UDView startAnimation() {
        AnimatorUtil.start(mAnimators);
        return this;
    }

    /**
     * 停止动画，回到终点位置
     * 返回是否结束成功
     *
     * @return
     */
    public boolean cancelAnimation() {
        return AnimatorUtil.cancel(mAnimators);
    }

    /**
     * pause animation
     *
     * @return
     */
    public UDView pauseAnimation() {
        AnimatorUtil.pause(mAnimators);
        return this;
    }

    /**
     * resume animation
     *
     * @return
     */
    public UDView resumeAnimation() {
        AnimatorUtil.resume(mAnimators);
        return this;
    }


    /**
     * end and call function
     *
     * @return
     */
    public boolean endAnimation() {
        return AnimatorUtil.end(mAnimators);
    }

    /**
     * 是否暂停
     *
     * @return
     */
    public boolean isAnimationPaused() {
        return AnimatorUtil.isPaused(mAnimators);
    }

    /**
     * 是否在动画
     *
     * @return
     */
    public boolean isAnimating() {
        return AnimatorUtil.isRunning(mAnimators);
    }
}
