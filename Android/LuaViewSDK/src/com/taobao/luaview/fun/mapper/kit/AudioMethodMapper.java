/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.kit;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.kit.UDAudio;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Audio 接口封装，声音处理
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class AudioMethodMapper<U extends UDAudio> extends BaseMethodMapper<U> {
    private static final String TAG = "AudioMethodMapper";
    private static final String[] sMethods = new String[]{
            "play",//0
            "pause",//1
            "resume",//2
            "stop",//3
            "seekTo",//4
            "callback",//5
            "playing",//6
            "pausing",//7
            "looping"//8
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return play(target, varargs);
            case 1:
                return pause(target, varargs);
            case 2:
                return resume(target, varargs);
            case 3:
                return stop(target, varargs);
            case 4:
                return seekTo(target, varargs);
            case 5:
                return callback(target, varargs);
            case 6:
                return playing(target, varargs);
            case 7:
                return pausing(target, varargs);
            case 8:
                return looping(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    /**
     * play music
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue play(U audio, Varargs varargs) {
        final String uriOrName = LuaUtil.getString(varargs, 2);
        final Integer loopTimes = LuaUtil.getInt(varargs, 2, 3);
        return audio.play(uriOrName, loopTimes);
    }

    /**
     * pause playing
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue pause(U audio, Varargs varargs) {
        return audio.pause();
    }


    /**
     * resume playing
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue resume(U audio, Varargs varargs) {
        return audio.resume();
    }

    /**
     * stop playing
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue stop(U audio, Varargs varargs) {
        return audio.stop();
    }

    /**
     * 到某个位置
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue seekTo(U audio, Varargs varargs) {
        final Integer msec = LuaUtil.getInt(varargs, 2);
        return audio.seekTo(msec);
    }

    /**
     * 回调
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue callback(U audio, Varargs varargs) {
        if (varargs.narg() > 1) {
            return setCallback(audio, varargs);
        } else {
            return getCallback(audio, varargs);
        }
    }

    public LuaValue setCallback(U audio, Varargs varargs) {
        final LuaFunction callback = LuaUtil.getFunction(varargs, 2);
        return audio.setCallback(callback);
    }

    public LuaValue getCallback(U audio, Varargs varargs) {
        return audio.getCallback();
    }


    /**
     * 是否播放
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue playing(U audio, Varargs varargs) {
        return valueOf(audio.isPlaying());
    }

    /**
     * 是否暂停
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue pausing(U audio, Varargs varargs) {
        return valueOf(audio.isPause());
    }

    /**
     * 是否循环
     *
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue looping(U audio, Varargs varargs) {
        return valueOf(audio.isLooping());
    }

}
