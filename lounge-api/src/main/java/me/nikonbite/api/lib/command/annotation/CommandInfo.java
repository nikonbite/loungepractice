package me.nikonbite.api.lib.command.annotation;

import me.nikonbite.api.lib.user.group.Group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String[] name();
    Group group() default Group.DEFAULT;
}