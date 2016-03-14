package com.taobao.luaview.fun.mapper.kit;

import com.taobao.luaview.fun.base.BaseMethodMapper;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.kit.UDAudio;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Audio 接口封装，声音处理
 *
 * @author song
 * @date 15/8/21
 */
@LuaViewLib
public class AudioMethodMapper<U extends UDAudio> extends BaseMethodMapper<U> {

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
     * @param audio
     * @param varargs
     * @return
     */
    public LuaValue seekTo(U audio, Varargs varargs){
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

    public LuaValue getCallback(U audio, Varargs varargs){
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
