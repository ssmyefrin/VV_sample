package com.example.sample.global.common.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CurrentUserId
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(hidden = true)
@AuthenticationPrincipal(expression = "username")
public @interface CurrentUserId {
}