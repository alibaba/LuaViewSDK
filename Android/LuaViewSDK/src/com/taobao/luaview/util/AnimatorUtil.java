/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.animation.Animator;
import android.os.Build;

import java.util.List;

/**
 * 动画相关
 *
 * @author song
 * @date 15/9/8
 */
public class AnimatorUtil {

    /**
     * 启动所有
     *
     * @param animators
     * @return
     */
    public static boolean start(List<Animator> animators) {
        boolean isStartCalled = false;
        if (animators != null) {
            for (Animator animator : animators) {
                if (start(animator)) {
                    isStartCalled = true;
                }
            }
        }
        return isStartCalled;
    }

    /**
     * 启动
     *
     * @param animator
     */
    public static boolean start(Animator animator) {
        if (animator != null && animator.isStarted() == false) {
            animator.setupStartValues();
            animator.start();
            return true;
        }
        return false;
    }

    /**
     * cancel
     *
     * @param animators
     * @return
     */
    public static boolean cancel(List<Animator> animators) {
        boolean isCancelCalled = false;
        if (animators != null) {
            for (Animator animator : animators) {
                if (cancel(animator)) {
                    isCancelCalled = true;
                }
            }
        }
        return isCancelCalled;
    }

    /**
     * cancel
     *
     * @param animator
     * @return
     */
    public static boolean cancel(Animator animator) {
        if (animator != null) {
            animator.cancel();
            return true;
        }
        return false;
    }

    /**
     * end a list of animator
     *
     * @param animators
     * @return
     */
    public static boolean end(List<Animator> animators) {
        boolean isEndCalled = false;
        if (animators != null) {
            for (Animator animator : animators) {
                if (end(animator)) {
                    isEndCalled = true;
                }
            }
        }
        return isEndCalled;
    }

    /**
     * call function and end
     *
     * @param animator
     * @return
     */
    public static boolean end(Animator animator) {
        if (animator != null && animator.isStarted()) {
            animator.end();
            return true;
        }
        return false;
    }

    /**
     * 是否暂停
     *
     * @param animators
     * @return
     */
    public static boolean isPaused(List<Animator> animators) {
        if (animators != null && animators.size() > 0) {
            boolean isAllPaused = true;
            for (Animator animator : animators) {
                if (isPaused(animator) == false) {
                    isAllPaused = false;
                }
            }
            return isAllPaused;
        }
        return false;
    }

    /**
     * 是否暂停
     *
     * @param animator
     * @return
     */
    public static boolean isPaused(Animator animator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return animator != null && animator.isPaused();
        }
        return false;
    }

    /**
     * 是否运行
     *
     * @param animators
     * @return
     */
    public static boolean isRunning(List<Animator> animators) {
        if (animators != null) {
            for (Animator animator : animators) {
                if (isRunning(animator)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否运行
     *
     * @param animator
     * @return
     */
    public static boolean isRunning(Animator animator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LogUtil.d(animator.isStarted(), animator.isPaused(), animator.isRunning());
        }
        return animator != null && animator.isRunning();
    }


    /**
     * pause animation
     *
     * @param animators
     * @return
     */
    public static boolean pause(List<Animator> animators) {
        boolean isPauseCalled = false;
        if (animators != null) {
            for (Animator animator : animators) {
                if (pause(animator)) {
                    isPauseCalled = true;
                }
            }
        }
        return isPauseCalled;
    }

    /**
     * pause
     *
     * @param animator
     */
    public static boolean pause(Animator animator) {
        if (animator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!animator.isPaused()) {
                    animator.pause();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * resume animator
     *
     * @param animators
     * @return
     */
    public static boolean resume(List<Animator> animators) {
        boolean isResumeCalled = false;
        if (animators != null) {
            for (Animator animator : animators) {
                if (resume(animator)) {
                    isResumeCalled = true;
                }
            }
        }
        return isResumeCalled;
    }

    /**
     * resume
     *
     * @param animator
     */
    public static boolean resume(Animator animator) {
        if (animator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (animator.isPaused()) {
                    animator.resume();
                    return true;
                }
            }
        }
        return false;
    }
}
