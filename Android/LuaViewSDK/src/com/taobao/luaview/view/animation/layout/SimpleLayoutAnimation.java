package com.taobao.luaview.view.animation.layout;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;

/**
 * XXX
 *
 * @author song
 * @date 16/8/23
 * 主要功能描述
 * 修改描述
 * 下午11:25 song XXX
 */
public class SimpleLayoutAnimation {

    private void fun() {
        //通过加载XML动画设置文件来创建一个Animation对象；

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.list_anim);

        //得到一个LayoutAnimationController对象；

        LayoutAnimationController lac = new LayoutAnimationController(animation);

        //设置控件显示的顺序；

        lac.setOrder(LayoutAnimationController.ORDER_REVERSE);

        //设置控件显示间隔时间；

        lac.setDelay(1);

        //为ListView设置LayoutAnimationController属性；

        ListView list;


    }
}
