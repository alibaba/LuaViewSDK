/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.webkit.URLUtil;

import com.taobao.luaview.userdata.base.BaseCacheUserdata;
import com.taobao.luaview.util.AssetUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Audio 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
public class UDAudio extends BaseCacheUserdata implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mMediaPlayer;
    private String mUriOrName;
    private Integer mLoopTimes;
    private LuaFunction mCallback;

    public UDAudio(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable, varargs);
        init();
    }

    private void init() {
        if (initParams != null) {
            mUriOrName = LuaUtil.getString(initParams, 1);
            mLoopTimes = LuaUtil.getInt(initParams, 2, 1);
            mCallback = LuaUtil.getFunction(initParams, 3, 2, 1);
        }
    }

    public UDAudio setCallback(LuaFunction callback) {
        this.mCallback = callback;
        return this;
    }

    public LuaFunction getCallback() {
        return mCallback;
    }

    /**
     * create a media player
     *
     * @return
     */
    private synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                try {
                    final Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
                    final Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
                    final Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
                    final Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");

                    final Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});

                    final Object subtitleInstance = constructor.newInstance(getContext(), null, null);

                    final Field f = cSubtitleController.getDeclaredField("mHandler");

                    f.setAccessible(true);
                    try {
                        f.set(subtitleInstance, new Handler());
                    } catch (IllegalAccessException e) {
                        return mMediaPlayer;
                    } finally {
                        f.setAccessible(false);
                    }

                    final Method setSubtitleAnchor = mMediaPlayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);
                    setSubtitleAnchor.invoke(mMediaPlayer, subtitleInstance, null);
                } catch (Exception e) {
                }
            }
        }
        return mMediaPlayer;
    }

    /**
     * start playing audio
     *
     * @param uriOrName
     * @param loopTimes
     * @return
     */
    public synchronized UDAudio play(String uriOrName, Integer loopTimes) {
        stopAndReset();

        if (uriOrName != null && uriOrName.equals(this.mUriOrName) == false) {//url 不同
            this.mUriOrName = uriOrName;
        }

        if (loopTimes != null) {
            this.mLoopTimes = loopTimes;
        }

        if (this.mUriOrName != null) {
            final MediaPlayer player = getMediaPlayer();
            if (player != null && player.isPlaying() == false) {
                String uri = null;
                boolean assetFileExist = false;
                if (URLUtil.isNetworkUrl(this.mUriOrName) || URLUtil.isFileUrl(this.mUriOrName) || URLUtil.isAssetUrl(this.mUriOrName)) {//net & file & asset
                    uri = this.mUriOrName;
                } else {//plain text, use as file path
                    uri = getLuaResourceFinder().buildFullPathInBundleOrAssets(this.mUriOrName);
                    assetFileExist = AssetUtil.exists(getContext(), uri);
                }
                try {
                    if (assetFileExist) {
                        final AssetFileDescriptor descriptor = getContext().getAssets().openFd(uri);
                        player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    } else {
                        player.setDataSource(uri);
                    }
                    player.setOnErrorListener(this);
                    player.setOnCompletionListener(this);
                    player.setOnPreparedListener(this);
                    player.setLooping((this.mLoopTimes != null && this.mLoopTimes > 1) ? true : false);
                    player.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return this;
    }

    /**
     * pause playing audio
     *
     * @return
     */
    public synchronized UDAudio pause() {
        final MediaPlayer player = getMediaPlayer();
        if (player != null) {
            try {
                player.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * resume playing
     *
     * @return
     */
    public synchronized UDAudio resume() {
        final MediaPlayer player = getMediaPlayer();
        if (player != null && player.isPlaying() == false) {
            try {
                if (player.getCurrentPosition() > 0) {
                    player.start();
                } else {
                    play(mUriOrName, mLoopTimes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * seek to position
     *
     * @param position
     * @return
     */
    public synchronized UDAudio seekTo(Integer position) {
        if (position != null) {
            final MediaPlayer player = getMediaPlayer();
            if (player != null) {
                try {
                    player.seekTo(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }


    /**
     * stop playing audio
     *
     * @return
     */
    public synchronized UDAudio stop() {
        final MediaPlayer player = getMediaPlayer();
        if (player != null && player.isPlaying()) {
            try {
                player.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * reset from error state
     *
     * @return
     */
    public synchronized UDAudio reset() {
        final MediaPlayer player = getMediaPlayer();
        if (player != null) {
            try {
                player.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * stop playing and reset error state
     *
     * @return
     */
    public synchronized UDAudio stopAndReset() {
        return stop().reset();
    }

    /**
     * stop playing and release
     *
     * @return
     */
    public synchronized UDAudio stopAndRelease() {
        return stop().release();
    }

    /**
     * release resource
     *
     * @return
     */
    public synchronized UDAudio release() {
        final MediaPlayer player = getMediaPlayer();
        if (player != null) {
            try {
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public boolean isPlaying() {
        return getMediaPlayer() != null && getMediaPlayer().isPlaying();
    }

    public boolean isLooping() {
        return getMediaPlayer() != null && getMediaPlayer().isLooping();
    }

    public boolean isPause() {
        return getMediaPlayer() != null && getMediaPlayer().isPlaying() == false && getMediaPlayer().getCurrentPosition() > 0;
    }

    @Override
    public void onCacheClear() {
        release();
    }


    //-----------------------------------------listeners--------------------------------------------

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp != null) {
            synchronized (mp) {
                try {
                    if (mp.isPlaying() == false) {
                        mp.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp != null) {
            synchronized (mp) {
                try {
                    mp.stop();
                    mp.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (mCallback != null) {
            LuaUtil.callFunction(LuaUtil.getFunction(mCallback, "onComplete", "OnComplete"));
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mCallback != null) {
            LuaUtil.callFunction(mCallback.get("onError"), valueOf(what));
        }
        return false;
    }
}
