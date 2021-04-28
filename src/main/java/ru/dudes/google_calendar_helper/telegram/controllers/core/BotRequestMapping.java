package ru.dudes.google_calendar_helper.telegram.controllers.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BotRequestMapping {
    String[] value() default {};
    BotRequestMethod[] method() default {BotRequestMethod.MSG};
}