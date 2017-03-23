/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.LuaView;

/**
 * LuaView Fragment
 *
 * @author song
 * @date 15/9/22
 */
public class LuaViewFragment extends Fragment {
    private LuaView mLuaView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLuaView = LuaView.create(getActivity());
        if (mLuaView != null) {
            registerNameBeforeLoad(mLuaView);
            load(mLuaView);
        }
        return mLuaView;
    }

    /**
     * 注册接口，注册各种脚本，panel
     */
    public void registerNameBeforeLoad(final LuaView luaView) {
    }

    /**
     * 加载数据
     */
    public void load(final LuaView luaView) {
//        luaView.loadFile(getLuaUri());
        luaView.load(getLuaUri());
    }

    /**
     * 获取文件名称
     *
     * @return
     */
    private String getLuaUri() {
        if (getActivity() != null && getActivity().getIntent() != null && getActivity().getIntent().hasExtra(Constants.PARAM_URI)) {
            String uri = getActivity().getIntent().getStringExtra(Constants.PARAM_URI);
            return uri;
        }
        return null;
    }

    public LuaView getLuaView() {
        return mLuaView;
    }
}
