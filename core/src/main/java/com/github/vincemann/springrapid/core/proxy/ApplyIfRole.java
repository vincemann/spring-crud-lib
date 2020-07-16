package com.github.vincemann.springrapid.core.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplyIfRole {

    String[] isNot() default {};
    String[] is() default {};

    /**
     * Security Context Authentication is null and there are no required roles, call PluginMethod yes or no?
     */
    boolean allowAnon() default true;
}