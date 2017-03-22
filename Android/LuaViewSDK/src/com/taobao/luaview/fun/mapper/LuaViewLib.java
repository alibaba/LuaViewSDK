/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * LuaView UI Lib 标识
 *
 * @author song
 * @date 15/12/25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LuaViewLib {
    /**
     * api 修订版本，标示该API修订过，之前可能有bug，或者只是空实现。
     * 字符串，默认为空。
     * 每次新增两个字符串：第一个标示版本，第二句标示修订内容。
     *
     * @return
     */
    String[] revisions() default {};
}
