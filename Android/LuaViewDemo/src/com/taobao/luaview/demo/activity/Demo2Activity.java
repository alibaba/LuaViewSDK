/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.demo.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.taobao.android.luaview.demo.R;

public class Demo2Activity extends Activity {

    private ViewGroup mContainer = null;
    private View child = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        test0();

//        test();
    }

    private void test0() {
        setContentView(R.layout.activity_main2);

        mContainer = (ViewGroup) this.findViewById(R.id.container);
        child = this.findViewById(R.id.child);

        child.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                child.animate().translationY(-200).setDuration(2000).start();
            }
        });
    }

    private void test() {
        mContainer = new RelativeLayout(this);

        mContainer.setBackgroundColor(Color.RED);
        mContainer.setAlpha(0.5f);

        child = new RelativeLayout(this);

        child.setBackgroundColor(Color.BLUE);
        child.setAlpha(0.5f);


        mContainer.addView(child, new ViewGroup.MarginLayoutParams(300, 300));

        ((ViewGroup.MarginLayoutParams)child.getLayoutParams()).leftMargin = 0;
        ((ViewGroup.MarginLayoutParams)child.getLayoutParams()).topMargin = 200;


        child.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                child.animate().translationY(-200).setDuration(2000).start();
            }
        });

        setContentView(mContainer, new ViewGroup.MarginLayoutParams(300, 300));

        ((ViewGroup.MarginLayoutParams)mContainer.getLayoutParams()).leftMargin = 200;
        ((ViewGroup.MarginLayoutParams)mContainer.getLayoutParams()).topMargin = 200;
    }
}
