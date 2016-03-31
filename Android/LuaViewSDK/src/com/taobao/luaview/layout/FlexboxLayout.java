package com.taobao.luaview.layout;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.facebook.csslayout.CSSLayoutContext;
import com.facebook.csslayout.CSSNode;

import java.util.ArrayList;

/**
 * Created by xiekaiwei on 16/3/14.
 */
public class FlexboxLayout extends RelativeLayout {
    private CSSLayoutContext mLayoutContext;
    private ArrayList<FlexboxNodeView> mChildNodeViews;

    public CSSNode flexNode;

    public FlexboxLayout(Context context) {
        super(context);

        flexNode = new CSSNode();
        mLayoutContext = new CSSLayoutContext();
    }

    public void setChildNodeViews(ArrayList<FlexboxNodeView> childNodeViews) {
        // diff old and new
        if (mChildNodeViews == childNodeViews) { return; }

        // remove all the old views
        clearChildNodeViews();

        // set the new nodes
        mChildNodeViews = childNodeViews;

        // enum array and add into it
        generateNodeViewTree();
    }

    private void clearChildNodeViews() {
        if (mChildNodeViews == null) { return; }

        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            FlexboxNodeView nodeView = mChildNodeViews.get(i);
            removeView(nodeView.view);
        }

        flexNode.resetChildren();
        flexNode.reset();
    }

    private void generateNodeViewTree() {
        if (mChildNodeViews == null) { return; }

        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            FlexboxNodeView nodeView = mChildNodeViews.get(i);

            addView(nodeView.view);
            flexNode.addChild(nodeView.node);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.d("FlexLayout", "onMeasure: ");

        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            FlexboxNodeView nodeView = mChildNodeViews.get(i);
            View view = nodeView.view;
            CSSNode node = nodeView.node;

            if (node.getSizeToFit()) {
                measureChild(view, widthMeasureSpec, heightMeasureSpec);

                node.setNoDirtyStyleWidth(view.getMeasuredWidth());
                node.setNoDirtyStyleHeight(view.getMeasuredHeight());
            }
        }

        flexNode.calculateLayout(mLayoutContext);
        assginNodeLayoutParams();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        ViewGroup.LayoutParams oldParam = getLayoutParams();

        boolean isNotSameAsOld = oldParam != null && (oldParam.width != params.width || oldParam.height != params.height);
        if (isNotSameAsOld || oldParam == null) {
            flexNode.setStyleHeight(params.height);
            flexNode.setStyleWidth(params.width);
        }

        super.setLayoutParams(params);
    }

    private void assginNodeLayoutParams() {
        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            FlexboxNodeView nodeView = mChildNodeViews.get(i);
            View view = nodeView.view;
            CSSNode node = nodeView.node;

            if (view != null && node != null) {
                int x = (int) node.getLayoutX();
                int y = (int) node.getLayoutY();
                int width = (int) node.getLayoutWidth();
                int height = (int) node.getLayoutHeight();

                RelativeLayout.LayoutParams lParams = (LayoutParams) view.getLayoutParams();
                if (lParams == null) {
                    lParams = new RelativeLayout.LayoutParams(width, height);
                } else {
                    lParams.width = width;
                    lParams.height = height;
                }
                Log.i("FlexLayout", "w: " + width + ", h: " + height);
                lParams.setMargins(x, y, 0, 0);
                view.setLayoutParams(lParams);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d("FlexLayout", "onLayout: ");
    }
}

