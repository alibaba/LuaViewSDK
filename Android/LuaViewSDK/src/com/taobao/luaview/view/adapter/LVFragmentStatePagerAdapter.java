/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.taobao.luaview.fragment.LVPageFragment;
import com.taobao.luaview.userdata.ui.UDViewPager;

import org.luaj.vm2.Globals;

/**
 * 支持Fragment的 pager adapter
 *
 * @author song
 * @date 15/9/17
 */
public class LVFragmentStatePagerAdapter extends FixedFragmentStatePagerAdapter {
    private static final String TAG = "LVFragmentStatePagerAdapter";
    private UDViewPager mInitProps;
    private Globals mGlobals;

    private FragmentManager mFragmentManager;

    public LVFragmentStatePagerAdapter(FragmentManager fm, Globals globals, UDViewPager udViewPager) {
        super(fm);
        this.mGlobals = globals;
        this.mInitProps = udViewPager;
        this.mFragmentManager = fm;
    }

    @Override
    public int getCount() {
        return this.mInitProps.getPageCount();
    }

    @Override
    public Fragment getItem(int position) {
        return new LVPageFragment(this.mGlobals, this.mInitProps, position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = null;
        if (mFragmentManager != null) {
            fragment = mFragmentManager.findFragmentByTag(TAG + position);
        }

        if (fragment == null) {
            fragment = (Fragment) super.instantiateItem(container, position);
        }
        return fragment;
    }
}
