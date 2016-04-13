package com.taobao.luaview.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Nonnull(
    when = When.UNKNOWN
)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface Nullable {
}
