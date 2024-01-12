package com.capstone.workspace.annotations;

import com.capstone.workspace.enums.user.UserType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedUsers {
    UserType[] userTypes() default {};
}
