/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.demo.fragment;

import android.os.Bundle;

import com.taobao.luaview.demo.activity.LuaViewBridge;
import com.taobao.luaview.demo.ui.CustomError;
import com.taobao.luaview.demo.ui.CustomLoading;
import com.taobao.luaview.fragment.LuaViewFragment;
import com.taobao.luaview.global.LuaView;

/**
 * @author song
 * @date 15/11/11
 */
public class DemoLuaViewFragment extends LuaViewFragment {

    /**
     * create a new lua view fragment
     *
     * @param bundle
     * @return
     */
    public static DemoLuaViewFragment newInstance(Bundle bundle) {
        DemoLuaViewFragment fragment = new DemoLuaViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void registerNameBeforeLoad(LuaView luaView) {
        super.registerNameBeforeLoad(luaView);
        luaView.registerPanel(CustomError.class);
        luaView.registerPanel(CustomLoading.class);
        luaView.register("viewController", new LuaViewBridge(getActivity()));
    }

    @Override
    public void load(final LuaView luaView) {
        luaView.load("testButton");
    }
}
