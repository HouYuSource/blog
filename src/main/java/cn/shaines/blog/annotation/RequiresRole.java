package cn.shaines.blog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 角色注解
 * @date: created in 2019-05-08 16:22:51
 * @author: houyu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {

    String value() default "USER";

    String[] roles() default { "ADMIN" };

}
