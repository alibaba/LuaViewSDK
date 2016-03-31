package com.taobao.luaview.demo.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.csslayout.CSSNode;
import com.taobao.android.luaview.demo.R;
import com.taobao.luaview.layout.FlexboxLayout;
import com.taobao.luaview.layout.FlexboxNodeView;

import java.util.ArrayList;
import java.util.Arrays;

public class FlexboxDemoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexbox_demo);

        testFlexbox();
    }

    private void testFlexbox() {
        FlexboxLayout flxLayout = new FlexboxLayout(this);
        flxLayout.setBackgroundColor(Color.parseColor("#ffd315"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, 800);
        params.setMargins(100, 100, 0, 0);
        flxLayout.setLayoutParams(params);

        View view1 = new View(this);
        view1.setBackgroundColor(Color.GREEN);
        CSSNode node1 = new com.facebook.csslayout.CSSNode();
        node1.setStyleWidth(233);
        node1.setStyleHeight(100);
        FlexboxNodeView nodeView1 = new FlexboxNodeView(view1, node1);

        View view2 = new View(this);
        view2.setBackgroundColor(Color.BLUE);
        CSSNode node2 = new com.facebook.csslayout.CSSNode();
        node2.setFlex(1);
        FlexboxNodeView nodeView2 = new FlexboxNodeView(view2, node2);

        View view3 = new View(this);
        view3.setBackgroundColor(Color.RED);
        CSSNode node3 = new com.facebook.csslayout.CSSNode();
        node3.setFlex(1);
        FlexboxNodeView nodeView3 = new FlexboxNodeView(view3, node3);

        TextView text = new TextView(this);
        text.setText("luvview测试的text");
        text.setBackgroundColor(Color.CYAN);
        CSSNode textNode = new com.facebook.csslayout.CSSNode();
        textNode.setSizeToFit(true);
        FlexboxNodeView nodeTextView = new FlexboxNodeView(text, textNode);

        ArrayList<FlexboxNodeView> childNodeViews = new ArrayList<FlexboxNodeView>(
                Arrays.asList(
                        nodeView1,
                        nodeView2,
                        nodeView3,
                        nodeTextView
                ));
        flxLayout.setChildNodeViews(childNodeViews);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(flxLayout);
    }
}
