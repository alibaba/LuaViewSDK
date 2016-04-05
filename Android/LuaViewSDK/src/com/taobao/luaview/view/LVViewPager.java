package com.taobao.luaview.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewPager;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.adapter.LVPagerAdapter;
import com.taobao.luaview.view.indicator.circle.PageIndicator;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView-ViewPager
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVViewPager extends ViewPager implements ILVViewGroup {
    public Globals mGlobals;
    private LuaValue mInitParams;
    private UDViewPager mLuaUserdata;
    private OnPageChangeListener mOnPageChangeListener;

    public LVViewPager(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.context);
        this.mGlobals = globals;
        this.mInitParams = varargs != null ? varargs.arg1() : null;
        this.mLuaUserdata = new UDViewPager(this, globals, metaTable, this.mInitParams);
        init();
    }

    private void init() {
        LuaViewUtil.setId(this);//TODO 必须设置，且每个ViewPager要有唯一id android.content.res.Resources$NotFoundException: Unable to find resource ID #0xffffffff
        this.mGlobals.saveContainer(this);
        initData();
        this.mGlobals.restoreContainer();
    }

    private void initData() {
        /*if (mGlobals.context instanceof FragmentActivity) {//TODO 这里因为拿不到childFragmentManger，暂时先用普通的Adapter来处理
            FragmentActivity activity = (FragmentActivity) mGlobals.context;
//            this.setAdapter(new LVFragmentPagerAdapter(activity.getSupportFragmentManager(), mGlobals, mLuaUserdata));
            this.setAdapter(new LVFragmentStatePagerAdapter(getFragmentManager(activity), mGlobals, mLuaUserdata));
        } else {
            this.setAdapter(new LVPagerAdapter(mGlobals, mLuaUserdata));
        }*/
        this.setAdapter(new LVPagerAdapter(mGlobals, mLuaUserdata));
        this.setCurrentItem(0);//TODO 可以定制
        initOnPageChangeListener();//初始化页面监听
    }

    public void initOnPageChangeListener() {
        mOnPageChangeListener = createOnPageChangeListener(this);
        this.setOnPageChangeListener(mOnPageChangeListener);
    }

    public void setViewPagerIndicator(LuaValue indicator) {
        if (indicator instanceof UDView && ((UDView) indicator).getView() instanceof PageIndicator) {
            final PageIndicator pageIndicator = (PageIndicator) ((UDView) indicator).getView();
            pageIndicator.setViewPager(this);
            pageIndicator.setOnPageChangeListener(mOnPageChangeListener);
        }
    }

    /**
     * get fragment manager
     *
     * @param fragmentActivity
     * @return
     */
    private FragmentManager getFragmentManager(FragmentActivity fragmentActivity) {
        Fragment fragment = null;
        if (fragmentActivity != null && fragmentActivity.getSupportFragmentManager() != null && fragmentActivity.getSupportFragmentManager().getFragments() != null && fragmentActivity.getSupportFragmentManager().getFragments().size() > 0) {
            fragment = fragmentActivity.getSupportFragmentManager().getFragments().get(0);
        }
        if (fragment != null && fragment.getParentFragment() != null) {//必须fragment嵌套fragment才取ChildFragment
            return fragment.getChildFragmentManager();
        }

        return fragmentActivity != null ? fragmentActivity.getSupportFragmentManager() : null;
    }

    /**
     * 创建页面移动listener
     *
     * @return
     */
    private OnPageChangeListener createOnPageChangeListener(final ViewPager viewPager) {
        if (this.mLuaUserdata.hasPageChangeListeners()) {
            return new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    mLuaUserdata.callPageCallbackScrolling(position, positionOffset, DimenUtil.pxToDpi(positionOffsetPixels));
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    switch (state) {
                        case ViewPager.SCROLL_STATE_DRAGGING:
                            mLuaUserdata.callPageCallbackScrollBegin(viewPager != null ? viewPager.getCurrentItem() : 0);
                            break;
                        case ViewPager.SCROLL_STATE_IDLE:
                            mLuaUserdata.callPageCallbackScrollEnd(viewPager != null ? viewPager.getCurrentItem() : 0);
                            break;
                        case ViewPager.SCROLL_STATE_SETTLING:
                            break;
                    }
                    mLuaUserdata.callPageCallbackStateChanged(state);
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void addLVView(View view, Varargs a) {
        this.addView(view);
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }

    //-----------------------------------View Pager Touch事件---------------------------------------

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_SCROLL) {
            super.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }*/

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = super.onInterceptTouchEvent(ev);
        if (ret)
            getParent().requestDisallowInterceptTouchEvent(true);
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean ret = super.onTouchEvent(ev);
        if (ret)
            getParent().requestDisallowInterceptTouchEvent(true);
        return ret;
    }*/

}
